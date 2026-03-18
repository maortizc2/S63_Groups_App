package com.groupsapp.monolito.repository;

import com.groupsapp.monolito.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Buscar usuario por email (para login)
    Optional<User> findByEmail(String email);

    // Buscar usuario por username
    Optional<User> findByUsername(String username);

    // Verificar si ya existe ese email (para registro)
    boolean existsByEmail(String email);

    // Verificar si ya existe ese username
    boolean existsByUsername(String username);
}