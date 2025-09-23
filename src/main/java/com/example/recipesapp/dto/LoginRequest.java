package com.example.recipesapp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/*
 * LoginRequest
 * ------------
 * DTO (Data Transfer Object) usado para el endpoint de login.
 * Contiene el nombre de usuario y la contraseña que se envían
 * desde el cliente para autenticar al usuario en la aplicación.
 */
public class LoginRequest {

    @NotBlank(message = "El nombre de usuario es obligatorio")
    @Size(min = 3, max = 50, message = "El nombre de usuario debe tener entre 3 y 50 caracteres")
    private String username;

    @NotBlank(message = "La contrasena es obligatoria")
    @Size(min = 6, message = "La contrasena debe tener al menos 6 caracteres")
    private String password;

    public LoginRequest() {
    }

    public LoginRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        // Por seguridad, no se muestra la contraseña en el toString.
        return "LoginRequest{username='" + username + "'}";
    }
}
