package com.example.recipesapp.repository;

import com.example.recipesapp.model.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/*
 * UserRepository
 * ---------------
 * Repositorio JPA para la entidad User.
 * Extiende JpaRepository, lo que provee métodos CRUD básicos automáticamente.
 * Además, se definen métodos personalizados para buscar usuarios por
 * username, email o validar su existencia.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Busca un usuario por su email
    User findByEmail(String email);

    // Busca un usuario por su nombre de usuario (puede o no estar presente)
    Optional<User> findByUsername(String username);

    // Verifica si ya existe un usuario con ese nombre
    boolean existsByUsername(String username);

    // Verifica si ya existe un usuario con ese email
    boolean existsByEmail(String email);

    // Busca un usuario por username o email (si coincide con cualquiera de los dos)
    Optional<User> findByUsernameOrEmail(String username, String email);
}
