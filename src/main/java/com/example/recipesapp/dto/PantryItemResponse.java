package com.example.recipesapp.dto;

/**
 * DTO que devuelve la despensa personal del usuario autenticado.
 */
public record PantryItemResponse(
    Long ingredientId,
    String ingredientName,
    Double quantity,
    String unit,
    IngredientCategoryResponse category
) {
}

