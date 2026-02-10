package io.aotchoun.blog.dto.response;

import io.aotchoun.blog.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO pour la réponse d'authentification
 * 
 * Renvoyé après un login ou register réussi
 * Contient le token JWT et les infos de base de l'utilisateur
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {

    /**
     * Token JWT
     * 
     * Le client devra envoyer ce token dans le header "Authorization"
     * pour toutes les requêtes protégées :
     * Authorization: Bearer <token>
     */
    private String token;

    /**
     * Type de token (toujours "Bearer" pour JWT)
     */
    private String type = "Bearer";

    /**
     * ID de l'utilisateur
     */
    private Long id;

    /**
     * Username
     */
    private String username;

    /**
     * Email
     */
    private String email;

    /**
     * Rôle (USER ou ADMIN)
     */
    private Role role;

    /**
     * Constructeur personnalisé (sans le type)
     * Le type sera toujours "Bearer"
     */
    public AuthResponse(String token, Long id, String username, String email, Role role) {
        this.token = token;
        this.type = "Bearer";
        this.id = id;
        this.username = username;
        this.email = email;
        this.role = role;
    }

    /**
     * NOTE IMPORTANTE :
     * On ne renvoie PAS le password ici !
     * C'est pour ça qu'on utilise un DTO au lieu de l'entité User.
     */
}