package com.groupsapp.monolito.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    // ── Punto de conexión WebSocket ───────────────────
    // El cliente se conecta a: ws://localhost:8080/ws
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*")  // En prod, restringir al dominio real
                .withSockJS();                  // Fallback para browsers sin WebSocket
    }

    // ── Configuración del broker de mensajes ──────────
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {

        // Prefijo para mensajes que el CLIENTE envía al servidor
        // El cliente envía a: /app/chat.send
        registry.setApplicationDestinationPrefixes("/app");

        // Prefijos para mensajes que el SERVIDOR envía a los clientes
        // /topic  → broadcast (1 mensaje → muchos clientes, para canales de grupo)
        // /queue  → punto a punto (1 mensaje → 1 cliente, para mensajes privados)
        registry.enableSimpleBroker("/topic", "/queue");

        // Prefijo para mensajes privados a un usuario específico
        // El servidor envía a: /user/{userId}/queue/messages
        registry.setUserDestinationPrefix("/user");
    }
}