package com.example.recipesapp.dto;

import com.example.recipesapp.model.Recipe.Difficulty;
import java.util.List;

/**
 * DTO response representing a recipe aggregate with its composition.
 */
public record RecipeResponse(
    Long id,
    String name,
    String description,
    String instructions,
    Integer prepTimeMinutes,
    Difficulty difficulty,
    String imageUrl,
    RecipeAuthorResponse author,
    List<RecipeIngredientResponse> ingredients
) {
}
