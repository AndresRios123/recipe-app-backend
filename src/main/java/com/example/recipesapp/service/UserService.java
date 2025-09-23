package com.example.recipesapp.service;

import com.example.recipesapp.model.User;
import com.example.recipesapp.repository.UserRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/*
 * UserService
 * ------------
 * Servicio encargado de la lógica de negocio relacionada con los usuarios.
 * Aquí se centralizan las operaciones de:
 *   - Registrar nuevos usuarios (con verificación de duplicados).
 *   - Actualizar datos de usuario.
 *   - Eliminar usuarios.
 *   - Autenticación de credenciales (username/email + password).
 *   - Cambiar contraseña.
 *   - Validar existencia de username o email.
 */
@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // Obtener todos los usuarios
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // Buscar usuario por ID
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    // Buscar usuario por username
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    // Buscar usuario por email
    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    // Registrar un nuevo usuario
    @Transactional
    public User registerUser(String username, String password, String email) {
        if (userRepository.existsByUsername(username)) {
            throw new RuntimeException("El nombre de usuario " + username + " ya esta en uso");
        }
        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("El email " + email + " ya esta registrado");
        }

        User newUser = new User();
        newUser.setUsername(username);
        newUser.setEmail(email);
        // La contraseña se guarda en la BD de forma cifrada con PasswordEncoder
        newUser.setPassword(passwordEncoder.encode(password));
        return userRepository.save(newUser);
    }

    // Actualizar datos de un usuario existente
    @Transactional
    public User updateUser(Long id, String username, String email) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Usuario con id " + id + " no encontrado"));

        // Verifica si el nuevo username ya está ocupado por otro usuario
        if (!user.getUsername().equals(username) && userRepository.existsByUsername(username)) {
            throw new RuntimeException("El nombre de usuario " + username + " ya esta en uso");
        }
        // Verifica si el nuevo email ya está registrado por otro usuario
        if (!user.getEmail().equals(email) && userRepository.existsByEmail(email)) {
            throw new RuntimeException("El email " + email + " ya esta registrado");
        }

        user.setUsername(username);
        user.setEmail(email);
        return userRepository.save(user);
    }

    // Eliminar usuario por ID
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    // Verificar si un username ya existe
    public boolean isUsernameExists(String username) {
        return userRepository.existsByUsername(username);
    }

    // Verificar si un email ya existe
    public boolean isEmailExist(String email) {
        return userRepository.existsByEmail(email);
    }

    // Autenticar usuario con username + password
    public boolean authenticateUser(String username, String password) {
        Optional<User> userOptional = userRepository.findByUsername(username);
        if (userOptional.isEmpty()) {
            return false;
        }
        User user = userOptional.get();
        // Compara el password plano con el cifrado almacenado
        return passwordEncoder.matches(password, user.getPassword());
    }

    // Autenticar usuario con username o email + password
    public boolean authenticateUserByUsernameOrEmail(String usernameOrEmail, String password) {
        Optional<User> userOptional = userRepository.findByUsernameOrEmail(usernameOrEmail, usernameOrEmail);
        if (userOptional.isEmpty()) {
            return false;
        }
        User user = userOptional.get();
        return passwordEncoder.matches(password, user.getPassword());
    }

    // Cambiar la contraseña del usuario autenticado
    @Transactional
    public boolean updatePassword(String username, String oldPassword, String newPassword) {
        if (!authenticateUser(username, oldPassword)) {
            return false; // Contraseña actual incorrecta
        }
        Optional<User> userOptional = userRepository.findByUsername(username);
        if (userOptional.isEmpty()) {
            return false;
        }
        User user = userOptional.get();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        return true;
    }
}
