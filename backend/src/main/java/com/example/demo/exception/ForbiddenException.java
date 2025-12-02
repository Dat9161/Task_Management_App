package com.example.demo.exception;

import org.springframework.http.HttpStatus;

/**
 * Exception thrown when a user attempts to access a resource they don't have permission for.
 * Returns HTTP 403 status code.
 */
public class ForbiddenException extends TaskManagementException {
    
    public ForbiddenException(String message) {
        super(message, "FORBIDDEN", HttpStatus.FORBIDDEN);
    }
    
    public ForbiddenException(String message, Throwable cause) {
        super(message, "FORBIDDEN", HttpStatus.FORBIDDEN, cause);
    }
}
