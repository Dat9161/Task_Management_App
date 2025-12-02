package com.example.demo.exception;

import org.springframework.http.HttpStatus;

/**
 * Exception thrown when input validation fails.
 * Returns HTTP 400 status code.
 */
public class ValidationException extends TaskManagementException {
    
    public ValidationException(String message) {
        super(message, "VALIDATION_ERROR", HttpStatus.BAD_REQUEST);
    }
    
    public ValidationException(String message, Throwable cause) {
        super(message, "VALIDATION_ERROR", HttpStatus.BAD_REQUEST, cause);
    }
}
