package com.example.recipesapp.service;

import com.example.recipesapp.dto.IngredientCategoryResponse;
import com.example.recipesapp.dto.IngredientRequest;
import com.example.recipesapp.dto.IngredientResponse;
import com.example.recipesapp.exception.DuplicateResourceException;
import com.example.recipesapp.exception.ResourceNotFoundException;
import com.example.recipesapp.model.Ingredient;
import com.example.recipesapp.model.IngredientCategory;
import com.example.recipesapp.repository.IngredientCategoryRepository;
import com.example.recipesapp.repository.IngredientRepository;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Layer component in charge of ingredient catalog operations.
 */
@Service
@Transactional
public class IngredientService {

    private final IngredientRepository ingredientRepository;
    private final IngredientCategoryRepository categoryRepository;

    public IngredientService(
        IngredientRepository ingredientRepository,
        IngredientCategoryRepository categoryRepository
    ) {
        this.ingredientRepository = ingredientRepository;
        this.categoryRepository = categoryRepository;
    }

    @Transactional(readOnly = true)
    public List<IngredientResponse> findAll() {
        return ingredientRepository.findAll().stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public IngredientResponse findById(Long id) {
        Ingredient ingredient = ingredientRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Ingrediente con id " + id + " no existe"));
        return mapToResponse(ingredient);
    }

    public IngredientResponse create(IngredientRequest request) {
        ingredientRepository.findByIngredientNameIgnoreCase(request.name())
            .ifPresent(existing -> {
                throw new DuplicateResourceException(
                    "El ingrediente '" + existing.getIngredientName() + "' ya existe");
            });

        IngredientCategory category = categoryRepository.findById(request.categoryId())
            .orElseThrow(() -> new ResourceNotFoundException(
                "Categoria con id " + request.categoryId() + " no existe"));

        Ingredient ingredient = new Ingredient(request.name(), category);
        Ingredient saved = ingredientRepository.save(ingredient);
        return mapToResponse(saved);
    }

    public IngredientResponse update(Long id, IngredientRequest request) {
        Ingredient ingredient = ingredientRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Ingrediente con id " + id + " no existe"));

        ingredientRepository.findByIngredientNameIgnoreCase(request.name())
            .filter(other -> !other.getId().equals(id))
            .ifPresent(existing -> {
                throw new DuplicateResourceException(
                    "El ingrediente '" + existing.getIngredientName() + "' ya existe");
            });

        IngredientCategory category = categoryRepository.findById(request.categoryId())
            .orElseThrow(() -> new ResourceNotFoundException(
                "Categoria con id " + request.categoryId() + " no existe"));

        ingredient.setIngredientName(request.name());
        ingredient.setCategory(category);
        return mapToResponse(ingredient);
    }

    public void delete(Long id) {
        Ingredient ingredient = ingredientRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Ingrediente con id " + id + " no existe"));
        ingredientRepository.delete(ingredient);
    }

    private IngredientResponse mapToResponse(Ingredient ingredient) {
        IngredientCategory category = ingredient.getCategory();
        IngredientCategoryResponse categoryDto =
            new IngredientCategoryResponse(category.getId(), category.getCategoryName());
        return new IngredientResponse(ingredient.getId(), ingredient.getIngredientName(), categoryDto);
    }
}