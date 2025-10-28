package com.example.recipesapp.service;

import com.example.recipesapp.exception.AiServiceException;
import java.net.URI;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * Encapsula la comunicacion con el SDK oficial de Gemini.
 */
@Service
public class GeminiClientService {

    private static final Logger LOGGER = LoggerFactory.getLogger(GeminiClientService.class);

    private final RestClient restClient;
    private final String apiUrl;
    private final String apiKey;

    public GeminiClientService(
        @Value("${ai.gemini.api-key:}") String apiKey,
        @Value("${ai.gemini.api-url:}") String apiUrl
    ) {
        if (!StringUtils.hasText(apiKey)) {
            throw new AiServiceException("La API de Gemini no esta configurada. Define ai.gemini.api-key.");
        }
        if (!StringUtils.hasText(apiUrl)) {
            throw new AiServiceException("La API de Gemini no esta configurada. Define ai.gemini.api-url.");
        }
        this.apiKey = apiKey;
        this.apiUrl = apiUrl;
        this.restClient = RestClient.builder()
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
            .build();
    }

    public String generateContent(String prompt) {
        try {
            URI uri = UriComponentsBuilder.fromHttpUrl(apiUrl)
                .queryParam("key", apiKey)
                .build()
                .toUri();

            GeminiGenerateContentRequest request = new GeminiGenerateContentRequest(
                List.of(new RequestContent(List.of(new RequestPart(prompt))))
            );

            GeminiGenerateContentResponse response = restClient.post()
                .uri(uri)
                .body(request)
                .retrieve()
                .body(GeminiGenerateContentResponse.class);

            String text = response != null ? response.firstCandidateText() : null;
            if (!StringUtils.hasText(text)) {
                throw new AiServiceException("La API de Gemini respondio sin contenido valido.");
            }
            return text;
        } catch (RestClientResponseException ex) {
            LOGGER.error("Gemini respondio con error {} {}: {}", ex.getStatusCode(), ex.getStatusText(), ex.getResponseBodyAsString());
            throw new AiServiceException("La API de Gemini respondio con un error " + ex.getStatusCode(), ex);
        } catch (RestClientException ex) {
            throw new AiServiceException("No fue posible conectarse a la API de Gemini.", ex);
        } catch (RuntimeException ex) {
            throw new AiServiceException("La API de Gemini no devolvio una respuesta valida.", ex);
        }
    }

    private record GeminiGenerateContentRequest(List<RequestContent> contents) {
    }

    private record RequestContent(List<RequestPart> parts) {
    }

    private record RequestPart(String text) {
    }

    private record GeminiGenerateContentResponse(List<Candidate> candidates) {

        private String firstCandidateText() {
            if (candidates == null) {
                return null;
            }
            return candidates.stream()
                .map(Candidate::content)
                .filter(content -> content != null && content.parts != null)
                .flatMap(content -> content.parts.stream())
                .map(Part::text)
                .filter(StringUtils::hasText)
                .findFirst()
                .orElse(null);
        }
    }

    private record Candidate(ResponseContent content) {
    }

    private record ResponseContent(List<Part> parts) {
    }

    private record Part(String text) {
    }
}
