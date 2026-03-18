package com.groupsapp.monolito.repository;

import com.groupsapp.monolito.model.Message;
import com.groupsapp.monolito.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    // Historial de mensajes de un canal (ordenado de más antiguo a más nuevo)
    List<Message> findByChannelIdOrderByCreatedAtAsc(Long channelId);

    // Conversación privada entre dos usuarios (en ambas direcciones)
    @Query("""
        SELECT m FROM Message m
        WHERE (m.sender.id = :userId1 AND m.receiver.id = :userId2)
            OR (m.sender.id = :userId2 AND m.receiver.id = :userId1)
        ORDER BY m.createdAt ASC
        """)
    List<Message> findDirectMessages(
        @Param("userId1") Long userId1,
        @Param("userId2") Long userId2
    );

    // Mensajes no leídos para un usuario en un canal
    @Query("""
        SELECT m FROM Message m
        WHERE m.channel.id = :channelId
            AND m.sender.id != :userId
            AND NOT EXISTS (
                SELECT ms FROM MessageStatus ms
                WHERE ms.message = m
                AND ms.user.id = :userId
                AND ms.readAt IS NOT NULL
            )
        ORDER BY m.createdAt ASC
        """)
    List<Message> findUnreadMessagesInChannel(
        @Param("channelId") Long channelId,
        @Param("userId") Long userId
    );

    // Contar mensajes no leídos (para el badge de notificaciones)
    @Query("""
        SELECT COUNT(m) FROM Message m
        WHERE m.channel.id = :channelId
            AND m.sender.id != :userId
            AND NOT EXISTS (
                SELECT ms FROM MessageStatus ms
                WHERE ms.message = m
                AND ms.user.id = :userId
                AND ms.readAt IS NOT NULL
            )
        """)
    long countUnreadMessages(
        @Param("channelId") Long channelId,
        @Param("userId") Long userId
    );
}