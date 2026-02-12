package io.aotchoun.blog.dto.response;

import io.aotchoun.blog.entity.Role;

/**
 * DTO pour la réponse d'authentification
 *
 * Renvoyé après un login ou register réussi.
 * Contient le token JWT et les infos de base de l'utilisateur.
 *
 * NOTE : Pas de champ "password" ici par sécurité !
 */
public class AuthResponse {

    private String token;
    private String type = "Bearer";
    private Long id;
    private String username;
    private String email;
    private Role role;

    // Constructeur vide
    public AuthResponse() {}

    // Constructeur principal utilisé dans AuthService
    public AuthResponse(String token, Long id, String username, String email, Role role) {
        this.token = token;
        this.type = "Bearer";
        this.id = id;
        this.username = username;
        this.email = email;
        this.role = role;
    }

    // Getters
    public String getToken() {
        return token;
    }

    public String getType() {
        return type;
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public Role getRole() {
        return role;
    }

    // Setters
    public void setToken(String token) {
        this.token = token;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}