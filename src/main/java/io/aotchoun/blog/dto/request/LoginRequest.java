package io.aotchoun.blog.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO pour la requête de connexion
 * 
 * L'utilisateur peut se connecter avec son email OU son username
 * On utilise un seul champ "identifier" pour les deux
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {

    /**
     * Identifiant : peut être un email OU un username
     * 
     * Exemples valides :
     * - "john@example.com"
     * - "johnDoe"
     */
    @NotBlank(message = "Email or username is required")
    private String identifier;

    /**
     * Mot de passe
     */
    @NotBlank(message = "Password is required")
    private String password;
}