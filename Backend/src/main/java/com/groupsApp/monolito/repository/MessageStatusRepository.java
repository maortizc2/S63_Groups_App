package com.groupsapp.monolito.repository;

import com.groupsapp.monolito.model.Message;
import com.groupsapp.monolito.model.MessageStatus;
import com.groupsapp.monolito.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface MessageStatusRepository extends JpaRepository<MessageStatus, Long> {

    // Estado de un mensaje para un usuario específico
    Optional<MessageStatus> findByMessageAndUser(Message message, User user);

    // Todos los estados de un mensaje (para saber quién lo leyó)
    List<MessageStatus> findByMessage(Message message);

    // Marcar todos los mensajes de un canal como entregados a un usuario
    @Modifying
    @Query("""
        UPDATE MessageStatus ms
        SET ms.deliveredAt = :now
        WHERE ms.user.id = :userId
            AND ms.message.channel.id = :channelId
            AND ms.deliveredAt IS NULL
        """)
    void markAllAsDelivered(
        @Param("userId") Long userId,
        @Param("channelId") Long channelId,
        @Param("now") LocalDateTime now
    );

    // Marcar todos los mensajes de un canal como leídos
    @Modifying
    @Query("""
        UPDATE MessageStatus ms
        SET ms.readAt = :now, ms.deliveredAt = :now
        WHERE ms.user.id = :userId
            AND ms.message.channel.id = :channelId
            AND ms.readAt IS NULL
        """)
    void markAllAsRead(
        @Param("userId") Long userId,
        @Param("channelId") Long channelId,
        @Param("now") LocalDateTime now
    );
}