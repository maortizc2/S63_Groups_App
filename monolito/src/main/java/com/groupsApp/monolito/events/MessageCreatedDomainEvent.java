package com.groupsapp.monolito.events;

/**
 * Evento de dominio interno, publicado cuando se crea un mensaje.
 *
 * Este evento circula dentro del ApplicationContext de Spring.
 * Un listener @TransactionalEventListener lo recibe después del commit
 * y publica el evento correspondiente a SQS.
 *
 * Patrón: Domain Events (Evans, DDD).
 *
 * Diferencia con MessageCreatedEvent:
 *   - MessageCreatedDomainEvent: evento interno de Spring, post-commit.
 *   - MessageCreatedEvent:       evento externo serializable, va a SQS.
 */
public class MessageCreatedDomainEvent {

    private final Long messageId;
    private final Long senderId;
    private final String senderUsername;
    private final Long receiverId;
    private final Long channelId;
    private final String contentPreview;

    public MessageCreatedDomainEvent(Long messageId, Long senderId, String senderUsername,
                                     Long receiverId, Long channelId, String contentPreview) {
        this.messageId = messageId;
        this.senderId = senderId;
        this.senderUsername = senderUsername;
        this.receiverId = receiverId;
        this.channelId = channelId;
        this.contentPreview = contentPreview;
    }

    public Long getMessageId() { return messageId; }
    public Long getSenderId() { return senderId; }
    public String getSenderUsername() { return senderUsername; }
    public Long getReceiverId() { return receiverId; }
    public Long getChannelId() { return channelId; }
    public String getContentPreview() { return contentPreview; }
}