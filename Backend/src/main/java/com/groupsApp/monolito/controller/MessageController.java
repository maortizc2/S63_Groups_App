package com.groupsapp.monolito.controller;

import com.groupsapp.monolito.dto.ApiResponse;
import com.groupsapp.monolito.dto.message.MessageDTO;
import com.groupsapp.monolito.dto.message.SendMessageRequest;
import com.groupsapp.monolito.model.User;
import com.groupsapp.monolito.repository.UserRepository;
import com.groupsapp.monolito.service.MessageService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/messages")
public class MessageController {

    private final MessageService         messageService;
    private final SimpMessagingTemplate  messagingTemplate;
    private final UserRepository         userRepository;

    public MessageController(MessageService messageService,
                             SimpMessagingTemplate messagingTemplate,
                             UserRepository userRepository) {
        this.messageService    = messageService;
        this.messagingTemplate = messagingTemplate;
        this.userRepository    = userRepository;
    }

    // POST /api/messages
    @PostMapping
    public ResponseEntity<ApiResponse<MessageDTO>> sendMessage(
            @Valid @RequestBody SendMessageRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {

        MessageDTO message = messageService.sendMessage(request, userDetails.getUsername());

        if (message.getChannelId() != null) {
            // Broadcast a todos los suscritos al canal
            messagingTemplate.convertAndSend(
                "/topic/channel/" + message.getChannelId(), message);

        } else if (message.getReceiverId() != null) {
            // Mensaje directo: convertAndSendToUser necesita el "username" STOMP
            // que en Spring Security es el email (el principal del JWT).
            // Por eso buscamos el email del receptor por su ID.
            userRepository.findById(message.getReceiverId()).ifPresent(receiver -> {
                messagingTemplate.convertAndSendToUser(
                    receiver.getEmail(),       // <-- email, NO el ID numérico
                    "/queue/messages",
                    message
                );
            });
        }

        return ResponseEntity.ok(ApiResponse.ok("Mensaje enviado", message));
    }

    // GET /api/messages/channel/{channelId}
    @GetMapping("/channel/{channelId}")
    public ResponseEntity<ApiResponse<List<MessageDTO>>> getChannelHistory(
            @PathVariable Long channelId,
            @AuthenticationPrincipal UserDetails userDetails) {
        List<MessageDTO> messages =
            messageService.getChannelHistory(channelId, userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.ok("Historial obtenido", messages));
    }

    // GET /api/messages/direct/{userId}
    @GetMapping("/direct/{userId}")
    public ResponseEntity<ApiResponse<List<MessageDTO>>> getDirectHistory(
            @PathVariable Long userId,
            @AuthenticationPrincipal UserDetails userDetails) {
        List<MessageDTO> messages =
            messageService.getDirectHistory(userId, userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.ok("Historial obtenido", messages));
    }

    // GET /api/messages/channel/{channelId}/unread
    @GetMapping("/channel/{channelId}/unread")
    public ResponseEntity<ApiResponse<Long>> getUnreadCount(
            @PathVariable Long channelId,
            @AuthenticationPrincipal UserDetails userDetails) {
        long count = messageService.getUnreadCount(channelId, userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.ok("Mensajes no leidos", count));
    }

    // WebSocket STOMP: /app/chat.send
    @MessageMapping("/chat.send")
    public void handleWebSocketMessage(@Payload SendMessageRequest request, Principal principal) {
        MessageDTO message = messageService.sendMessage(request, principal.getName());
        if (message.getChannelId() != null) {
            messagingTemplate.convertAndSend(
                "/topic/channel/" + message.getChannelId(), message);
        } else if (message.getReceiverId() != null) {
            userRepository.findById(message.getReceiverId()).ifPresent(receiver -> {
                messagingTemplate.convertAndSendToUser(
                    receiver.getEmail(),
                    "/queue/messages",
                    message
                );
            });
        }
    }
}
