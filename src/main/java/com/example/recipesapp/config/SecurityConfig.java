package com.example.recipesapp.config;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

/*
 * SecurityConfig
 * ---------------
 * Configura una cadena de filtros mínima para que la API funcione con sesiones
 * sin necesidad de autenticadores avanzados.
 * - Define el codificador de contraseñas (BCrypt) reutilizado por UserService.
 * - Centraliza CORS tomando los orígenes permitidos desde app.cors.allowed-origins.
 * - Deshabilita CSRF, form login y HTTP Basic (SPA/JWT no los necesita).
 * - Permite libremente los endpoints de autenticación y las lecturas de recetas,
 *   mientras que el resto requiere que exista un SecurityContext en la sesión.
 */

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final List<String> allowedOrigins;

    public SecurityConfig(@Value("${app.cors.allowed-origins:http://localhost:5173}") String allowedOrigins) {
        this.allowedOrigins = Arrays.stream(allowedOrigins.split(","))
            .map(String::trim)
            .filter(origin -> !origin.isEmpty())
            .collect(Collectors.toList());
    }

    /**
     * Codificador BCrypt con los parámetros por defecto (fuerza 10).
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Cadena principal de filtros de seguridad para proteger la API REST
     * usando autenticación basada en sesión.
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            // Deshabilitamos CSRF porque normalmente en APIs REST no hay formularios
            // y el cliente (por ejemplo, Postman/Front SPA) maneja la autenticación.
            .csrf(AbstractHttpConfigurer::disable)

            // Política de creación de sesiones:
            // IF_REQUIRED -> crea sesión solo si algún componente la necesita.
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))

            // Autorización por rutas:
            // - Estas rutas de auth son públicas.
            // - Cualquier otra requiere estar autenticado.
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/recipes", "/api/recipes/**").permitAll()
                .anyRequest().authenticated()
            )

            // Deshabilitamos el formulario de login por defecto de Spring Security.
            .formLogin(AbstractHttpConfigurer::disable)

            // Deshabilitamos HTTP Basic (no queremos el popup de usuario/contraseña del navegador).
            .httpBasic(AbstractHttpConfigurer::disable);

        return http.build();
    }

    /**
     * Configura CORS tomando los orígenes de app.cors.allowed-origins (separados por coma).
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        List<String> origins = allowedOrigins.isEmpty()
            ? List.of("http://localhost:5173")
            : allowedOrigins;
        if (origins.contains("*")) {
            configuration.setAllowedOriginPatterns(origins);
        } else {
            configuration.setAllowedOrigins(origins);
        }
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

}
