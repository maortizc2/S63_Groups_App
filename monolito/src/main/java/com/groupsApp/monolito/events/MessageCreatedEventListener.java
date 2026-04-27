package com.groupsapp.monolito.events;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.time.Instant;

/**
 * Listener que reacciona a MessageCreatedDomainEvent y publica el evento a SQS.
 *
 * La clave está en @TransactionalEventListener(phase = AFTER_COMMIT):
 *   - Si la transacción de sendMessage hace COMMIT exitoso, este método se ejecuta.
 *   - Si la transacción hace ROLLBACK, este método NO se ejecuta.
 *   - Garantiza consistencia: no se publican eventos de mensajes que nunca existieron.
 */
@Component
public class MessageCreatedEventListener {

    private static final Logger log = LoggerFactory.getLogger(MessageCreatedEventListener.class);

    private final NotificationEventPublisher publisher;

    public MessageCreatedEventListener(NotificationEventPublisher publisher) {
        this.publisher = publisher;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(MessageCreatedDomainEvent domainEvent) {
        log.debug("Transaction committed, publishing external event for messageId={}",
                domainEvent.getMessageId());

        // Traducimos el evento interno (domain) al evento externo (para SQS).
        MessageCreatedEvent externalEvent = new MessageCreatedEvent();
        externalEvent.setOccurredAt(Instant.now());
        externalEvent.setMessageId(domainEvent.getMessageId());
        externalEvent.setSenderId(domainEvent.getSenderId());
        externalEvent.setSenderUsername(domainEvent.getSenderUsername());
        externalEvent.setReceiverId(domainEvent.getReceiverId());
        externalEvent.setChannelId(domainEvent.getChannelId());
        externalEvent.setContentPreview(domainEvent.getContentPreview());

        publisher.publishMessageCreated(externalEvent);
    }
}