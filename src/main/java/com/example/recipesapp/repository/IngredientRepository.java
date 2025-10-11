package com.example.recipesapp.repository;

import com.example.recipesapp.model.Ingredient;
import com.example.recipesapp.model.IngredientCategory;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository pattern implementation for Ingredient entities.
 * Provides catalog queries grouped by category and uniqueness lookups.
 */
@Repository
public interface IngredientRepository extends JpaRepository<Ingredient, Long> {

    Optional<Ingredient> findByIngredientNameIgnoreCase(String ingredientName);

    boolean existsByIngredientNameIgnoreCase(String ingredientName);

    List<Ingredient> findByCategory(IngredientCategory category);
}