package com.groupsapp.monolito.service;

import com.groupsapp.monolito.dto.message.MessageDTO;
import com.groupsapp.monolito.dto.message.SendMessageRequest;
import com.groupsapp.monolito.grpc.PresenceGrpcClient;    
import com.groupsapp.monolito.model.*;
import com.groupsapp.monolito.repository.*;
import com.groupsapp.presence.grpc.PresenceResponse;      
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MessageService {

    private static final Logger log = LoggerFactory.getLogger(MessageService.class);

    private final MessageRepository messageRepository;
    private final MessageStatusRepository messageStatusRepository;
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final FileMetadataRepository fileRepository;
    private final PresenceGrpcClient presenceClient;

    public MessageService(MessageRepository messageRepository,
                          MessageStatusRepository messageStatusRepository,
                          UserRepository userRepository,
                          ChannelRepository channelRepository,
                          GroupMemberRepository groupMemberRepository,
                          FileMetadataRepository fileRepository,
                          PresenceGrpcClient presenceClient) {
        this.messageRepository       = messageRepository;
        this.messageStatusRepository = messageStatusRepository;
        this.userRepository          = userRepository;
        this.channelRepository       = channelRepository;
        this.groupMemberRepository   = groupMemberRepository;
        this.fileRepository          = fileRepository;
        this.presenceClient          = presenceClient;
    }

    @Transactional
    public MessageDTO sendMessage(SendMessageRequest request, String senderEmail) {
        User sender = userRepository.findByEmail(senderEmail)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Message message = new Message();
        message.setSender(sender);
        message.setType(request.getType());
        message.setContent(request.getContent());

        if (request.getChannelId() != null) {
            Channel channel = channelRepository.findById(request.getChannelId())
                    .orElseThrow(() -> new RuntimeException("Canal no encontrado"));
            if (!groupMemberRepository.existsByUserAndGroup(sender, channel.getGroup()))
                throw new RuntimeException("No eres miembro de este grupo");
            if (channel.getReadOnly()) {
                GroupMember member = groupMemberRepository
                        .findByUserAndGroup(sender, channel.getGroup()).orElseThrow();
                if (member.getRole() == GroupMember.MemberRole.MEMBER)
                    throw new RuntimeException("Este canal es de solo lectura");
            }
            message.setChannel(channel);
        } else if (request.getReceiverId() != null) {
            User receiver = userRepository.findById(request.getReceiverId())
                    .orElseThrow(() -> new RuntimeException("Destinatario no encontrado"));
            message.setReceiver(receiver);
        } else {
            throw new RuntimeException("Debes especificar channelId o receiverId");
        }

        if (request.getFileId() != null) {
            FileMetadata file = fileRepository.findById(request.getFileId())
                    .orElseThrow(() -> new RuntimeException("Archivo no encontrado"));
            message.setFile(file);
        }

        Message saved = messageRepository.save(message);
        createMessageStatus(saved);   // ← aquí vive la lógica nueva
        return MessageDTO.fromEntity(saved, sender.getId());
    }

    // ── El método clave: ahora distingue DM de canal ──────────────────────────
    @Transactional
    private void createMessageStatus(Message message) {
        if (message.getChannel() != null) {
            // Mensaje de canal: N destinatarios, no consultamos presence por cada uno.
            // deliveredAt queda null — se marca cuando el usuario abre el canal (markAllAsRead).
            groupMemberRepository.findByGroup(message.getChannel().getGroup()).stream()
                    .filter(m -> !m.getUser().getId().equals(message.getSender().getId()))
                    .forEach(m -> {
                        MessageStatus status = new MessageStatus();
                        status.setMessage(message);
                        status.setUser(m.getUser());
                        // deliveredAt = null intencionalmente
                        messageStatusRepository.save(status);
                    });

        } else if (message.getReceiver() != null) {
            // DM: consultamos presence para saber si entregar al instante
            MessageStatus status = new MessageStatus();
            status.setMessage(message);
            status.setUser(message.getReceiver());

            boolean receiverIsOnline = checkReceiverOnline(message.getReceiver().getId());
            if (receiverIsOnline) {
                status.markAsDelivered();   // deliveredAt = ahora
                log.debug("DM delivered instantly — receiver userId={} is online",
                          message.getReceiver().getId());
            } else {
                log.debug("DM queued for delivery — receiver userId={} is offline",
                          message.getReceiver().getId());
                // deliveredAt queda null; un proceso futuro (Día 4 con SQS) lo marcará
            }

            messageStatusRepository.save(status);
        }
    }

    // ── Helper: llama gRPC y absorbe fallos ───────────────────────────────────
    private boolean checkReceiverOnline(Long userId) {
        try {
            PresenceResponse response = presenceClient.checkPresence(userId);
            return response.getOnline();
        } catch (Exception e) {
            // Si el presence-service está caído, asumimos offline (conservador)
            log.warn("Presence check failed for userId={}, assuming offline: {}", userId, e.getMessage());
            return false;
        }
    }

    // ── El resto no cambia ────────────────────────────────────────────────────

    @Transactional
    public List<MessageDTO> getChannelHistory(Long channelId, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new RuntimeException("Canal no encontrado"));
        if (!groupMemberRepository.existsByUserAndGroup(user, channel.getGroup()))
            throw new RuntimeException("No tienes acceso a este canal");
        messageStatusRepository.markAllAsRead(user.getId(), channelId, LocalDateTime.now());
        return messageRepository.findByChannelIdOrderByCreatedAtAsc(channelId)
                .stream().map(m -> MessageDTO.fromEntity(m, user.getId()))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<MessageDTO> getDirectHistory(Long otherUserId, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        if (!userRepository.existsById(otherUserId))
            throw new RuntimeException("Usuario no encontrado");
        return messageRepository.findDirectMessages(user.getId(), otherUserId)
                .stream().map(m -> MessageDTO.fromEntity(m, user.getId()))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public long getUnreadCount(Long channelId, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        return messageRepository.countUnreadMessages(channelId, user.getId());
    }
}