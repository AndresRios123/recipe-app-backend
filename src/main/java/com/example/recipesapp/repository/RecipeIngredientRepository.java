package com.example.recipesapp.repository;

import com.example.recipesapp.model.Ingredient;
import com.example.recipesapp.model.Recipe;
import com.example.recipesapp.model.RecipeIngredient;
import com.example.recipesapp.model.RecipeIngredientId;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository abstraction for the association entity RecipeIngredient.
 * Keeps the persistence logic for ingredient quantities within recipes.
 */
@Repository
public interface RecipeIngredientRepository extends JpaRepository<RecipeIngredient, RecipeIngredientId> {

    List<RecipeIngredient> findByRecipe(Recipe recipe);

    List<RecipeIngredient> findByIngredient(Ingredient ingredient);
}