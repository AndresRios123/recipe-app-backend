package com.example.recipesapp.service.factory;

import com.example.recipesapp.dto.RecipeRequest;
import com.example.recipesapp.model.Recipe;
import com.example.recipesapp.model.User;
import org.springframework.stereotype.Component;

/**
 * Factory Method pattern: centraliza la creacion y actualizacion de agregados Recipe.
 */
@Component
public class RecipeFactory {

    public Recipe createFromRequest(RecipeRequest request, User owner) {
        return Recipe.builder()
            .withName(request.name())
            .withDescription(request.description())
            .withInstructions(request.instructions())
            .withPrepTimeMinutes(request.prepTimeMinutes())
            .withDifficulty(request.difficulty())
            .withImageUrl(request.imageUrl())
            .withCreatedBy(owner)
            .build();
    }

    public void applyUpdates(Recipe target, RecipeRequest request) {
        target.setName(request.name());
        target.setDescription(request.description());
        target.setInstructions(request.instructions());
        target.setPrepTimeMinutes(request.prepTimeMinutes());
        target.setDifficulty(request.difficulty());
        target.setImageUrl(request.imageUrl());
    }
}
