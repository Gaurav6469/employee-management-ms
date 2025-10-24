package com.bfsi.citibank.employeemanagement.service.impl;

import com.bfsi.citibank.employeemanagement.dto.EmployeeRequest;
import com.bfsi.citibank.employeemanagement.entity.Employee;
import com.bfsi.citibank.employeemanagement.exception.EmployeeAlreadyExistsException;
import com.bfsi.citibank.employeemanagement.exception.EmployeeNotFoundException;
import com.bfsi.citibank.employeemanagement.exception.EmployeeUnderageException;
import com.bfsi.citibank.employeemanagement.repository.EmployeeRepository;
import com.bfsi.citibank.employeemanagement.service.EmployeeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    private static final Logger logger = LoggerFactory.getLogger(EmployeeServiceImpl.class);

    private final EmployeeRepository repository;

    public EmployeeServiceImpl(EmployeeRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional
    public Employee create(EmployeeRequest request) {
        logger.info("create() called with firstName='{}', lastName='{}', dob='{}', doj='{}'",
                request.getFirstName(), request.getLastName(), request.getDob(), request.getDoj());

        // check duplicate by firstName, lastName, dob
        boolean exists = repository.existsByFirstNameAndLastNameAndDob(request.getFirstName().trim(), request.getLastName().trim(), request.getDob());
        logger.debug("Duplicate check result = {} for {} {} {}", exists, request.getFirstName(), request.getLastName(), request.getDob());
        if (exists) {
            logger.warn("Attempt to create duplicate employee: {} {} dob={}", request.getFirstName(), request.getLastName(), request.getDob());
            throw new EmployeeAlreadyExistsException("Employee already exists with same firstName, lastName and DOB");
        }
        //generating the employeeno along with the checking the age
        Employee newSaved = generateEmployeeNo(request);
        logger.debug("Generated employee object (pre-final-save) id={} employeeNo={}", newSaved.getId(), newSaved.getEmployeeNo());
        // update with employeeNo
        Employee finalSaved = repository.save(newSaved);
        logger.info("Employee created with id={} employeeNo={}", finalSaved.getId(), finalSaved.getEmployeeNo());
        return finalSaved;
    }


    @Override
    @Transactional
    public Employee update(Long id, EmployeeRequest request) {
        logger.info("update() called for id={} with payload firstName='{}', lastName='{}', dob='{}', doj='{}', salary={}",
                id, request.getFirstName(), request.getLastName(), request.getDob(), request.getDoj(), request.getSalary());

        return repository.findById(id).map(e -> {
            logger.debug("Found existing employee id={} employeeNo={}", e.getId(), e.getEmployeeNo());
            e.setFirstName(request.getFirstName().trim());
            e.setLastName(request.getLastName().trim());
            e.setDob(request.getDob());
            e.setDoj(request.getDoj());
            e.setSalary(request.getSalary());
            // Note: EmployeeNo left unchanged for simplicity. You could regenerate if needed.
            Employee saved = repository.save(e);
            logger.info("Employee updated id={} employeeNo={}", saved.getId(), saved.getEmployeeNo());
            return saved;
        }).orElseThrow(() -> {
            logger.warn("Attempt to update non-existing employee id={}", id);
            return new EmployeeNotFoundException("Employee not found: " + id);
        });
    }

    @Override
    public Employee getById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new EmployeeNotFoundException("Employee not found with ID: " + id));
    }


    @Override
    public List<Employee> getAll() {
        logger.debug("getAll() called");
        List<Employee> all = repository.findAll();
        logger.info("getAll() returned {} employees", all.size());
        return all;
    }

    @Override
    public void delete(Long id) {
        logger.info("delete() called for id={}", id);
        if (!repository.existsById(id)) {
            logger.warn("Attempt to delete non-existing employee id={}", id);
            throw new EmployeeNotFoundException("Employee not found: " + id);
        }
        repository.deleteById(id);
        logger.info("Employee deleted id={}", id);
    }

    private Employee generateEmployeeNo(EmployeeRequest request){
        logger.debug("generateEmployeeNo() called for {} {}", request.getFirstName(), request.getLastName());
        // check age >= 18 at DOJ
        LocalDate dob = request.getDob();
        LocalDate doj = request.getDoj();
        int ageAtDoj = Period.between(dob, doj).getYears();
        logger.debug("Computed age at DOJ = {} (dob={}, doj={})", ageAtDoj, dob, doj);
        if (ageAtDoj < 18) {
            logger.warn("Employee underage: ageAtDoj={} for dob={} doj={}", ageAtDoj, dob, doj);
            throw new EmployeeUnderageException("Employee must be at least 18 years old at date of joining");
        }
        Employee emp = new Employee();
        emp.setFirstName(request.getFirstName().trim());
        emp.setLastName(request.getLastName().trim());
        emp.setDob(dob);
        emp.setDoj(doj);
        emp.setSalary(request.getSalary());

        // Save first to get generated id
        Employee saved = repository.save(emp);
        logger.debug("Saved temporary employee to generate id: id={}", saved.getId());

        // Generate EmployeeNo: MMddyyyy + zero-padded 5-digit id + first2 of firstName + first2 of lastName
        String datePart = doj.format(DateTimeFormatter.ofPattern("MMddyyyy"));
        String idPart = String.format("%05d", saved.getId());
        String fn = saved.getFirstName().length() >= 2 ? saved.getFirstName().substring(0,2).toUpperCase() : saved.getFirstName().toUpperCase();
        String ln = saved.getLastName().length() >= 2 ? saved.getLastName().substring(0,2).toUpperCase() : saved.getLastName().toUpperCase();
        String empNo = datePart + idPart + fn + ln;
        saved.setEmployeeNo(empNo);
        logger.debug("Generated employeeNo='{}' for id={}", empNo, saved.getId());
        return saved;
    }
}
