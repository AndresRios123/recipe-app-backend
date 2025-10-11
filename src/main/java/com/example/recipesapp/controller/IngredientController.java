package com.example.recipesapp.controller;

import com.example.recipesapp.dto.IngredientRequest;
import com.example.recipesapp.dto.IngredientResponse;
import com.example.recipesapp.dto.MessageResponse;
import com.example.recipesapp.service.IngredientService;
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
 * REST controller exposing ingredient catalog operations.
 */
@RestController
@RequestMapping("/api/ingredients")
public class IngredientController {

    private final IngredientService ingredientService;

    public IngredientController(IngredientService ingredientService) {
        this.ingredientService = ingredientService;
    }

    @GetMapping
    public ResponseEntity<List<IngredientResponse>> getAll() {
        return ResponseEntity.ok(ingredientService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<IngredientResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ingredientService.findById(id));
    }

    @PostMapping
    public ResponseEntity<IngredientResponse> create(@Valid @RequestBody IngredientRequest request) {
        IngredientResponse response = ingredientService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<IngredientResponse> update(
        @PathVariable Long id,
        @Valid @RequestBody IngredientRequest request
    ) {
        return ResponseEntity.ok(ingredientService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<MessageResponse> delete(@PathVariable Long id) {
        ingredientService.delete(id);
        return ResponseEntity.ok(new MessageResponse("Ingrediente eliminado correctamente"));
    }
}