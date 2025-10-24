package com.bfsi.citibank.employeemanagement.controller;

import com.bfsi.citibank.employeemanagement.dto.EmployeeRequest;
import com.bfsi.citibank.employeemanagement.dto.EmployeeResponse;
import com.bfsi.citibank.employeemanagement.entity.Employee;
import com.bfsi.citibank.employeemanagement.service.EmployeeService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

    private static final Logger log = LoggerFactory.getLogger(EmployeeController.class);

    private final EmployeeService service;

    public EmployeeController(EmployeeService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<EmployeeResponse> createEmployee(@Valid @RequestBody EmployeeRequest request) {
        log.info("Received request to create employee: {}", request);
        Employee created = service.create(request);
        log.debug("Employee created successfully with ID: {}", created.getId());
        return ResponseEntity.created(URI.create("/api/employees/" + created.getId())).body(toResponse(created));
    }

    @GetMapping("/{id}")
    public ResponseEntity<EmployeeResponse> getEmployeeById(@PathVariable Long id) {
        log.info("Fetching employee with ID: {}", id);
        Employee e = service.getById(id);
        log.debug("Employee found: {}", e);
        return ResponseEntity.ok(toResponse(e));
    }


    @GetMapping
    public List<EmployeeResponse> getAllEmployee() {
        log.info("Fetching all employees");
        List<EmployeeResponse> responses = service.getAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        log.debug("Total employees found: {}", responses.size());
        return responses;
    }

    @PutMapping("/{id}")
    public ResponseEntity<EmployeeResponse> updateEmployee(
            @PathVariable Long id,
            @Valid @RequestBody EmployeeRequest request) {

        log.info("Updating employee with ID: {}", id);
        Employee updated = service.update(id, request);
        log.debug("Employee with ID {} updated successfully", id);
        return ResponseEntity.ok(toResponse(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEmployeeById(@PathVariable Long id) {
        log.info("Deleting employee with ID: {}", id);
        service.delete(id);
        log.debug("Employee with ID {} deleted successfully", id);
        return ResponseEntity.noContent().build();
    }

    private EmployeeResponse toResponse(Employee e) {
        EmployeeResponse res = new EmployeeResponse();
        res.setId(e.getId());
        res.setEmployeeNo(e.getEmployeeNo());
        res.setFirstName(e.getFirstName());
        res.setLastName(e.getLastName());
        res.setDob(e.getDob());
        res.setDoj(e.getDoj());
        res.setSalary(e.getSalary());
        return res;
    }
}
