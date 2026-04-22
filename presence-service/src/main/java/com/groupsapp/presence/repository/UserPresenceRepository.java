package com.groupsapp.presence.repository;

import com.groupsapp.presence.model.UserPresence;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserPresenceRepository extends JpaRepository<UserPresence, Long> {
    // JpaRepository<T, ID> ya nos da save, findById, delete, etcétera.
    // Por ahora no necesitamos métodos adicionales personalizados.
}