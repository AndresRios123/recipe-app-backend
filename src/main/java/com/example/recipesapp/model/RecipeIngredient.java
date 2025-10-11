package com.example.recipesapp.model;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;

@Entity
@Table(name = "recipe_ingredients")
public class RecipeIngredient {

    @EmbeddedId
    private RecipeIngredientId id = new RecipeIngredientId();

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("recipeId")
    @JoinColumn(name = "recipe_id", nullable = false)
    private Recipe recipe;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("ingredientId")
    @JoinColumn(name = "ingredient_id", nullable = false)
    private Ingredient ingredient;

    @Column(nullable = false)
    private Double quantity;

    @Column(length = 50, nullable = false)
    private String unit;

    @Column(length = 150)
    private String notes;

    public RecipeIngredient() {
    }

    public RecipeIngredient(Recipe recipe, Ingredient ingredient, Double quantity, String unit) {
        this.quantity = quantity;
        this.unit = unit;
        setRecipe(recipe);      // asegura que recipe y el id embebido queden alineados
        setIngredient(ingredient); // idem para el ingrediente
    }

    public RecipeIngredientId getId() {
        return id;
    }

    public Recipe getRecipe() {
        return recipe;
    }

    public void setRecipe(Recipe recipe) {
        this.recipe = recipe;
        if (recipe != null) {
            this.id.setRecipeId(recipe.getId());
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

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}