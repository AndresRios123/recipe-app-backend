package com.example.recipesapp.exception;

/**
 * Exception representing the absence of domain entities.
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }
}