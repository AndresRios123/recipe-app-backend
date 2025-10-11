package com.example.recipesapp.model;

// import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;


@Entity
@Table(name = "ingredients")
public class Ingredient {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 100, nullable = false, unique = true)
    private String ingredientName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private IngredientCategory category;

    public Ingredient(){

    }

    public Ingredient(String ingredientName, IngredientCategory category){
        this.ingredientName = ingredientName;
        this.category = category;

    }

    public Long getId(){
        return id;
    }

    public void setId(Long id){
        this.id = id;
    }

    
    public String getIngredientName(){
        return ingredientName;
    }

    public void setIngredientName(String ingredientName){
        this.ingredientName = ingredientName;
    }

    public IngredientCategory getCategory(){
        return category;
    }

    public void setCategory(IngredientCategory category){
        this.category = category;
    }

}