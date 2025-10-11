package com.example.recipesapp.dto;

/**
 * DTO response representing one ingredient line inside a recipe aggregate.
 */
public record RecipeIngredientResponse(
    Long ingredientId,
    String ingredientName,
    Double quantity,
    String unit,
    String notes
) {
}