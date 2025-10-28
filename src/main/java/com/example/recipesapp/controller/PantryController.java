package com.example.recipesapp.controller;

import com.example.recipesapp.dto.MessageResponse;
import com.example.recipesapp.dto.PantryItemRequest;
import com.example.recipesapp.dto.PantryItemResponse;
import com.example.recipesapp.service.PantryService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controlador REST para la "despensa virtual" del usuario.
 */
@RestController
@RequestMapping("/api/pantry")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class PantryController {

    private final PantryService pantryService;

    public PantryController(PantryService pantryService) {
        this.pantryService = pantryService;
    }

    @GetMapping
    public ResponseEntity<List<PantryItemResponse>> getPantry() {
        return ResponseEntity.ok(pantryService.findMine());
    }

    @PostMapping
    public ResponseEntity<PantryItemResponse> addIngredient(
        @Valid @RequestBody PantryItemRequest request
    ) {
        PantryItemResponse response = pantryService.addOrUpdate(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{ingredientId}")
    public ResponseEntity<PantryItemResponse> updateIngredient(
        @PathVariable Long ingredientId,
        @Valid @RequestBody PantryItemRequest request
    ) {
        return ResponseEntity.ok(pantryService.updateItem(ingredientId, request));
    }

    @DeleteMapping("/{ingredientId}")
    public ResponseEntity<MessageResponse> deleteIngredient(@PathVariable Long ingredientId) {
        pantryService.remove(ingredientId);
        return ResponseEntity.ok(new MessageResponse("Ingrediente eliminado de la despensa"));
    }
}
