package com.example.recipesapp.service.ai;

/**
 * Strategy pattern: abstrae al proveedor de IA usado por RecommendationService.
 */
public interface AiClient {

    String generateContent(String prompt);
}
