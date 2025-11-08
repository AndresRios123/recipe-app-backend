package com.example.recipesapp.controller;

import com.example.recipesapp.dto.ChangePasswordRequest;
import com.example.recipesapp.dto.LoginRequest;
import com.example.recipesapp.dto.MessageResponse;
import com.example.recipesapp.dto.RegisterRequest;
import com.example.recipesapp.dto.UserResponse;
import com.example.recipesapp.model.User;
import com.example.recipesapp.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/*
 * AuthController
 * ---------------
 * Controlador REST que maneja la autenticación y gestión de usuarios.
 * Endpoints:
 *   - /register: registrar un nuevo usuario.
 *   - /login: iniciar sesión (se guarda en la sesión de Spring Security).
 *   - /logout: cerrar sesión.
 *   - /profile: obtener el perfil del usuario autenticado.
 *   - /change-password: cambiar la contraseña.
 *   - /status: verificar si hay una sesión activa.
 *   - /users: listar todos los usuarios (público).
 *   - /users-protected: listar usuarios pero solo si hay sesión activa.
 *   - /user/{id}: obtener usuario por ID.
 */

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(allowCredentials = "true")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    // Registro de nuevo usuario
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest registerRequest) {
        try {
            User newUser = userService.registerUser(
                registerRequest.getUsername(),
                registerRequest.getPassword(),
                registerRequest.getEmail()
            );

            UserResponse response = UserResponse.fromEntity(newUser);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body(new MessageResponse(ex.getMessage()));
        }
    }

    // Inicio de sesión
    @PostMapping("/login")
    public ResponseEntity<MessageResponse> login(
        @Valid @RequestBody LoginRequest loginRequest,
        HttpServletRequest request
    ) {
        boolean credentialsValid = userService.authenticateUser(
            loginRequest.getUsername(),
            loginRequest.getPassword()
        );

        if (!credentialsValid) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new MessageResponse("Credenciales incorrectas"));
        }

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
            loginRequest.getUsername(),
            null,
            Collections.emptyList()
        );

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);

        HttpSession session = request.getSession(true);
        session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, context);
        session.setAttribute("username", loginRequest.getUsername());
        session.setMaxInactiveInterval(30 * 60); // 30 minutos

        return ResponseEntity.ok(new MessageResponse("Login exitoso. Bienvenido " + loginRequest.getUsername()));
    }

    // Cierre de sesión
    @PostMapping("/logout")
    public ResponseEntity<MessageResponse> logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate(); // invalida la sesión
        }
        SecurityContextHolder.clearContext(); // limpia el contexto de seguridad
        return ResponseEntity.ok(new MessageResponse("Logout exitoso"));
    }

    // Obtiene el perfil del usuario logueado
    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("username") == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new MessageResponse("Debes estar logueado para ver tu perfil"));
        }

        String username = (String) session.getAttribute("username");
        Optional<User> userOptional = userService.findByUsername(username);
        if (userOptional.isEmpty()) {
            session.invalidate();
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new MessageResponse("Sesion invalida. Inicia nuevamente"));
        }

        return ResponseEntity.ok(UserResponse.fromEntity(userOptional.get()));
    }

    // Cambiar contraseña del usuario autenticado
    @PostMapping("/change-password")
    public ResponseEntity<MessageResponse> changePassword(
        @Valid @RequestBody ChangePasswordRequest changePasswordRequest,
        HttpServletRequest request
    ) {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("username") == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new MessageResponse("Debes estar logueado para cambiar tu contrasena"));
        }

        String username = (String) session.getAttribute("username");
        boolean updated = userService.updatePassword(
            username,
            changePasswordRequest.getCurrentPassword(),
            changePasswordRequest.getNewPassword()
        );

        if (!updated) {
            return ResponseEntity.badRequest()
                .body(new MessageResponse("La contrasena actual es incorrecta"));
        }

        return ResponseEntity.ok(new MessageResponse("Contrasena actualizada exitosamente"));
    }

    // Revisa si existe una sesión activa
    @GetMapping("/status")
    public ResponseEntity<MessageResponse> getSessionStatus(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("username") != null) {
            String username = (String) session.getAttribute("username");
            return ResponseEntity.ok(new MessageResponse("Sesion activa para usuario: " + username));
        }
        return ResponseEntity.ok(new MessageResponse("No hay sesion activa"));
    }

    // Lista todos los usuarios (sin proteger)
    @GetMapping("/users")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        List<UserResponse> users = userService.getAllUsers().stream()
            .map(UserResponse::fromEntity)
            .collect(Collectors.toList());
        return ResponseEntity.ok(users);
    }

    // Lista usuarios pero requiere sesión activa
    @GetMapping("/users-protected")
    public ResponseEntity<List<UserResponse>> getAllUsersProtected(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("username") == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        List<UserResponse> users = userService.getAllUsers().stream()
            .map(UserResponse::fromEntity)
            .collect(Collectors.toList());
        return ResponseEntity.ok(users);
    }

    // Obtiene un usuario por su ID
    @GetMapping("/user/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        Optional<User> userOptional = userService.getUserById(id);
        if (userOptional.isPresent()) {
            return ResponseEntity.ok(UserResponse.fromEntity(userOptional.get()));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(new MessageResponse("Usuario con ID " + id + " no encontrado"));
    }
}

