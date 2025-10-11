package com.example.recipesapp.controller;

import com.example.recipesapp.dto.MessageResponse;
import com.example.recipesapp.dto.RecipeRequest;
import com.example.recipesapp.dto.RecipeResponse;
import com.example.recipesapp.service.RecipeService;
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
 * REST controller delegating recipe operations to the Service Layer.
 */
@RestController
@RequestMapping("/api/recipes")
public class RecipeController {

    private final RecipeService recipeService;

    public RecipeController(RecipeService recipeService) {
        this.recipeService = recipeService;
    }

    @GetMapping
    public ResponseEntity<List<RecipeResponse>> getAll() {
        return ResponseEntity.ok(recipeService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<RecipeResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(recipeService.findById(id));
    }

    @PostMapping
    public ResponseEntity<RecipeResponse> create(@Valid @RequestBody RecipeRequest request) {
        RecipeResponse response = recipeService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<RecipeResponse> update(
        @PathVariable Long id,
        @Valid @RequestBody RecipeRequest request
    ) {
        return ResponseEntity.ok(recipeService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<MessageResponse> delete(@PathVariable Long id) {
        recipeService.delete(id);
        return ResponseEntity.ok(new MessageResponse("Receta eliminada correctamente"));
    }
}