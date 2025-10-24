package com.bfsi.citibank.employeemanagement.exception;

import org.springframework.http.HttpStatus;

public class ApiError {
    private int status;
    private String error;
    private String message;

    public ApiError() {}

    public ApiError(HttpStatus status, String message) {
        this.status = status.value();
        this.error = status.getReasonPhrase();
        this.message = message;
    }

    // getters & setters
    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
