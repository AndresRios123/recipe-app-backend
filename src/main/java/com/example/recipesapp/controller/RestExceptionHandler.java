package com.example.recipesapp.controller;

import com.example.recipesapp.dto.MessageResponse;
import com.example.recipesapp.exception.DuplicateResourceException;
import com.example.recipesapp.exception.ResourceNotFoundException;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Controller advice (Controller pattern variation) mapping domain exceptions to HTTP responses.
 */
@RestControllerAdvice
public class RestExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<MessageResponse> handleNotFound(ResourceNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(new MessageResponse(ex.getMessage()));
    }

    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<MessageResponse> handleConflict(DuplicateResourceException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
            .body(new MessageResponse(ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = error instanceof FieldError fieldError
                ? fieldError.getField()
                : error.getObjectName();
            errors.put(fieldName, error.getDefaultMessage());
        });
        return ResponseEntity.badRequest().body(errors);
    }
}