package com.example.recipesapp.config;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class WebCorsConfig {

    @Value("${app.cors.allowed-origins:http://localhost:5173}")
    private String allowedOriginsProperty;

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.setAllowedOriginPatterns(resolveAllowedOrigins());
        config.addAllowedHeader(CorsConfiguration.ALL);
        config.addAllowedMethod(CorsConfiguration.ALL);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return new CorsFilter(source);
    }

    private List<String> resolveAllowedOrigins() {
        List<String> patterns = Arrays.stream(allowedOriginsProperty.split(","))
            .map(String::trim)
            .filter(origin -> !origin.isBlank())
            .collect(Collectors.toList());

        if (patterns.isEmpty()) {
            patterns = List.of("http://localhost:5173");
        }

        return patterns;
    }
}
