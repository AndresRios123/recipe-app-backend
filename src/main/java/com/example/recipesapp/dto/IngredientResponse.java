package com.example.recipesapp.dto;

/**
 * DTO response for ingredient catalog queries.
 */
public record IngredientResponse(
    Long id,
    String name,
    IngredientCategoryResponse category
) {
}