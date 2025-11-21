package com.example.recipesapp.service;

import com.example.recipesapp.dto.RecipeAuthorResponse;
import com.example.recipesapp.dto.RecipeIngredientRequest;
import com.example.recipesapp.dto.RecipeIngredientResponse;
import com.example.recipesapp.dto.RecipeRequest;
import com.example.recipesapp.dto.RecipeResponse;
import com.example.recipesapp.exception.AccessDeniedException;
import com.example.recipesapp.exception.ResourceNotFoundException;
import com.example.recipesapp.model.Ingredient;
import com.example.recipesapp.model.Recipe;
import com.example.recipesapp.model.RecipeIngredient;
import com.example.recipesapp.model.User;
import com.example.recipesapp.repository.IngredientRepository;
import com.example.recipesapp.repository.RecipeRepository;
import com.example.recipesapp.service.factory.RecipeFactory;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Layer implementation orchestrating recipe aggregates.
 */
@Service
@Transactional
public class RecipeService {

    private final RecipeRepository recipeRepository;
    private final IngredientRepository ingredientRepository;
    private final CurrentUserService currentUserService;
    private final RecipeFactory recipeFactory;

    public RecipeService(
        RecipeRepository recipeRepository,
        IngredientRepository ingredientRepository,
        CurrentUserService currentUserService,
        RecipeFactory recipeFactory
    ) {
        this.recipeRepository = recipeRepository;
        this.ingredientRepository = ingredientRepository;
        this.currentUserService = currentUserService;
        this.recipeFactory = recipeFactory;
    }

    @Transactional(readOnly = true)
    public List<RecipeResponse> findAll() {
        return recipeRepository.findAll().stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<RecipeResponse> findMine() {
        User current = currentUserService.getCurrentUser();
        return recipeRepository.findByCreatedBy(current).stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public RecipeResponse findById(Long id) {
        Recipe recipe = recipeRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Receta con id " + id + " no encontrada"));
        return mapToResponse(recipe);
    }

    public RecipeResponse create(RecipeRequest request) {
        User current = currentUserService.getCurrentUser();
        // Factory Method: delega la creacion del agregado a RecipeFactory.
        Recipe recipe = recipeFactory.createFromRequest(request, current);

        mapIngredientRequests(recipe, request.ingredients());

        Recipe saved = recipeRepository.save(recipe);
        return mapToResponse(saved);
    }

    public RecipeResponse update(Long id, RecipeRequest request) {
        Recipe recipe = recipeRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Receta con id " + id + " no encontrada"));
        enforceOwnership(recipe);

        // Factory reutilizada para mantener consistencia cuando se actualiza un agregado existente.
        recipeFactory.applyUpdates(recipe, request);

        recipe.getIngredients().clear();
        mapIngredientRequests(recipe, request.ingredients());

        return mapToResponse(recipe);
    }

    public void delete(Long id) {
        Recipe recipe = recipeRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Receta con id " + id + " no encontrada"));
        enforceOwnership(recipe);
        recipeRepository.delete(recipe);
    }

    private void mapIngredientRequests(Recipe recipe, List<RecipeIngredientRequest> ingredientRequests) {
        for (RecipeIngredientRequest ingredientRequest : ingredientRequests) {
            Ingredient ingredient = ingredientRepository.findById(ingredientRequest.ingredientId())
                .orElseThrow(() -> new ResourceNotFoundException(
                    "Ingrediente con id " + ingredientRequest.ingredientId() + " no existe"));

            RecipeIngredient recipeIngredient = new RecipeIngredient();
            recipeIngredient.setRecipe(recipe);
            recipeIngredient.setIngredient(ingredient);
            recipeIngredient.setQuantity(ingredientRequest.quantity());
            recipeIngredient.setUnit(ingredientRequest.unit());
            recipeIngredient.setNotes(ingredientRequest.notes());

            recipe.addIngredient(recipeIngredient);
        }
    }

    private RecipeResponse mapToResponse(Recipe recipe) {
        List<RecipeIngredientResponse> ingredientResponses = recipe.getIngredients().stream()
            .map(ri -> new RecipeIngredientResponse(
                ri.getIngredient().getId(),
                ri.getIngredient().getIngredientName(),
                ri.getQuantity(),
                ri.getUnit(),
                ri.getNotes()
            ))
            .collect(Collectors.toList());

        RecipeAuthorResponse authorDto = new RecipeAuthorResponse(
            recipe.getCreatedBy().getId(),
            recipe.getCreatedBy().getUsername(),
            recipe.getCreatedBy().getEmail()
        );

        return new RecipeResponse(
            recipe.getId(),
            recipe.getName(),
            recipe.getDescription(),
            recipe.getInstructions(),
            recipe.getPrepTimeMinutes(),
            recipe.getDifficulty(),
            recipe.getImageUrl(),
            authorDto,
            ingredientResponses
        );
    }

    /**
     * Verifica que la receta pertenezca al usuario autenticado.
     */
    private void enforceOwnership(Recipe recipe) {
        User current = currentUserService.getCurrentUser();
        if (!recipe.getCreatedBy().getId().equals(current.getId())) {
            throw new AccessDeniedException("No tienes permisos para modificar esta receta");
        }
    }
}
