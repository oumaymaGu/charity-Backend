package tn.example.charity.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker  // Active le support WebSocket avec STOMP
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Point d'entrée WebSocket (à utiliser côté client pour la connexion)
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*") // Autorise toutes les origines — à restreindre en production
                .withSockJS(); // Support de SockJS pour fallback HTTP si WebSocket n’est pas supporté
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // Configure un broker simple en mémoire pour les messages destinés aux abonnés
        registry.enableSimpleBroker("/topic"); // Préfixe utilisé pour le broadcast
        registry.setApplicationDestinationPrefixes("/app"); // Préfixe pour les messages envoyés aux méthodes @MessageMapping
    }
}
