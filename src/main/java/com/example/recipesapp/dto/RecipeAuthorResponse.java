package com.example.recipesapp.dto;

/**
 * DTO ligero que expone información básica del autor de la receta.
 */
public record RecipeAuthorResponse(Long id, String username, String email) {
}

