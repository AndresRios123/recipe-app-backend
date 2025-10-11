package com.example.recipesapp.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Nested DTO used to express ingredient lines inside a recipe (Composite DTO pattern).
 */
public record RecipeIngredientRequest(
    @NotNull(message = "El identificador del ingrediente es obligatorio")
    Long ingredientId,

    @NotNull(message = "La cantidad es obligatoria")
    @Min(value = 0, message = "La cantidad debe ser positiva")
    Double quantity,

    @NotBlank(message = "La unidad es obligatoria")
    @Size(max = 50, message = "La unidad no debe exceder 50 caracteres")
    String unit,

    @Size(max = 150, message = "Las notas no deben exceder 150 caracteres")
    String notes
) {
}