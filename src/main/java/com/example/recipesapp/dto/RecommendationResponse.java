package com.example.recipesapp.dto;

import java.util.List;

/**
 * DTO devuelto por el motor de recomendación.
 */
public record RecommendationResponse(
    Long recipeId,
    String title,
    String description,
    String instructions,
    Integer prepTimeMinutes,
    String difficulty,
    String imageUrl,
    double matchScore,
    List<String> missingIngredients,
    List<RecommendationIngredientDto> ingredients
) {
}
