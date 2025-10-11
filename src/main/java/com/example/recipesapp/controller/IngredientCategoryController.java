package com.example.recipesapp.controller;

import com.example.recipesapp.dto.IngredientCategoryRequest;
import com.example.recipesapp.dto.IngredientCategoryResponse;
import com.example.recipesapp.dto.MessageResponse;
import com.example.recipesapp.service.IngredientCategoryService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller (Controller pattern) delegating to the Service Layer.
 */
@RestController
@RequestMapping("/api/categories")
public class IngredientCategoryController {

    private final IngredientCategoryService categoryService;

    public IngredientCategoryController(IngredientCategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public ResponseEntity<List<IngredientCategoryResponse>> getAll() {
        return ResponseEntity.ok(categoryService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<IngredientCategoryResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(categoryService.findById(id));
    }

    @PostMapping
    public ResponseEntity<IngredientCategoryResponse> create(
        @Valid @RequestBody IngredientCategoryRequest request
    ) {
        IngredientCategoryResponse response = categoryService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<IngredientCategoryResponse> update(
        @PathVariable Long id,
        @Valid @RequestBody IngredientCategoryRequest request
    ) {
        return ResponseEntity.ok(categoryService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<MessageResponse> delete(@PathVariable Long id) {
        categoryService.delete(id);
        return ResponseEntity.ok(new MessageResponse("Categoria eliminada correctamente"));
    }
}