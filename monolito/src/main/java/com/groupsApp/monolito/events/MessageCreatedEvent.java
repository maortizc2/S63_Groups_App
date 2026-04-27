package com.groupsapp.monolito.events;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.Instant;

/**
 * Evento de dominio: un mensaje fue enviado en el chat.
 *
 * Se serializa a JSON y se publica a SQS. Los consumers (notifications-consumer)
 * deserializan este mismo esquema para procesar el evento.
 *
 * El schema del evento es un contrato: cambios rompen a los consumers.
 * En un sistema maduro, esto iría en un schema registry (como Avro Schema Registry).
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MessageCreatedEvent {

    private String eventType = "message.created";
    private String eventVersion = "1.0";
    private Instant occurredAt;

    private Long messageId;
    private Long senderId;
    private String senderUsername;
    private Long receiverId;        // null si es mensaje a canal
    private Long channelId;         // null si es mensaje directo
    private String contentPreview;  // primeros 100 chars del contenido

    // ── Constructores ──────────────────────────────────
    public MessageCreatedEvent() {}

    // ── Getters y Setters ──────────────────────────────
    public String getEventType() { return eventType; }
    public void setEventType(String eventType) { this.eventType = eventType; }

    public String getEventVersion() { return eventVersion; }
    public void setEventVersion(String eventVersion) { this.eventVersion = eventVersion; }

    public Instant getOccurredAt() { return occurredAt; }
    public void setOccurredAt(Instant occurredAt) { this.occurredAt = occurredAt; }

    public Long getMessageId() { return messageId; }
    public void setMessageId(Long messageId) { this.messageId = messageId; }

    public Long getSenderId() { return senderId; }
    public void setSenderId(Long senderId) { this.senderId = senderId; }

    public String getSenderUsername() { return senderUsername; }
    public void setSenderUsername(String senderUsername) { this.senderUsername = senderUsername; }

    public Long getReceiverId() { return receiverId; }
    public void setReceiverId(Long receiverId) { this.receiverId = receiverId; }

    public Long getChannelId() { return channelId; }
    public void setChannelId(Long channelId) { this.channelId = channelId; }

    public String getContentPreview() { return contentPreview; }
    public void setContentPreview(String contentPreview) { this.contentPreview = contentPreview; }

    @Override
    public String toString() {
        return "MessageCreatedEvent{" +
               "messageId=" + messageId +
               ", senderId=" + senderId +
               ", receiverId=" + receiverId +
               ", channelId=" + channelId +
               '}';
    }
}