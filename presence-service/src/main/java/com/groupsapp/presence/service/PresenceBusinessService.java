package com.groupsapp.presence.service;

import com.groupsapp.presence.model.UserPresence;
import com.groupsapp.presence.repository.UserPresenceRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;

/**
 * Lógica de negocio del presence-service.
 *
 * Separada deliberadamente del adapter gRPC (PresenceServiceGrpcImpl).
 * Esto sigue el principio de Hexagonal Architecture / Ports & Adapters:
 *   - El "puerto" es esta clase de servicio.
 *   - El "adapter" es la clase gRPC que adapta peticiones gRPC a llamadas del puerto.
 *
 * Ventaja: si mañana quisiéramos exponer lo mismo por REST, solo
 * agregamos otro adapter sin tocar esta lógica.
 */
@Service
public class PresenceBusinessService {

    private static final Logger log = LoggerFactory.getLogger(PresenceBusinessService.class);

    private final UserPresenceRepository repository;

    public PresenceBusinessService(UserPresenceRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public UserPresence markOnline(Long userId) {
        UserPresence presence = repository.findById(userId)
                .orElse(new UserPresence(userId, false, null));
        presence.setOnline(true);
        presence.setLastSeen(Instant.now());
        UserPresence saved = repository.save(presence);
        log.info("User {} marked ONLINE at {}", userId, saved.getLastSeen());
        return saved;
    }

    @Transactional
    public UserPresence markOffline(Long userId) {
        UserPresence presence = repository.findById(userId)
                .orElse(new UserPresence(userId, false, null));
        presence.setOnline(false);
        presence.setLastSeen(Instant.now());
        UserPresence saved = repository.save(presence);
        log.info("User {} marked OFFLINE at {}", userId, saved.getLastSeen());
        return saved;
    }

    @Transactional(readOnly = true)
    public UserPresence getPresence(Long userId) {
        Optional<UserPresence> existing = repository.findById(userId);
        if (existing.isPresent()) {
            return existing.get();
        }
        // Usuario que nunca hemos visto: consideramos offline con lastSeen nulo.
        log.debug("User {} never seen, returning default offline", userId);
        return new UserPresence(userId, false, null);
    }
}