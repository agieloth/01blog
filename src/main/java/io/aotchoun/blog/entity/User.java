package io.aotchoun.blog.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * Entité User - Représente un utilisateur dans la base de données
 * 
 * ANNOTATIONS LOMBOK (réduisent le code boilerplate) :
 * @Data : Génère automatiquement getters, setters, toString, equals, hashCode
 * @NoArgsConstructor : Génère un constructeur sans paramètres (requis par JPA)
 * @AllArgsConstructor : Génère un constructeur avec tous les paramètres
 * 
 * ANNOTATIONS JPA (mapping objet-relationnel) :
 * @Entity : Indique que cette classe est une entité JPA (sera mappée en table)
 * @Table : Spécifie le nom de la table en DB (optionnel, par défaut = nom de la classe)
 */
@Entity
@Table(name = "users")  // La table s'appellera "users" en DB
@Data                    // Lombok : génère getters/setters/toString/etc.
@NoArgsConstructor       // Lombok : constructeur vide
@AllArgsConstructor      // Lombok : constructeur avec tous les champs
public class User {

    /**
     * Identifiant unique de l'utilisateur
     * 
     * @Id : Indique que c'est la clé primaire
     * @GeneratedValue : La valeur est générée automatiquement
     * IDENTITY : Auto-increment géré par la DB (1, 2, 3, ...)
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Nom d'utilisateur unique
     * 
     * @Column : Configure la colonne en DB
     * unique = true : Pas de doublons (contrainte UNIQUE en SQL)
     * nullable = false : Ne peut pas être NULL (contrainte NOT NULL)
     */
    @Column(unique = true, nullable = false, length = 50)
    private String username;

    /**
     * Email unique
     */
    @Column(unique = true, nullable = false, length = 100)
    private String email;

    /**
     * Mot de passe hashé
     * 
     * IMPORTANT : On ne stocke JAMAIS le mot de passe en clair !
     * Il sera hashé avec BCrypt avant d'être sauvegardé
     */
    @Column(nullable = false)
    private String password;

    /**
     * Rôle de l'utilisateur (USER ou ADMIN)
     * 
     * @Enumerated(EnumType.STRING) : Stocke l'enum en tant que STRING en DB
     * Au lieu de stocker 0/1, on stocke "USER"/"ADMIN" (plus lisible)
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role = Role.USER;  // Par défaut, nouveau user = USER

    /**
     * Indique si l'utilisateur est banni
     */
    @Column(nullable = false)
    private Boolean isBanned = false;  // Par défaut, pas banni

    /**
     * Date de création du compte
     * 
     * @Column(updatable = false) : Cette valeur ne peut pas être modifiée après création
     */
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Date de dernière mise à jour
     */
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    /**
     * Méthode appelée automatiquement AVANT l'insertion en DB
     * 
     * @PrePersist est une annotation JPA lifecycle
     * Permet d'initialiser des valeurs avant la sauvegarde
     */
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Méthode appelée automatiquement AVANT chaque mise à jour
     * 
     * @PreUpdate est une annotation JPA lifecycle
     */
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Constructeur personnalisé pour créer un nouveau User
     * (utile lors de l'inscription)
     */
    public User(String username, String email, String password, Role role) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.role = role;
        this.isBanned = false;
    }
}