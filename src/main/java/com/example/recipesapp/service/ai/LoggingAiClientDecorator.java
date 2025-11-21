package com.example.recipesapp.service.ai;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

/**
 * Decorator pattern: envuelve al proveedor real para agregar logging y metricas ligeras.
 */
@Component
@Primary
public class LoggingAiClientDecorator implements AiClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoggingAiClientDecorator.class);

    private final AiClient delegate;

    public LoggingAiClientDecorator(@Qualifier("geminiAiClient") AiClient delegate) {
        this.delegate = delegate;
    }

    @Override
    public String generateContent(String prompt) {
        long start = System.currentTimeMillis();
        try {
            String response = delegate.generateContent(prompt);
            LOGGER.debug("AI response length={} characters", response != null ? response.length() : 0);
            return response;
        } finally {
            long elapsed = System.currentTimeMillis() - start;
            LOGGER.debug("AI provider processed prompt in {} ms", elapsed);
        }
    }
}
