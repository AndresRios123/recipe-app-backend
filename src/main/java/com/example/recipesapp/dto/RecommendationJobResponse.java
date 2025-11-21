package com.example.recipesapp.dto;

import com.example.recipesapp.service.job.RecommendationJobService.Status;
import java.util.List;

public record RecommendationJobResponse(
    String jobId,
    Status status,
    List<RecommendationResponse> recommendations,
    String errorMessage
) {
}
