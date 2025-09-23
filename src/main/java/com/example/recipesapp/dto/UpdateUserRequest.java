package com.example.recipesapp.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/*
 * UpdateUserRequest
 * ------------------
 * DTO (Data Transfer Object) usado para actualizar los datos de un usuario,
 * específicamente el nombre de usuario y el correo electrónico.
 * Importante: este DTO no maneja cambios de contraseña, solo información básica.
 */
public class UpdateUserRequest {

    @NotBlank(message = "El nombre de usuario es obligatorio")
    @Size(min = 3, max = 50, message = "El nombre de usuario debe tener entre 3 y 50 caracteres")
    private String username;

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El email debe tener un formato valido")
    private String email;

    public UpdateUserRequest() {
    }

    public UpdateUserRequest(String username, String email) {
        this.username = username;
        this.email = email;
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
}
