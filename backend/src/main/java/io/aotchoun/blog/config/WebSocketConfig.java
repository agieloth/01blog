package io.aotchoun.blog.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * Configuration WebSocket avec STOMP
 *
 * STOMP (Simple Text Oriented Messaging Protocol) est un protocole
 * de messagerie qui fonctionne au-dessus de WebSocket.
 *
 * ANALOGIE :
 * WebSocket = une autoroute (connexion bidirectionnelle)
 * STOMP = le code de la route (comment structurer les messages)
 * Topics = les sorties d'autoroute (canaux d'abonnement)
 *
 * Flux :
 * Client A crée un post
 *   → PostService envoie au broker sur /topic/posts
 *     → Tous les clients abonnés à /topic/posts reçoivent le message
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Point de connexion WebSocket
        // Le client se connecte à ws://localhost:8080/ws
        // SockJS = fallback HTTP si WebSocket non supporté par le navigateur
        //
        // FIX SECURITE : restreindre les origines autorisées.
        // En dev : localhost:4200 (Angular). En prod : surcharger via variable d'env.
        // L'ancien "allowedOriginPatterns("*")" autorisait n'importe quelle origine.
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns(
                        "http://localhost:4200",
                        "http://localhost:8080"
                )
                .withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // Préfixe pour les messages envoyés par le client vers le serveur
        registry.setApplicationDestinationPrefixes("/app");

        // Préfixe pour les topics auxquels les clients s'abonnent
        // /topic/posts   → nouveaux posts
        // /topic/comments → nouveaux commentaires
        // /topic/likes   → changements de likes
        registry.enableSimpleBroker("/topic");
    }
}