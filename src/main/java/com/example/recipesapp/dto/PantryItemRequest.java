package com.example.recipesapp.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * DTO de entrada para registrar o actualizar un ingrediente en la despensa.
 * Permite suministrar un id existente o el nombre de un nuevo ingrediente.
 */
public record PantryItemRequest(
    Long ingredientId,

    @Size(max = 120, message = "El nombre del ingrediente no debe exceder 120 caracteres")
    String ingredientName,

    Long categoryId,

    @NotNull(message = "La cantidad es obligatoria")
    @Min(value = 0, message = "La cantidad debe ser positiva")
    Double quantity,

    @NotBlank(message = "La unidad es obligatoria")
    @Size(max = 50, message = "La unidad no debe exceder 50 caracteres")
    String unit
) {
}
