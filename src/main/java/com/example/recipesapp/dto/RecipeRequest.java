package com.example.recipesapp.dto;

import com.example.recipesapp.model.Recipe.Difficulty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;

/**
 * DTO encapsulating the data required to create or update recipes.
 */
public record RecipeRequest(
    @NotBlank(message = "El nombre de la receta es obligatorio")
    @Size(max = 150, message = "El nombre de la receta no puede exceder 150 caracteres")
    String name,

    @Size(max = 500, message = "La descripcion no puede exceder 500 caracteres")
    String description,

    @NotBlank(message = "Las instrucciones son obligatorias")
    String instructions,

    Integer prepTimeMinutes,

    @NotNull(message = "La dificultad es obligatoria")
    Difficulty difficulty,

    String imageUrl,

    @NotNull(message = "La receta debe incluir al menos un ingrediente")
    @Size(min = 1, message = "Debe registrar al menos un ingrediente")
    @Valid
    List<RecipeIngredientRequest> ingredients
) {
}