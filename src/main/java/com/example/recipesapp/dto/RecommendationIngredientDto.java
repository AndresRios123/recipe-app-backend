package com.example.recipesapp.dto;

/**
 * Ingrediente que forma parte de una receta recomendada por IA.
 */
public record RecommendationIngredientDto(
    String name,
    Double quantity,
    String unit
) {
}

