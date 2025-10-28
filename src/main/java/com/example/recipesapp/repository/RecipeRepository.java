package com.example.recipesapp.repository;

import com.example.recipesapp.model.Recipe;
import com.example.recipesapp.model.Recipe.Difficulty;
import com.example.recipesapp.model.User;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for Recipe aggregates (Repository pattern).
 * Supports search utilities that will feed the recommendation strategy later on.
 */
@Repository
public interface RecipeRepository extends JpaRepository<Recipe, Long> {

    List<Recipe> findByNameContainingIgnoreCase(String name);

    List<Recipe> findByDifficulty(Difficulty difficulty);

    List<Recipe> findByPrepTimeMinutesLessThanEqual(Integer minutes);

    List<Recipe> findByCreatedBy(User createdBy);
}
