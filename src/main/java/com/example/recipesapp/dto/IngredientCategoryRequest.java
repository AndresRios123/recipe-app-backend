package com.example.recipesapp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO pattern request object for ingredient category commands.
 */
public record IngredientCategoryRequest(
    @NotBlank(message = "El nombre de la categoria es obligatorio")
    @Size(max = 100, message = "La categoria no debe exceder 100 caracteres")
    String name
) {
}