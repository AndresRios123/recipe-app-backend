package com.example.recipesapp.exception;

/**
 * Excepción lanzada cuando la integración con el motor de IA falla o no está disponible.
 */
public class AiServiceException extends RuntimeException {

    public AiServiceException(String message) {
        super(message);
    }

    public AiServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}

