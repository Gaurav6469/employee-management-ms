package com.bfsi.citibank.employeemanagement.repository;

import com.bfsi.citibank.employeemanagement.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    boolean existsByFirstNameAndLastNameAndDob(String firstName, String lastName, LocalDate dob);
    Optional<Employee> findByEmployeeNo(String employeeNo);
}
