package com.example.demo.exception;

import org.springframework.http.HttpStatus;

/**
 * Base exception class for all custom exceptions in the Task Management application.
 * Provides common fields for error code and HTTP status.
 */
public class TaskManagementException extends RuntimeException {
    
    private final String errorCode;
    private final HttpStatus httpStatus;
    
    public TaskManagementException(String message, String errorCode, HttpStatus httpStatus) {
        super(message);
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
    }
    
    public TaskManagementException(String message, String errorCode, HttpStatus httpStatus, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
    }
    
    public String getErrorCode() {
        return errorCode;
    }
    
    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}
