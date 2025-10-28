package com.example.recipesapp.exception;

/**
 * Se lanza cuando se requiere autenticacion y no existe una sesion valida.
 */
public class UnauthorizedException extends RuntimeException {

    public UnauthorizedException(String message) {
        super(message);
    }

    public UnauthorizedException(String message, Throwable cause) {
        super(message, cause);
    }
}

