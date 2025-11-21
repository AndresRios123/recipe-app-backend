package com.example.recipesapp.service.job;

import com.example.recipesapp.model.User;
import com.example.recipesapp.service.CurrentUserService;
import com.example.recipesapp.service.RecommendationService;
import com.example.recipesapp.dto.RecommendationResponse;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.springframework.stereotype.Service;

@Service
public class RecommendationJobService {

    public enum Status {
        PENDING,
        DONE,
        ERROR
    }

    public static final class JobResult {
        private final String jobId;
        private volatile Status status;
        private volatile List<RecommendationResponse> recommendations;
        private volatile String errorMessage;

        public JobResult(String jobId) {
            this.jobId = jobId;
            this.status = Status.PENDING;
            this.recommendations = Collections.emptyList();
        }

        public String getJobId() {
            return jobId;
        }

        public Status getStatus() {
            return status;
        }

        public List<RecommendationResponse> getRecommendations() {
            return recommendations;
        }

        public String getErrorMessage() {
            return errorMessage;
        }

        private void markDone(List<RecommendationResponse> recs) {
            this.recommendations = recs;
            this.status = Status.DONE;
        }

        private void markError(String message) {
            this.errorMessage = message;
            this.status = Status.ERROR;
        }
    }

    private final RecommendationService recommendationService;
    private final CurrentUserService currentUserService;
    private final Map<String, JobResult> jobs = new ConcurrentHashMap<>();
    private final ExecutorService executor = Executors.newCachedThreadPool();

    public RecommendationJobService(
        RecommendationService recommendationService,
        CurrentUserService currentUserService
    ) {
        this.recommendationService = recommendationService;
        this.currentUserService = currentUserService;
    }

    public JobResult createJob() {
        User user = currentUserService.getCurrentUser();
        String jobId = UUID.randomUUID().toString();
        JobResult job = new JobResult(jobId);
        jobs.put(jobId, job);

        executor.submit(() -> {
            try {
                List<RecommendationResponse> recs = recommendationService.generateRecommendationsForUser(user);
                job.markDone(recs);
            } catch (Exception ex) {
                job.markError(ex.getMessage());
            }
        });

        return job;
    }

    public JobResult getJob(String jobId) {
        return jobs.get(jobId);
    }
}
