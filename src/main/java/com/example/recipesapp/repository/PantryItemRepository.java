package com.example.recipesapp.repository;

import com.example.recipesapp.model.Ingredient;
import com.example.recipesapp.model.PantryItem;
import com.example.recipesapp.model.PantryItemId;
import com.example.recipesapp.model.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository pattern para los elementos de despensa por usuario.
 */
@Repository
public interface PantryItemRepository extends JpaRepository<PantryItem, PantryItemId> {

    List<PantryItem> findByUser(User user);

    Optional<PantryItem> findByUserAndIngredient(User user, Ingredient ingredient);
}

