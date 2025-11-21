package com.example.recipesapp.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "recipes")
public class Recipe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 150, nullable = false)
    private String name;

    @Column(length = 500)
    private String description;

    @Lob
    @Column(columnDefinition = "LONGTEXT", nullable = false)
    private String instructions;

    @Column(name = "prep_time_minutes")
    private Integer prepTimeMinutes;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private Difficulty difficulty;

    @Column(name = "image_url")
    private String imageUrl;

    @OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<RecipeIngredient> ingredients = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_id", nullable = false)
    private User createdBy;

    public Recipe() {
    }

    public Recipe(
        String name,
        String description,
        String instructions,
        Integer prepTimeMinutes,
        Difficulty difficulty,
        String imageUrl,
        User createdBy
    ) {
        this.name = name;
        this.description = description;
        this.instructions = instructions;
        this.prepTimeMinutes = prepTimeMinutes;
        this.difficulty = difficulty;
        this.imageUrl = imageUrl;
        this.createdBy = createdBy;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getInstructions() {
        return instructions;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

    public Integer getPrepTimeMinutes() {
        return prepTimeMinutes;
    }

    public void setPrepTimeMinutes(Integer prepTimeMinutes) {
        this.prepTimeMinutes = prepTimeMinutes;
    }

    public Difficulty getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(Difficulty difficulty) {
        this.difficulty = difficulty;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Set<RecipeIngredient> getIngredients() {
        return ingredients;
    }

    public void setIngredients(Set<RecipeIngredient> ingredients) {
        this.ingredients = ingredients;
    }

    public User getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }

    public void addIngredient(RecipeIngredient recipeIngredient) {
        ingredients.add(recipeIngredient);
        recipeIngredient.setRecipe(this);
    }

    public void removeIngredient(RecipeIngredient recipeIngredient) {
        ingredients.remove(recipeIngredient);
        recipeIngredient.setRecipe(null);
    }

    /**
     * Builder pattern: permite crear instancias controlando los campos opcionales.
     */
    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {

        private final Recipe instance;

        private Builder() {
            this.instance = new Recipe();
        }

        public Builder withName(String name) {
            instance.setName(name);
            return this;
        }

        public Builder withDescription(String description) {
            instance.setDescription(description);
            return this;
        }

        public Builder withInstructions(String instructions) {
            instance.setInstructions(instructions);
            return this;
        }

        public Builder withPrepTimeMinutes(Integer prepTimeMinutes) {
            instance.setPrepTimeMinutes(prepTimeMinutes);
            return this;
        }

        public Builder withDifficulty(Difficulty difficulty) {
            instance.setDifficulty(difficulty);
            return this;
        }

        public Builder withImageUrl(String imageUrl) {
            instance.setImageUrl(imageUrl);
            return this;
        }

        public Builder withCreatedBy(User createdBy) {
            instance.setCreatedBy(createdBy);
            return this;
        }

        public Recipe build() {
            if (instance.getIngredients() == null) {
                instance.setIngredients(new HashSet<>());
            }
            return instance;
        }
    }

    public enum Difficulty {
        EASY,
        MEDIUM,
        HARD
    }
}
