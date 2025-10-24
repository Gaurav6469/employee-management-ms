package com.bfsi.citibank.employeemanagement.service;

import com.bfsi.citibank.employeemanagement.dto.EmployeeRequest;
import com.bfsi.citibank.employeemanagement.entity.Employee;

import java.util.List;
import java.util.Optional;

public interface EmployeeService {
    Employee create(EmployeeRequest request);
    Employee update(Long id, EmployeeRequest request);
    Employee getById(Long id);
    List<Employee> getAll();
    void delete(Long id);
}
