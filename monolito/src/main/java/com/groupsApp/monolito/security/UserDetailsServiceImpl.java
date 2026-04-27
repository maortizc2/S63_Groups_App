package com.groupsapp.monolito.security;

import com.groupsapp.monolito.model.User;
import com.groupsapp.monolito.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

// Spring Security usa esta clase para cargar el usuario
// cuando necesita verificar credenciales
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

        private final UserRepository userRepository;

        public UserDetailsServiceImpl(UserRepository userRepository) {
                this.userRepository = userRepository;
        }
    // Spring llama a este método automáticamente durante la autenticación
    // Le pasamos el email como "username"
        @Override
        public UserDetails loadUserByUsername(String email)
                throws UsernameNotFoundException {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "Usuario no encontrado con email: " + email
                ));

        // Convertir nuestra entidad User → UserDetails de Spring Security
        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                List.of(new SimpleGrantedAuthority("ROLE_USER")) // rol básico
        );
        }
}