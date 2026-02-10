package io.aotchoun.blog.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * DTO pour les réponses d'erreur
 * 
 * Structure standard pour toutes les erreurs de l'API
 * Permet au frontend de gérer les erreurs de manière cohérente
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {

    /**
     * Timestamp de l'erreur
     */
    private LocalDateTime timestamp;

    /**
     * Code de statut HTTP (400, 401, 404, 500, etc.)
     */
    private Integer status;

    /**
     * Message d'erreur pour les développeurs
     */
    private String error;

    /**
     * Message d'erreur convivial pour l'utilisateur
     */
    private String message;

    /**
     * Chemin de la requête qui a causé l'erreur
     */
    private String path;

    /**
     * Constructeur simplifié
     */
    public ErrorResponse(Integer status, String error, String message, String path) {
        this.timestamp = LocalDateTime.now();
        this.status = status;
        this.error = error;
        this.message = message;
        this.path = path;
    }

    /**
     * Constructeur encore plus simple
     */
    public ErrorResponse(Integer status, String message) {
        this.timestamp = LocalDateTime.now();
        this.status = status;
        this.message = message;
    }
}