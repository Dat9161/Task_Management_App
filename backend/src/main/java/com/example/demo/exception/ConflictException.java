package com.example.demo.exception;

import org.springframework.http.HttpStatus;

/**
 * Exception thrown when a business rule violation occurs (e.g., duplicate resource, conflicting state).
 * Returns HTTP 409 status code.
 */
public class ConflictException extends TaskManagementException {
    
    public ConflictException(String message) {
        super(message, "CONFLICT", HttpStatus.CONFLICT);
    }
    
    public ConflictException(String message, Throwable cause) {
        super(message, "CONFLICT", HttpStatus.CONFLICT, cause);
    }
}
