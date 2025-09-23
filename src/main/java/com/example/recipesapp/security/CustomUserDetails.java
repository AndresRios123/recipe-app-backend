package com.example.recipesapp.security;

import com.example.recipesapp.model.User;
import java.util.Collection;
import java.util.Collections;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

/*
 * CustomUserDetails
 * -----------------
 * Implementación simple de la interfaz UserDetails de Spring Security,
 * respaldada por la entidad User.
 *
 * Esta clase actúa como un "adaptador" entre nuestro modelo User y
 * el sistema de autenticación de Spring Security.
 */
public class CustomUserDetails implements UserDetails {

    private final User user;

    public CustomUserDetails(User user) {
        this.user = user;
    }

    // Devuelve el id del usuario (dato adicional que no está en UserDetails)
    public Long getId() {
        return user.getId();
    }

    // Devuelve el email del usuario
    public String getEmail() {
        return user.getEmail();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // En este caso no se manejan roles, se devuelve lista vacía
        return Collections.emptyList();
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        // Siempre activo (no se maneja caducidad de cuenta)
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        // Siempre desbloqueado (no se maneja bloqueo de cuenta)
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        // Siempre activas (no se maneja caducidad de credenciales)
        return true;
    }

    @Override
    public boolean isEnabled() {
        // Siempre habilitado (no se maneja estado deshabilitado)
        return true;
    }
}
