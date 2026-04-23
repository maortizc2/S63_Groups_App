package com.groupsapp.monolito.events;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;
import software.amazon.awssdk.services.sqs.model.SendMessageResponse;

/**
 * Publica eventos de dominio a SQS.
 *
 * Cada método corresponde a un tipo de evento. Internamente serializa a JSON
 * y lo envía a la cola configurada.
 *
 * Este componente representa el PRODUCTOR en el patrón MOM (Message-Oriented Middleware).
 * No sabe quién consume los eventos; solo los publica.
 */
@Component
public class NotificationEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(NotificationEventPublisher.class);

    private final SqsClient sqsClient;
    private final ObjectMapper objectMapper;
    private final String queueUrl;

    public NotificationEventPublisher(
            SqsClient sqsClient,
            ObjectMapper objectMapper,
            @Value("${app.sqs.queue-url}") String queueUrl) {
        this.sqsClient = sqsClient;
        this.objectMapper = objectMapper;
        this.queueUrl = queueUrl;
    }

    /**
     * Publica evento "message.created" cuando un usuario envía un mensaje nuevo.
     * El consumidor (notifications-consumer en Python) leerá esto y simulará
     * enviar una notificación push al receptor.
     */
    public void publishMessageCreated(MessageCreatedEvent event) {
        try {
            String payload = objectMapper.writeValueAsString(event);

            SendMessageRequest request = SendMessageRequest.builder()
                    .queueUrl(queueUrl)
                    .messageBody(payload)
                    .build();

            SendMessageResponse response = sqsClient.sendMessage(request);

            log.info("Published message.created event (SQS messageId={}, userId={}, messageId={})",
                    response.messageId(), event.getSenderId(), event.getMessageId());

        } catch (JsonProcessingException e) {
            log.error("Failed to serialize MessageCreatedEvent: {}", event, e);
            // No propagamos la excepción: si falla la publicación,
            // no queremos romper el envío del mensaje principal.
        } catch (Exception e) {
            log.error("Failed to publish message.created event: {}", event, e);
        }
    }
}