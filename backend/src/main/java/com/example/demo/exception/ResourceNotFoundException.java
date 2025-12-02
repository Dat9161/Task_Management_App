package com.example.demo.exception;

import org.springframework.http.HttpStatus;

/**
 * Exception thrown when a requested resource is not found in the database.
 * Returns HTTP 404 status code.
 */
public class ResourceNotFoundException extends TaskManagementException {
    
    public ResourceNotFoundException(String message) {
        super(message, "RESOURCE_NOT_FOUND", HttpStatus.NOT_FOUND);
    }
    
    public ResourceNotFoundException(String resourceName, Long id) {
        super(String.format("%s with id %d not found", resourceName, id), 
              "RESOURCE_NOT_FOUND", 
              HttpStatus.NOT_FOUND);
    }
    
    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, "RESOURCE_NOT_FOUND", HttpStatus.NOT_FOUND, cause);
    }
}
