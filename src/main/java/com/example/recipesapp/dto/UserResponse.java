package com.example.recipesapp.dto;

import com.example.recipesapp.model.User;

/*
 * UserResponse
 * -------------
 * DTO (Data Transfer Object) usado para exponer información segura de un usuario
 * al cliente. 
 * Incluye únicamente los datos básicos (id, username, email) y omite
 * información sensible como la contraseña.
 */
public class UserResponse {

    private Long id;
    private String username;
    private String email;

    public UserResponse() {
    }

    public UserResponse(Long id, String username, String email) {
        this.id = id;
        this.username = username;
        this.email = email;
    }

    // Método de ayuda que convierte una entidad User en un DTO UserResponse
    public static UserResponse fromEntity(User user) {
        return new UserResponse(user.getId(), user.getUsername(), user.getEmail());
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "UserResponse{id=" + id + ", username='" + username + "', email='" + email + "'}";
    }
}
