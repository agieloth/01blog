package io.aotchoun.blog.dto.response;

import io.aotchoun.blog.entity.Post;
import java.time.LocalDateTime;

/**
 * DTO pour la réponse d'un post
 *
 * On n'expose jamais l'entité Post directement — on contrôle
 * exactement ce que l'API retourne au client.
 *
 * Par exemple, on inclut "authorUsername" mais pas l'objet User complet
 * (qui contient le mot de passe hashé).
 */
public class PostResponse {

    private Long id;
    private String title;
    private String content;
    private Long authorId;
    private String authorUsername;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public PostResponse() {}

    /**
     * Construit un PostResponse depuis une entité Post
     *
     * C'est le pattern "factory method" : plutôt qu'un constructeur compliqué,
     * on a une méthode statique claire qui fait la conversion.
     *
     * Utilisation : PostResponse.from(post)
     */
    public static PostResponse from(Post post) {
        PostResponse response = new PostResponse();
        response.id = post.getId();
        response.title = post.getTitle();
        response.content = post.getContent();
        response.authorId = post.getAuthor().getId();
        response.authorUsername = post.getAuthor().getUsername();
        response.createdAt = post.getCreatedAt();
        response.updatedAt = post.getUpdatedAt();
        return response;
    }

    // Getters
    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getContent() { return content; }
    public Long getAuthorId() { return authorId; }
    public String getAuthorUsername() { return authorUsername; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }

    // Setters
    public void setId(Long id) { this.id = id; }
    public void setTitle(String title) { this.title = title; }
    public void setContent(String content) { this.content = content; }
    public void setAuthorId(Long authorId) { this.authorId = authorId; }
    public void setAuthorUsername(String authorUsername) { this.authorUsername = authorUsername; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}