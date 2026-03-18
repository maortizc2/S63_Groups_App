package com.groupsapp.monolito.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

// Este filtro se ejecuta UNA VEZ por cada request que llega al servidor
// Su trabajo: leer el token JWT del header y autenticar al usuario
@Component
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserDetailsServiceImpl userDetailsService;

     // Constructor
    public JwtFilter(JwtUtil jwtUtil, UserDetailsServiceImpl userDetailsService) {
        this.jwtUtil             = jwtUtil;
        this.userDetailsService  = userDetailsService;
    }
    
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        // 1. Leer el header Authorization
        String authHeader = request.getHeader("Authorization");

        // 2. Verificar que tenga el formato "Bearer eyJ..."
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response); // Pasar sin autenticar
            return;
        }

        // 3. Extraer solo el token (quitar el "Bearer ")
        String token = authHeader.substring(7);

        // 4. Validar el token
        if (!jwtUtil.validateToken(token)) {
            filterChain.doFilter(request, response); // Token inválido, pasar sin autenticar
            return;
        }

        // 5. Extraer el email del token
        String email = jwtUtil.getEmailFromToken(token);

        // 6. Solo autenticar si no hay sesión activa ya
        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            // 7. Cargar el usuario desde la BD
            UserDetails userDetails = userDetailsService.loadUserByUsername(email);

            // 8. Crear objeto de autenticación y guardarlo en el contexto de Spring
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,                           // sin credenciales (ya validamos con JWT)
                            userDetails.getAuthorities()
                    );

            authentication.setDetails(
                    new WebAuthenticationDetailsSource().buildDetails(request)
            );

            // 9. Decirle a Spring "este usuario está autenticado"
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        // 10. Continuar con el siguiente filtro o el controller
        filterChain.doFilter(request, response);
    }
}