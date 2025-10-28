package com.example.recipesapp.exception;

/**
 * Excepción para operaciones no autorizadas por el usuario actual.
 */
public class AccessDeniedException extends RuntimeException {

    public AccessDeniedException(String message) {
        super(message);
    }
}

