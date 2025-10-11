package com.example.recipesapp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * DTO request for ingredient create and update operations.
 */
public record IngredientRequest(
    @NotBlank(message = "El nombre del ingrediente es obligatorio")
    @Size(max = 100, message = "El nombre del ingrediente no debe exceder 100 caracteres")
    String name,

    @NotNull(message = "La categoria es obligatoria")
    Long categoryId
) {
}