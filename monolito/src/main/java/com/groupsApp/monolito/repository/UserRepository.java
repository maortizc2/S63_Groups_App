package com.groupsapp.monolito.repository;

import com.groupsapp.monolito.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);
    Optional<User> findByUsername(String username);
    boolean existsByEmail(String email);
    boolean existsByUsername(String username);

    // Buscar por username o email (para el modal de buscar usuarios)
    @Query("SELECT u FROM User u WHERE " +
           "LOWER(u.username) LIKE LOWER(CONCAT('%', :q, '%')) OR " +
           "LOWER(u.email)    LIKE LOWER(CONCAT('%', :q, '%'))")
    List<User> searchByUsernameOrEmail(@Param("q") String q);
}
