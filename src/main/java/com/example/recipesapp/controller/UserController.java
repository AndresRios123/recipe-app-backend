package com.example.recipesapp.controller;

import com.example.recipesapp.dto.MessageResponse;
import com.example.recipesapp.dto.RegisterRequest;
import com.example.recipesapp.dto.UpdateUserRequest;
import com.example.recipesapp.dto.UserResponse;
import com.example.recipesapp.model.User;
import com.example.recipesapp.service.UserService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/*
 * UserController
 * ---------------
 * Controlador REST para la gestión de usuarios.
 * Endpoints:
 *   - POST /api/users         → Crear un nuevo usuario.
 *   - GET /api/users          → Obtener la lista de todos los usuarios.
 *   - GET /api/users/{id}     → Obtener un usuario por su ID.
 *   - PUT /api/users/{id}     → Actualizar los datos de un usuario existente.
 *   - DELETE /api/users/{id}  → Eliminar un usuario por su ID.
 */

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // Crear un nuevo usuario
    @PostMapping
    public ResponseEntity<?> createUser(@Valid @RequestBody RegisterRequest request) {
        try {
            User created = userService.registerUser(request.getUsername(), request.getPassword(), request.getEmail());
            return ResponseEntity.status(HttpStatus.CREATED).body(UserResponse.fromEntity(created));
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body(new MessageResponse(ex.getMessage()));
        }
    }

    // Obtener todos los usuarios
    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        List<UserResponse> users = userService.getAllUsers().stream()
            .map(UserResponse::fromEntity)
            .collect(Collectors.toList());
        return ResponseEntity.ok(users);
    }

    // Obtener un usuario por su ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        Optional<User> userOptional = userService.getUserById(id);
        if (userOptional.isPresent()) {
            return ResponseEntity.ok(UserResponse.fromEntity(userOptional.get()));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(new MessageResponse("Usuario con ID " + id + " no encontrado"));
    }

    // Actualizar usuario existente
    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @Valid @RequestBody UpdateUserRequest request) {
        try {
            User updated = userService.updateUser(id, request.getUsername(), request.getEmail());
            return ResponseEntity.ok(UserResponse.fromEntity(updated));
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body(new MessageResponse(ex.getMessage()));
        }
    }

    // Eliminar usuario por ID
    @DeleteMapping("/{id}")
    public ResponseEntity<MessageResponse> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok(new MessageResponse("Usuario con id " + id + " eliminado correctamente"));
    }
}
