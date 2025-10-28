package com.example.recipesapp.service;

import com.example.recipesapp.dto.IngredientCategoryResponse;
import com.example.recipesapp.dto.PantryItemRequest;
import com.example.recipesapp.dto.PantryItemResponse;
import com.example.recipesapp.exception.ResourceNotFoundException;
import com.example.recipesapp.model.Ingredient;
import com.example.recipesapp.model.IngredientCategory;
import com.example.recipesapp.model.PantryItem;
import com.example.recipesapp.model.User;
import com.example.recipesapp.repository.IngredientCategoryRepository;
import com.example.recipesapp.repository.IngredientRepository;
import com.example.recipesapp.repository.PantryItemRepository;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

/**
 * Service Layer para la despensa del usuario. Administra altas, bajas y garantiza
 * coherencia en cantidades y unidades.
 */
@Service
@Transactional
public class PantryService {

    private static final String DEFAULT_CATEGORY_NAME = "General";

    private final PantryItemRepository pantryItemRepository;
    private final IngredientRepository ingredientRepository;
    private final IngredientCategoryRepository categoryRepository;
    private final CurrentUserService currentUserService;

    public PantryService(
        PantryItemRepository pantryItemRepository,
        IngredientRepository ingredientRepository,
        IngredientCategoryRepository categoryRepository,
        CurrentUserService currentUserService
    ) {
        this.pantryItemRepository = pantryItemRepository;
        this.ingredientRepository = ingredientRepository;
        this.categoryRepository = categoryRepository;
        this.currentUserService = currentUserService;
    }

    @Transactional(readOnly = true)
    public List<PantryItemResponse> findMine() {
        User current = currentUserService.getCurrentUser();
        return pantryItemRepository.findByUser(current).stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    public PantryItemResponse addOrUpdate(PantryItemRequest request) {
        User current = currentUserService.getCurrentUser();
        Ingredient ingredient = resolveIngredient(request);

        PantryItem pantryItem = pantryItemRepository.findByUserAndIngredient(current, ingredient)
            .orElseGet(() -> new PantryItem(current, ingredient, 0.0, request.unit()));

        pantryItem.setQuantity(request.quantity());
        pantryItem.setUnit(request.unit());

        PantryItem saved = pantryItemRepository.save(pantryItem);
        return mapToResponse(saved);
    }

    public PantryItemResponse updateItem(Long ingredientId, PantryItemRequest request) {
        User current = currentUserService.getCurrentUser();
        Ingredient ingredient = ingredientRepository.findById(ingredientId)
            .orElseThrow(() -> new ResourceNotFoundException(
                "Ingrediente con id " + ingredientId + " no existe"));

        PantryItem pantryItem = pantryItemRepository.findByUserAndIngredient(current, ingredient)
            .orElseThrow(() -> new ResourceNotFoundException("No has registrado este ingrediente en tu despensa"));

        pantryItem.setQuantity(request.quantity());
        pantryItem.setUnit(request.unit());
        return mapToResponse(pantryItem);
    }

    public void remove(Long ingredientId) {
        User current = currentUserService.getCurrentUser();
        Ingredient ingredient = ingredientRepository.findById(ingredientId)
            .orElseThrow(() -> new ResourceNotFoundException(
                "Ingrediente con id " + ingredientId + " no existe"));

        PantryItem pantryItem = pantryItemRepository.findByUserAndIngredient(current, ingredient)
            .orElseThrow(() -> new ResourceNotFoundException("No has registrado este ingrediente en tu despensa"));

        pantryItemRepository.delete(pantryItem);
    }

    private Ingredient resolveIngredient(PantryItemRequest request) {
        if (request.ingredientId() != null) {
            return ingredientRepository.findById(request.ingredientId())
                .orElseThrow(() -> new ResourceNotFoundException(
                    "Ingrediente con id " + request.ingredientId() + " no existe"));
        }

        if (!StringUtils.hasText(request.ingredientName())) {
            throw new ResourceNotFoundException("Debe proporcionar el id o el nombre del ingrediente");
        }

        return ingredientRepository.findByIngredientNameIgnoreCase(request.ingredientName().trim())
            .orElseGet(() -> createIngredient(request));
    }

    private Ingredient createIngredient(PantryItemRequest request) {
        IngredientCategory category;
        if (request.categoryId() != null) {
            category = categoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new ResourceNotFoundException(
                    "Categoria con id " + request.categoryId() + " no existe"));
        } else {
            category = categoryRepository.findByCategoryNameIgnoreCase(DEFAULT_CATEGORY_NAME)
                .orElseGet(() -> categoryRepository.save(new IngredientCategory(DEFAULT_CATEGORY_NAME)));
        }

        Ingredient ingredient = new Ingredient(request.ingredientName().trim(), category);
        return ingredientRepository.save(ingredient);
    }

    private PantryItemResponse mapToResponse(PantryItem pantryItem) {
        Ingredient ingredient = pantryItem.getIngredient();
        IngredientCategoryResponse categoryResponse = new IngredientCategoryResponse(
            ingredient.getCategory().getId(),
            ingredient.getCategory().getCategoryName()
        );
        return new PantryItemResponse(
            ingredient.getId(),
            ingredient.getIngredientName(),
            pantryItem.getQuantity(),
            pantryItem.getUnit(),
            categoryResponse
        );
    }
}