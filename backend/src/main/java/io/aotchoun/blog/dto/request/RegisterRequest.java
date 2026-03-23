package io.aotchoun.blog.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO pour la requête d'inscription
 *
 * NOTE : On écrit les getters/setters explicitement ici
 * pour garantir que Jackson peut désérialiser le JSON correctement.
 *
 * Jackson a besoin de :
 * 1. Un constructeur sans arguments (pour créer l'objet)
 * 2. Des setters (pour remplir les champs depuis le JSON)
 * 3. Des getters (pour la validation et la sérialisation)
 */
public class RegisterRequest {

    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    private String username;

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    @Size(max = 100, message = "Email must not exceed 100 characters")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 6, max = 100, message = "Password must be between 6 and 100 characters")
    private String password;

    // ================================
    // Constructeur vide (requis par Jackson pour désérialiser)
    // ================================
    public RegisterRequest() {}

    // ================================
    // Constructeur complet (pratique pour les tests)
    // ================================
    public RegisterRequest(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
    }

    // ================================
    // Getters (Jackson lit les données pour les réponses)
    // ================================
    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    // ================================
    // Setters (Jackson remplit les champs depuis le JSON reçu)
    // ================================
    public void setUsername(String username) {
        this.username = username;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}