package com.example.recipesapp.dto;

/*
 * MessageResponse
 * ----------------
 * DTO (Data Transfer Object) usado para devolver mensajes de texto plano al cliente.
 * Generalmente se utiliza para respuestas simples como confirmaciones,
 * errores o notificaciones de estado.
 */
public class MessageResponse {

    private String message;

    public MessageResponse() {
    }

    public MessageResponse(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "MessageResponse{message='" + message + "'}";
    }
}
