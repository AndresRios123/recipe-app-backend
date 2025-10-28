package com.example.recipesapp.controller;

import com.example.recipesapp.dto.RecommendationResponse;
import com.example.recipesapp.dto.SaveRecommendationRequest;
import com.example.recipesapp.service.RecommendationService;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Exposicion REST para obtener recomendaciones alimentadas por IA.
 */
@RestController
@RequestMapping("/api/recommendations")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class RecommendationController {

    private final RecommendationService recommendationService;

    public RecommendationController(RecommendationService recommendationService) {
        this.recommendationService = recommendationService;
    }

    @GetMapping
    public ResponseEntity<List<RecommendationResponse>> getRecommendations() {
        return ResponseEntity.ok(recommendationService.generateRecommendationsForCurrentUser());
    }

    @PostMapping("/save")
    public ResponseEntity<?> saveRecommendation(@RequestBody SaveRecommendationRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(recommendationService.saveRecommendation(request));
    }
}
