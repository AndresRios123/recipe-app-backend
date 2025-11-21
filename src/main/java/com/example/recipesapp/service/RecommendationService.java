package com.example.recipesapp.service;

import com.example.recipesapp.dto.RecommendationIngredientDto;
import com.example.recipesapp.dto.RecommendationResponse;
import com.example.recipesapp.dto.RecipeResponse;
import com.example.recipesapp.dto.SaveRecommendationRequest;
import com.example.recipesapp.exception.AiServiceException;
import com.example.recipesapp.model.Ingredient;
import com.example.recipesapp.model.IngredientCategory;
import com.example.recipesapp.model.PantryItem;
import com.example.recipesapp.model.Recipe;
import com.example.recipesapp.model.RecipeIngredient;
import com.example.recipesapp.model.User;
import com.example.recipesapp.repository.IngredientCategoryRepository;
import com.example.recipesapp.repository.IngredientRepository;
import com.example.recipesapp.repository.PantryItemRepository;
import com.example.recipesapp.repository.RecipeRepository;
import com.example.recipesapp.service.ai.AiClient;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

/**
 * Servicio responsable de comunicarse con el motor de IA (Gemini).
 */
@Service
@Transactional(readOnly = true)
public class RecommendationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(RecommendationService.class);
    private static final String DEFAULT_CATEGORY_NAME = "General";
    private static final int MAX_TITLE_LENGTH = 150;
    private static final int MAX_DESCRIPTION_LENGTH = 500;
    private static final int MAX_INSTRUCTIONS_LENGTH = 60000;

    private final CurrentUserService currentUserService;
    private final PantryItemRepository pantryItemRepository;
    private final RecipeRepository recipeRepository;
    private final IngredientRepository ingredientRepository;
    private final IngredientCategoryRepository categoryRepository;
    private final RecipeService recipeService;
    // Strategy pattern: AiClient permite intercambiar proveedores de IA sin tocar este servicio.
    private final AiClient aiClient;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public RecommendationService(
        CurrentUserService currentUserService,
        PantryItemRepository pantryItemRepository,
        RecipeRepository recipeRepository,
        IngredientRepository ingredientRepository,
        IngredientCategoryRepository categoryRepository,
        RecipeService recipeService,
        AiClient aiClient
    ) {
        this.currentUserService = currentUserService;
        this.pantryItemRepository = pantryItemRepository;
        this.recipeRepository = recipeRepository;
        this.ingredientRepository = ingredientRepository;
        this.categoryRepository = categoryRepository;
        this.recipeService = recipeService;
        this.aiClient = aiClient;
    }

    public List<RecommendationResponse> generateRecommendationsForCurrentUser() {
        User current = currentUserService.getCurrentUser();
        List<PantryItem> pantryItems = pantryItemRepository.findByUser(current);

        String prompt = buildGeminiPrompt(pantryItems);
        LOGGER.debug("Gemini prompt: {}", prompt);

        String rawResponse = aiClient.generateContent(prompt);
        List<RecommendationResponse> parsed = parseGeminiResponse(rawResponse);
        if (parsed.isEmpty()) {
            throw new AiServiceException("La API de Gemini no devolvio recetas en la respuesta.");
        }
        return parsed;
    }

    @Transactional
    public RecipeResponse saveRecommendation(SaveRecommendationRequest request) {
        if (request.recipeId() != null) {
            return recipeService.findById(request.recipeId());
        }

        if (!StringUtils.hasText(request.title())) {
            throw new IllegalArgumentException("El titulo de la receta es obligatorio");
        }

        User current = currentUserService.getCurrentUser();

        Recipe recipe = new Recipe();
        recipe.setName(truncate(request.title(), MAX_TITLE_LENGTH));
        recipe.setDescription(truncate(request.description(), MAX_DESCRIPTION_LENGTH));
        recipe.setInstructions(resolveInstructions(request.instructions()));
        recipe.setPrepTimeMinutes(request.prepTimeMinutes());
        recipe.setImageUrl(request.imageUrl());
        recipe.setCreatedBy(current);
        recipe.setDifficulty(parseDifficulty(request.difficulty()));

        if (request.ingredients() != null && !request.ingredients().isEmpty()) {
            for (RecommendationIngredientDto dto : request.ingredients()) {
                Ingredient ingredient = resolveIngredientByName(dto.name());
                RecipeIngredient recipeIngredient = new RecipeIngredient();
                recipeIngredient.setRecipe(recipe);
                recipeIngredient.setIngredient(ingredient);
                recipeIngredient.setQuantity(dto.quantity() != null ? dto.quantity() : 1.0);
                recipeIngredient.setUnit(StringUtils.hasText(dto.unit()) ? dto.unit() : "unidad");
                recipe.addIngredient(recipeIngredient);
            }
        }

        Recipe saved = recipeRepository.save(recipe);
        return recipeService.findById(saved.getId());
    }

    private Recipe.Difficulty parseDifficulty(String difficulty) {
        if (!StringUtils.hasText(difficulty)) {
            return Recipe.Difficulty.EASY;
        }
        try {
            String normalized = difficulty.trim().toUpperCase(Locale.ROOT);
            return Recipe.Difficulty.valueOf(normalized);
        } catch (IllegalArgumentException ex) {
            return Recipe.Difficulty.EASY;
        }
    }

    private String buildGeminiPrompt(List<PantryItem> pantryItems) {
        String ingredientList = pantryItems.stream()
            .map(item -> String.format(
                "%s (%s %s)",
                item.getIngredient().getIngredientName(),
                item.getQuantity(),
                item.getUnit()
            ))
            .collect(Collectors.joining(", "));

        if (!StringUtils.hasText(ingredientList)) {
            ingredientList = "ningun ingrediente";
        }

        return "Actua como un chef asistido por IA. Dispones de estos ingredientes en la despensa: "
            + ingredientList
            + ". Debes devolver exactamente 3 recetas distintas en formato JSON (arreglo). "
            + "Cada receta debe usar la mayor cantidad posible de los ingredientes disponibles y puede incluir como maximo tres ingredientes adicionales si son indispensables. "
            + "El JSON debe contener objetos con la forma {\"recipeId\": number o null, "
            + "\"title\": string, \"description\": string, \"instructions\": string, \"prepTimeMinutes\": number o null, "
            + "\"difficulty\": string, \"imageUrl\": string o null, \"matchScore\": number entre 0 y 1, "
            + "\"missingIngredients\": array de strings, \"ingredients\": array de objetos {\"name\": string, \"quantity\": number o null, \"unit\": string}}. "
            + "Si no conoces un identificador real para la receta, usa null en \"recipeId\" y nunca inventes valores. "
            + "Asegurate de que las tres recetas sean diferentes en titulo e instrucciones y no incluyas texto adicional fuera del JSON.";
    }

    private String sanitizeJsonBlock(String raw) {
        if (!StringUtils.hasText(raw)) {
            throw new AiServiceException("La respuesta de la API de Gemini esta vacia.");
        }
        String trimmed = raw.trim();
        if (trimmed.startsWith("```")) {
            int firstLineBreak = trimmed.indexOf('\n');
            int lastFence = trimmed.lastIndexOf("```");
            if (firstLineBreak > 0 && lastFence > firstLineBreak) {
                trimmed = trimmed.substring(firstLineBreak + 1, lastFence).trim();
            }
        }
        return trimmed;
    }

    private List<RecommendationResponse> parseGeminiResponse(String rawText) {
        try {
            String jsonPayload = sanitizeJsonBlock(rawText);
            JsonNode arrayNode = objectMapper.readTree(jsonPayload);
            if (!arrayNode.isArray()) {
                throw new AiServiceException("La respuesta de Gemini no tiene el formato JSON esperado.");
            }
            List<RecommendationResponse> recommendations = new ArrayList<>();
            for (JsonNode node : arrayNode) {
                String title = node.path("title").asText("Receta sugerida");
                double rawScore = node.path("matchScore").asDouble(0.5);
                double score = normalizeMatchScore(rawScore);

                Long recipeId = null;
                if (node.hasNonNull("recipeId")) {
                    long candidateId = node.path("recipeId").asLong();
                    if (candidateId > 0 && recipeRepository.existsById(candidateId)) {
                        recipeId = candidateId;
                    }
                }
                String description = node.path("description").asText(null);
                String instructions = node.path("instructions").asText(null);
                Integer prepTime = node.hasNonNull("prepTimeMinutes") ? node.path("prepTimeMinutes").asInt() : null;
                String difficulty = node.path("difficulty").asText(null);
                String image = node.path("imageUrl").asText(null);

                List<String> missing = new ArrayList<>();
                if (node.path("missingIngredients").isArray()) {
                    node.path("missingIngredients").forEach(m -> {
                        String value = m.asText();
                        if (StringUtils.hasText(value)) {
                            missing.add(value.trim());
                        }
                    });
                }

                List<RecommendationIngredientDto> ingredients = new ArrayList<>();
                if (node.path("ingredients").isArray()) {
                    for (JsonNode ingredientNode : node.path("ingredients")) {
                        String name = ingredientNode.path("name").asText(null);
                        if (!StringUtils.hasText(name)) {
                            continue;
                        }
                        Double quantity = ingredientNode.hasNonNull("quantity")
                            ? ingredientNode.path("quantity").asDouble()
                            : null;
                        String unit = ingredientNode.path("unit").asText(null);
                        ingredients.add(new RecommendationIngredientDto(name.trim(), quantity, unit));
                    }
                }

                String sanitizedTitle = truncate(title, MAX_TITLE_LENGTH);
                String sanitizedDescription = truncate(description, MAX_DESCRIPTION_LENGTH);
                String sanitizedInstructions = truncate(instructions, MAX_INSTRUCTIONS_LENGTH);

                recommendations.add(new RecommendationResponse(
                    recipeId,
                    sanitizedTitle,
                    sanitizedDescription,
                    sanitizedInstructions,
                    prepTime,
                    difficulty,
                    image,
                    score,
                    missing,
                    ingredients
                ));
            }
            return recommendations;
        } catch (JsonProcessingException ex) {
            LOGGER.error("No fue posible interpretar la respuesta de la API de Gemini", ex);
            throw new AiServiceException("No fue posible interpretar la respuesta de la API de Gemini.", ex);
        }
    }

    private double normalizeMatchScore(double rawScore) {
        if (Double.isNaN(rawScore)) {
            return 0.5;
        }
        double normalized = rawScore;
        if (rawScore > 1.0) {
            normalized = rawScore / 100.0;
        }
        if (normalized < 0) {
            normalized = 0;
        }
        if (normalized > 1.0) {
            normalized = 1.0;
        }
        return normalized;
    }

    private String resolveInstructions(String instructions) {
        if (StringUtils.hasText(instructions)) {
            return truncate(instructions, MAX_INSTRUCTIONS_LENGTH);
        }
        return "Instrucciones no disponibles. Sigue tu intuicion culinaria para completar la receta.";
    }

    private String truncate(String value, int maxLength) {
        if (!StringUtils.hasText(value)) {
            return null;
        }
        String trimmed = value.trim();
        byte[] bytes = trimmed.getBytes(StandardCharsets.UTF_8);
        if (bytes.length <= maxLength) {
            return trimmed;
        }
        int end = Math.min(trimmed.length(), maxLength);
        while (end > 0) {
            String candidate = trimmed.substring(0, end);
            if (candidate.getBytes(StandardCharsets.UTF_8).length <= maxLength) {
                return candidate;
            }
            end--;
        }
        return trimmed.substring(0, Math.min(trimmed.length(), maxLength));
    }

    private Ingredient resolveIngredientByName(String name) {
        if (!StringUtils.hasText(name)) {
            throw new IllegalArgumentException("El nombre del ingrediente es obligatorio");
        }
        return ingredientRepository.findByIngredientNameIgnoreCase(name.trim())
            .orElseGet(() -> createIngredient(name.trim()));
    }

    private Ingredient createIngredient(String name) {
        IngredientCategory category = categoryRepository.findByCategoryNameIgnoreCase(DEFAULT_CATEGORY_NAME)
            .orElseGet(() -> categoryRepository.save(new IngredientCategory(DEFAULT_CATEGORY_NAME)));
        Ingredient ingredient = new Ingredient(name, category);
        return ingredientRepository.save(ingredient);
    }
}

