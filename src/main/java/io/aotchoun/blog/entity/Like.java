package io.aotchoun.blog.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entité Like
 *
 * La contrainte @Table(uniqueConstraints) garantit qu'un utilisateur
 * ne peut liker un post qu'une seule fois au niveau de la base de données.
 * C'est une sécurité supplémentaire en plus de la vérification dans le service.
 */
@Entity
@Table(name = "likes", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"user_id", "post_id"})
})
public class Like {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public Like() {}

    public Like(User user, Post post) {
        this.user = user;
        this.post = post;
    }

    @PrePersist
    protected void onCreate() { this.createdAt = LocalDateTime.now(); }

    public Long getId() { return id; }
    public User getUser() { return user; }
    public Post getPost() { return post; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setId(Long id) { this.id = id; }
    public void setUser(User user) { this.user = user; }
    public void setPost(Post post) { this.post = post; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}