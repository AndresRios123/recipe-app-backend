package com.example.recipesapp.dto;

import java.util.List;

/**
 * Request para persistir una receta sugerida por la IA.
 */
public record SaveRecommendationRequest(
    Long recipeId,
    String title,
    String description,
    String instructions,
    Integer prepTimeMinutes,
    String difficulty,
    String imageUrl,
    List<RecommendationIngredientDto> ingredients
) {
}

