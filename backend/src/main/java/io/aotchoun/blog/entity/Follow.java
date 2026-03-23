package io.aotchoun.blog.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entité Follow - Représente une relation d'abonnement entre deux users
 * 
 * follower = celui qui suit
 * followed = celui qui est suivi
 * 
 * Exemple : Alice suit Bob
 * → follower = Alice, followed = Bob
 * 
 * La contrainte UniqueConstraint empêche de suivre deux fois la même personne.
 */
@Entity
@Table(name = "follows", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"follower_id", "followed_id"})
})
public class Follow {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "follower_id", nullable = false)
    private User follower;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "followed_id", nullable = false)
    private User followed;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public Follow() {}

    public Follow(User follower, User followed) {
        this.follower = follower;
        this.followed = followed;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    // Getters
    public Long getId() { return id; }
    public User getFollower() { return follower; }
    public User getFollowed() { return followed; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    // Setters
    public void setId(Long id) { this.id = id; }
    public void setFollower(User follower) { this.follower = follower; }
    public void setFollowed(User followed) { this.followed = followed; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}