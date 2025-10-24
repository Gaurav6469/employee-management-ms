package com.bfsi.citibank.employeemanagement.exception;

public class EmployeeAlreadyExistsException extends RuntimeException {
    public EmployeeAlreadyExistsException(String message) { super(message); }
}
