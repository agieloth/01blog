package io.aotchoun.blog.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entité Post - Représente un article de blog dans la base de données
 *
 * Relation avec User :
 * Plusieurs posts peuvent appartenir à un seul utilisateur (Many-To-One)
 * Un utilisateur peut avoir plusieurs posts (One-To-Many côté User)
 */
@Entity
@Table(name = "posts")
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String title;

    // TEXT = pas de limite de longueur (contrairement à VARCHAR)
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    /**
     * Relation Many-To-One avec User
     *
     * @ManyToOne : Plusieurs posts → un seul auteur
     * @JoinColumn : La colonne FK dans la table "posts" s'appellera "author_id"
     * fetch = LAZY : L'auteur n'est chargé depuis la DB QUE si on y accède
     *                (meilleure performance, pas de jointure inutile)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    // ================================
    // Constructeurs
    // ================================

    public Post() {}

    public Post(String title, String content, User author) {
        this.title = title;
        this.content = content;
        this.author = author;
    }

    // ================================
    // Lifecycle JPA
    // ================================

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // ================================
    // Getters
    // ================================

    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getContent() { return content; }
    public User getAuthor() { return author; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }

    // ================================
    // Setters
    // ================================

    public void setId(Long id) { this.id = id; }
    public void setTitle(String title) { this.title = title; }
    public void setContent(String content) { this.content = content; }
    public void setAuthor(User author) { this.author = author; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}