package com.example.demo.exception;

import org.springframework.http.HttpStatus;

/**
 * Exception thrown when authentication fails.
 * Returns HTTP 401 status code.
 */
public class UnauthorizedException extends TaskManagementException {
    
    public UnauthorizedException(String message) {
        super(message, "UNAUTHORIZED", HttpStatus.UNAUTHORIZED);
    }
    
    public UnauthorizedException(String message, Throwable cause) {
        super(message, "UNAUTHORIZED", HttpStatus.UNAUTHORIZED, cause);
    }
}
