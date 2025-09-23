package com.example.recipesapp.config;

import com.example.recipesapp.security.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/*
 * SecurityConfig
 * ---------------
 * Configura la seguridad de Spring Security para la API.
 * - Define el codificador de contraseñas (BCrypt).
 * - Registra un AuthenticationProvider basado en DAO que usa nuestro CustomUserDetailsService.
 * - Construye la cadena de filtros (SecurityFilterChain) para:
 *     * Deshabilitar CSRF (útil en APIs REST sin formularios HTML).
 *     * Manejar sesiones (IF_REQUIRED: solo crea sesión cuando se necesita).
 *     * Permitir acceso público a /api/auth/register, /api/auth/login, /api/auth/status.
 *     * Exigir autenticación para el resto de endpoints.
 *     * Deshabilitar form login y HTTP Basic (aquí no se usan).
 *     * Configurar el endpoint de logout.
 */

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // Servicio que carga usuarios desde nuestra capa de seguridad personalizada.
    private final CustomUserDetailsService userDetailsService;

    public SecurityConfig(CustomUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    /**
     * Codificador BCrypt con fuerza 12: mantiene las contraseñas seguras
     * sin sacrificar demasiado rendimiento.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    /**
     * El AuthenticationManager se obtiene de la configuración de Spring
     * y delega en el AuthenticationProvider registrado.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    /**
     * AuthenticationProvider basado en DAO (base de datos):
     * utiliza nuestro CustomUserDetailsService y el codificador de contraseñas.
     */
    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService); // de dónde se cargan los usuarios
        provider.setPasswordEncoder(passwordEncoder());     // cómo se validan las contraseñas
        return provider;
    }

    /**
     * Cadena principal de filtros de seguridad para proteger la API REST
     * usando autenticación basada en sesión.
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // Deshabilitamos CSRF porque normalmente en APIs REST no hay formularios
            // y el cliente (por ejemplo, Postman/Front SPA) maneja la autenticación.
            .csrf(AbstractHttpConfigurer::disable)

            // Política de creación de sesiones:
            // IF_REQUIRED -> crea sesión solo si algún componente la necesita.
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))

            // Registramos nuestro proveedor de autenticación (DAO + UserDetailsService).
            .authenticationProvider(daoAuthenticationProvider())

            // Autorización por rutas:
            // - Estas rutas de auth son públicas.
            // - Cualquier otra requiere estar autenticado.
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/register", "/api/auth/login", "/api/auth/status").permitAll()
                .anyRequest().authenticated()
            )

            // Deshabilitamos el formulario de login por defecto de Spring Security.
            .formLogin(AbstractHttpConfigurer::disable)

            // Deshabilitamos HTTP Basic (no queremos el popup de usuario/contraseña del navegador).
            .httpBasic(AbstractHttpConfigurer::disable)

            // Configuramos el logout:
            // - URL para cerrar sesión.
            // - Invalida la sesión y limpia la autenticación.
            // - Elimina la cookie JSESSIONID para evitar reutilización.
            .logout(logout -> logout
                .logoutUrl("/api/auth/logout")
                .invalidateHttpSession(true)
                .clearAuthentication(true)
                .deleteCookies("JSESSIONID")
            );

        return http.build();
    }
}
