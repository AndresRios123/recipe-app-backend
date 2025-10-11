package com.example.recipesapp.service;

import com.example.recipesapp.dto.IngredientCategoryRequest;
import com.example.recipesapp.dto.IngredientCategoryResponse;
import com.example.recipesapp.exception.DuplicateResourceException;
import com.example.recipesapp.exception.ResourceNotFoundException;
import com.example.recipesapp.model.IngredientCategory;
import com.example.recipesapp.repository.IngredientCategoryRepository;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Layer pattern implementation that orchestrates ingredient category use cases.
 */
@Service
@Transactional
public class IngredientCategoryService {

    private final IngredientCategoryRepository categoryRepository;

    public IngredientCategoryService(IngredientCategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Transactional(readOnly = true)
    public List<IngredientCategoryResponse> findAll() {
        return categoryRepository.findAll().stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public IngredientCategoryResponse findById(Long id) {
        IngredientCategory category = categoryRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Categoria con id " + id + " no existe"));
        return mapToResponse(category);
    }

    public IngredientCategoryResponse create(IngredientCategoryRequest request) {
        categoryRepository.findByCategoryNameIgnoreCase(request.name())
            .ifPresent(existing -> {
                throw new DuplicateResourceException(
                    "La categoria '" + existing.getCategoryName() + "' ya existe");
            });

        IngredientCategory category = new IngredientCategory(request.name());
        IngredientCategory saved = categoryRepository.save(category);
        return mapToResponse(saved);
    }

    public IngredientCategoryResponse update(Long id, IngredientCategoryRequest request) {
        IngredientCategory category = categoryRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Categoria con id " + id + " no existe"));

        categoryRepository.findByCategoryNameIgnoreCase(request.name())
            .filter(other -> !other.getId().equals(id))
            .ifPresent(existing -> {
                throw new DuplicateResourceException(
                    "La categoria '" + existing.getCategoryName() + "' ya existe");
            });

        category.setCategoryName(request.name());
        return mapToResponse(category);
    }

    public void delete(Long id) {
        IngredientCategory category = categoryRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Categoria con id " + id + " no existe"));
        categoryRepository.delete(category);
    }

    private IngredientCategoryResponse mapToResponse(IngredientCategory category) {
        return new IngredientCategoryResponse(category.getId(), category.getCategoryName());
    }
}