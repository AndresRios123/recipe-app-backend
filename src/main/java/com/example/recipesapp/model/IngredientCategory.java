package com.example.recipesapp.model;

// import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;


@Entity
@Table(name = "ingredient_categories")
public class IngredientCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String categoryName;

    public IngredientCategory(){

    }

    public IngredientCategory(String categoryName){
        this.categoryName = categoryName;
    }

    public Long getId(){
        return id;
    }

    public void setId(Long id){
        this.id = id;
    }

    
    public String getCategoryName(){
        return categoryName;
    }

    public void setCategoryName(String categoryName){
        this.categoryName = categoryName;
    }
}