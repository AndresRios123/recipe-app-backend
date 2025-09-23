package com.example.recipesapp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/*
 * ChangePasswordRequest
 * ---------------------
 * DTO (Data Transfer Object) usado para solicitar un cambio de contraseña
 * del usuario autenticado en la sesión actual.
 * Contiene la contraseña actual y la nueva contraseña que se quiere establecer.
 */
public class ChangePasswordRequest {

    @NotBlank(message = "La contrasena actual es obligatoria")
    private String currentPassword;

    @NotBlank(message = "La nueva contrasena es obligatoria")
    @Size(min = 6, message = "La nueva contrasena debe tener al menos 6 caracteres")
    private String newPassword;

    public ChangePasswordRequest() {
    }

    public ChangePasswordRequest(String currentPassword, String newPassword) {
        this.currentPassword = currentPassword;
        this.newPassword = newPassword;
    }

    public String getCurrentPassword() {
        return currentPassword;
    }

    public void setCurrentPassword(String currentPassword) {
        this.currentPassword = currentPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    @Override
    public String toString() {
        // Se ocultan los valores de las contraseñas para evitar exponerlas en logs
        return "ChangePasswordRequest{currentPassword='***', newPassword='***'}";
    }
}
