package com.groupsapp.monolito.dto.message;

import com.groupsapp.monolito.model.Message;
import com.groupsapp.monolito.model.MessageStatus;

import java.time.LocalDateTime;
import java.util.List;

public class MessageDTO {

    private Long id;
    private String content;
    private Message.MessageType type;
    private LocalDateTime createdAt;

    // Quién lo envió
    private Long senderId;
    private String senderUsername;
    private String senderAvatarUrl;

    // Destino: canal o privado
    private Long channelId;         // null si es privado
    private Long receiverId;        // null si es de canal

    // Archivo adjunto (null si es solo texto)
    private Long fileId;
    private String fileName;
    private String fileUrl;

    // Los "chulitos": SENT, DELIVERED, READ
    private String deliveryStatus;

    // Constructor que convierte entidad → DTO
    public static MessageDTO fromEntity(Message message, Long currentUserId) {
        MessageDTO dto = new MessageDTO();
        dto.setId(message.getId());
        dto.setContent(message.getContent());
        dto.setType(message.getType());
        dto.setCreatedAt(message.getCreatedAt());

        // Datos del remitente
        dto.setSenderId(message.getSender().getId());
        dto.setSenderUsername(message.getSender().getUsername());
        dto.setSenderAvatarUrl(message.getSender().getAvatarUrl());

        // Destino
        if (message.getChannel() != null) {
            dto.setChannelId(message.getChannel().getId());
        }
        if (message.getReceiver() != null) {
            dto.setReceiverId(message.getReceiver().getId());
        }

        // Archivo adjunto
        if (message.getFile() != null) {
            dto.setFileId(message.getFile().getId());
            dto.setFileName(message.getFile().getOriginalName());
            dto.setFileUrl("/api/files/" + message.getFile().getId());
        }

        // Estado de entrega (chulitos)
        dto.setDeliveryStatus(resolveStatus(message.getStatuses(), currentUserId));

        return dto;
    }

    // Lógica de los "chulitos"
    // ✓   = SENT      (enviado, sin confirmar entrega)
    // ✓✓  = DELIVERED (entregado al destinatario)
    // ✓✓🔵 = READ     (leído por el destinatario)
    private static String resolveStatus(List<MessageStatus> statuses, Long currentUserId) {
        if (statuses == null || statuses.isEmpty()) return "SENT";

        boolean anyRead      = statuses.stream().anyMatch(MessageStatus::isRead);
        boolean anyDelivered = statuses.stream().anyMatch(MessageStatus::isDelivered);

        if (anyRead)      return "READ";
        if (anyDelivered) return "DELIVERED";
        return "SENT";
    }

    // Getters y Setters 
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }    

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public Message.MessageType getType() { return type; }
    public void setType(Message.MessageType type) { this.type = type; }

    public LocalDateTime getCreatedAt() { return createdAt; }   
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public Long getSenderId() { return senderId; }
    public void setSenderId(Long senderId) { this.senderId = senderId; }

    public String getSenderUsername() { return senderUsername; }
    public void setSenderUsername(String senderUsername) { this.senderUsername = senderUsername; }

    public String getSenderAvatarUrl() { return senderAvatarUrl; }
    public void setSenderAvatarUrl(String senderAvatarUrl) { this.senderAvatarUrl = senderAvatarUrl; }

    public Long getChannelId() { return channelId; }
    public void setChannelId(Long channelId) { this.channelId = channelId; }

    public Long getReceiverId() { return receiverId; }
    public void setReceiverId(Long receiverId) { this.receiverId = receiverId; }

    public Long getFileId() { return fileId; }
    public void setFileId(Long fileId) { this.fileId = fileId; }

    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }

    public String getFileUrl() { return fileUrl; }
    public void setFileUrl(String fileUrl) { this.fileUrl = fileUrl; }

    public String getDeliveryStatus() { return deliveryStatus; }
    public void setDeliveryStatus(String deliveryStatus) { this.deliveryStatus = deliveryStatus; }

}