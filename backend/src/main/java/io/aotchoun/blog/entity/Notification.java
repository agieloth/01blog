package io.aotchoun.blog.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
@Entity
@Table(name = "notifications")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // Destinataire de la notification

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private NotificationType type;

    @Column(nullable = false, length = 500)
    private String message;

    // ID de la ressource liée (post, user, etc.)
    private Long relatedEntityId;

    // Username de l'acteur (celui qui a fait l'action)
    @Column(length = 100)
    private String actorUsername;

    @Column(nullable = false)
    private boolean read = false;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public Notification() {}

    public Notification(User user, NotificationType type, String message, Long relatedEntityId, String actorUsername) {
        this.user = user;
        this.type = type;
        this.message = message;
        this.relatedEntityId = relatedEntityId;
        this.actorUsername = actorUsername;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public enum NotificationType {
        NEW_FOLLOWER,
        POST_LIKED,
        POST_COMMENTED,
        NEW_POST        // ← Nouveau type : un profil suivi a publié un post
    }

    // Getters & Setters
    public Long getId() { return id; }
    public User getUser() { return user; }
    public NotificationType getType() { return type; }
    public String getMessage() { return message; }
    public Long getRelatedEntityId() { return relatedEntityId; }
    public String getActorUsername() { return actorUsername; }
    public boolean isRead() { return read; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    public void setId(Long id) { this.id = id; }
    public void setUser(User user) { this.user = user; }
    public void setType(NotificationType type) { this.type = type; }
    public void setMessage(String message) { this.message = message; }
    public void setRelatedEntityId(Long relatedEntityId) { this.relatedEntityId = relatedEntityId; }
    public void setActorUsername(String actorUsername) { this.actorUsername = actorUsername; }
    public void setRead(boolean read) { this.read = read; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}