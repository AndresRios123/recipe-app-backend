package com.example.recipesapp.service;

import com.example.recipesapp.exception.ResourceNotFoundException;
import com.example.recipesapp.exception.UnauthorizedException;
import com.example.recipesapp.model.User;
import com.example.recipesapp.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

/**
 * Servicio de aplicación que encapsula la obtención del usuario autenticado
 * actual. (Patrón Facade/Service que centraliza el acceso al contexto de seguridad).
 */
@Service
public class CurrentUserService {

    private final UserRepository userRepository;

    public CurrentUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Recupera la entidad User asociada a la sesion actual.
     */
    public User getCurrentUser() {
        SecurityContext context = SecurityContextHolder.getContext();
        Authentication authentication = context != null ? context.getAuthentication() : null;
        if (authentication == null
            || !authentication.isAuthenticated()
            || "anonymousUser".equals(authentication.getPrincipal())) {
            throw new UnauthorizedException("No existe un usuario autenticado en la sesion actual");
        }
        String username = authentication.getName();
        return userRepository.findByUsername(username)
            .orElseThrow(() -> new ResourceNotFoundException("Usuario " + username + " no encontrado"));
    }
}
