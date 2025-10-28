package com.example.recipesapp.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

/**
 * Clave compuesta (Patrón Value Object) para identificar un ingrediente en la despensa por usuario.
 */
@Embeddable
public class PantryItemId implements Serializable {

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "ingredient_id")
    private Long ingredientId;

    public PantryItemId() {
    }

    public PantryItemId(Long userId, Long ingredientId) {
        this.userId = userId;
        this.ingredientId = ingredientId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getIngredientId() {
        return ingredientId;
    }

    public void setIngredientId(Long ingredientId) {
        this.ingredientId = ingredientId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PantryItemId that)) {
            return false;
        }
        return Objects.equals(userId, that.userId)
            && Objects.equals(ingredientId, that.ingredientId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, ingredientId);
    }
}

