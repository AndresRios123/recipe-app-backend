package com.example.recipesapp.security;

import com.example.recipesapp.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/*
 * CustomUserDetailsService
 * ------------------------
 * Servicio que carga usuarios desde la base de datos para la autenticación
 * de Spring Security.
 *
 * Implementa la interfaz UserDetailsService, requerida por Spring Security,
 * que busca un usuario por su username y devuelve un objeto UserDetails
 * (en este caso, CustomUserDetails).
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Busca el usuario en la base de datos y lo adapta a CustomUserDetails.
        // Si no existe, lanza una excepción UsernameNotFoundException.
        return userRepository.findByUsername(username)
            .map(CustomUserDetails::new)
            .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));
    }
}
