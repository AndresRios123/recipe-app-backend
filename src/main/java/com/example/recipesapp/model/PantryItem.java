package com.example.recipesapp.model;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;

/**
 * Entidad que representa la despensa virtual del usuario (Patrón Aggregate).
 */
@Entity
@Table(name = "pantry_items")
public class PantryItem {

    @EmbeddedId
    private PantryItemId id = new PantryItemId();

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("ingredientId")
    @JoinColumn(name = "ingredient_id", nullable = false)
    private Ingredient ingredient;

    @Column(nullable = false)
    private Double quantity;

    @Column(length = 50, nullable = false)
    private String unit;

    public PantryItem() {
    }

    public PantryItem(User user, Ingredient ingredient, Double quantity, String unit) {
        setUser(user);
        setIngredient(ingredient);
        this.quantity = quantity;
        this.unit = unit;
    }

    public PantryItemId getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
        if (user != null) {
            this.id.setUserId(user.getId());
        }
    }

    public Ingredient getIngredient() {
        return ingredient;
    }

    public void setIngredient(Ingredient ingredient) {
        this.ingredient = ingredient;
        if (ingredient != null) {
            this.id.setIngredientId(ingredient.getId());
        }
    }

    public Double getQuantity() {
        return quantity;
    }

    public void setQuantity(Double quantity) {
        this.quantity = quantity;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }
}

