package com.example.recipesapp.exception;

/**
 * Custom exception used by the Service Layer when a business constraint is violated.
 */
public class DuplicateResourceException extends RuntimeException {

    public DuplicateResourceException(String message) {
        super(message);
    }
}