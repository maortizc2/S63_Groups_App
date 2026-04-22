package com.groupsapp.monolito.service;

import com.groupsapp.monolito.dto.auth.AuthResponse;
import com.groupsapp.monolito.dto.auth.LoginRequest;
import com.groupsapp.monolito.dto.auth.RegisterRequest;
import com.groupsapp.monolito.model.User;
import com.groupsapp.monolito.repository.UserRepository;
import com.groupsapp.monolito.security.JwtUtil;
import com.groupsapp.monolito.grpc.PresenceGrpcClient;  
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authManager;
    private final PresenceGrpcClient presenceClient;
    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    public AuthService(UserRepository userRepository,
                    PasswordEncoder passwordEncoder,
                    JwtUtil jwtUtil,
                    AuthenticationManager authManager,
                    PresenceGrpcClient presenceClient) {
        this.userRepository  = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil         = jwtUtil;
        this.authManager     = authManager;
        this.presenceClient  = presenceClient;
    }

    // ── register  ────────────────────────────────────────────────────────────────────
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail()))
            throw new RuntimeException("El email ya está registrado");
        if (userRepository.existsByUsername(request.getUsername()))
            throw new RuntimeException("El username ya está en uso");

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setStatus(User.UserStatus.ONLINE);

        User saved = userRepository.save(user);
        String token = jwtUtil.generateToken(saved.getId(), saved.getEmail());
        return new AuthResponse(token, saved.getId(), saved.getUsername(), saved.getEmail());
    }

    // ── login ────────────────────────────────────────────────────────────────────
    public AuthResponse login(LoginRequest request) {
        authManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // 1. Estado persistente en PostgreSQL (ya lo tenías)
        user.setStatus(User.UserStatus.ONLINE);
        userRepository.save(user);

        // 2. Estado rápido en presence-service (nuevo)
        try {
            presenceClient.setOnline(user.getId());
        } catch (Exception e) {
            // Si el presence-service está caído, el login igual funciona
            log.warn("Could not update presence on login for userId={}: {}", user.getId(), e.getMessage());
        }

        String token = jwtUtil.generateToken(user.getId(), user.getEmail());
        return new AuthResponse(token, user.getId(), user.getUsername(), user.getEmail());
    }

    // ── logout ───────────────────────────────────────────────────────────────────
    public void logout(String email) {
        userRepository.findByEmail(email).ifPresent(user -> {
            // 1. Estado persistente en PostgreSQL (ya lo tenías)
            user.setStatus(User.UserStatus.OFFLINE);
            userRepository.save(user);

            // 2. Estado rápido en presence-service (nuevo)
            try {
                presenceClient.setOffline(user.getId());
            } catch (Exception e) {
                log.warn("Could not update presence on logout for userId={}: {}", user.getId(), e.getMessage());
            }
        });
    }
}