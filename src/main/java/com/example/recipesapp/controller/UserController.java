package com.example.recipesapp.controller;

import com.example.recipesapp.model.User;
import com.example.recipesapp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController // Indica que esta clase será un controlador REST
@RequestMapping("/api/users") // Ruta base para todos los endpoints
public class UserController {

    @Autowired
    private UserService userService;

    // Crear un usuario
    @PostMapping
    public User createUser(@RequestBody User username) {
        return userService.saveUser(username);
    }

    // Listar todos los usuarios
    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    // Buscar usuario por ID
    @GetMapping("/{id}")
    public Optional<User> getUserById(@PathVariable Long id) {
        return userService.getUserById(id);
    }

    // Actualizar un usuario
    @PutMapping("/{id}")
    public User updateUser(@PathVariable Long id, @RequestBody User user) {
        user.setId(id); // Nos aseguramos de actualizar el usuario correcto
        return userService.saveUser(user);
    }

    // Eliminar un usuario
    @DeleteMapping("/{id}")
    public String deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return "Usuario con id " + id + " eliminado correctamente.";
    }
}
