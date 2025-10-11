package com.example.recipesapp.repository;

import com.example.recipesapp.model.IngredientCategory;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository pattern concrete implementation for ingredient category aggregates.
 * Encapsulates persistence operations for the shared catalog.
 */
@Repository
public interface IngredientCategoryRepository extends JpaRepository<IngredientCategory, Long> {

    Optional<IngredientCategory> findByCategoryNameIgnoreCase(String categoryName);

    boolean existsByCategoryNameIgnoreCase(String categoryName);
}