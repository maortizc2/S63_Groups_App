package com.groupsapp.monolito.service;

import com.groupsapp.monolito.dto.auth.AuthResponse;
import com.groupsapp.monolito.dto.auth.LoginRequest;
import com.groupsapp.monolito.dto.auth.RegisterRequest;
import com.groupsapp.monolito.model.User;
import com.groupsapp.monolito.repository.UserRepository;
import com.groupsapp.monolito.security.JwtUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authManager;

    public AuthService(UserRepository userRepository,
                    PasswordEncoder passwordEncoder,
                    JwtUtil jwtUtil,
                    AuthenticationManager authManager) {
        this.userRepository  = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil         = jwtUtil;
        this.authManager     = authManager;
    }

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

    public AuthResponse login(LoginRequest request) {
        authManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        user.setStatus(User.UserStatus.ONLINE);
        userRepository.save(user);
        String token = jwtUtil.generateToken(user.getId(), user.getEmail());
        return new AuthResponse(token, user.getId(), user.getUsername(), user.getEmail());
    }

    public void logout(String email) {
        userRepository.findByEmail(email).ifPresent(user -> {
            user.setStatus(User.UserStatus.OFFLINE);
            userRepository.save(user);
        });
    }
}