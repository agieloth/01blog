package io.aotchoun.blog.dto.response;

import io.aotchoun.blog.entity.Post;
import java.time.LocalDateTime;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.List;

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
    private List<String> imageUrls;
    private Long authorId;
    private String authorUsername;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private long likeCount;
    private long commentCount;
    private boolean likedByCurrentUser;

    private static final ObjectMapper mapper = new ObjectMapper();

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
        PostResponse r = new PostResponse();
        r.id = post.getId();
        r.title = post.getTitle();
        r.content = post.getContent();
        r.imageUrls = parseImageUrls(post.getImageUrls());
        r.authorId = post.getAuthor().getId();
        r.authorUsername = post.getAuthor().getUsername();
        r.createdAt = post.getCreatedAt();
        r.updatedAt = post.getUpdatedAt();
        return r;
    }

    // Surcharge avec compteurs — utilisée dans PostService
    public static PostResponse from(Post post, long likeCount, long commentCount, boolean likedByCurrentUser) {
        PostResponse r = from(post);
        r.likeCount = likeCount;
        r.commentCount = commentCount;
        r.likedByCurrentUser = likedByCurrentUser;
        return r;
    }

    private static List<String> parseImageUrls(String json) {
        if (json == null || json.isEmpty()) return new ArrayList<>();
        try {
            return mapper.readValue(json, new TypeReference<List<String>>() {});
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    // Getters
    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getContent() { return content; }
    public List<String> getImageUrls() { return imageUrls; }
    public Long getAuthorId() { return authorId; }
    public String getAuthorUsername() { return authorUsername; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public long getLikeCount() { return likeCount; }
    public long getCommentCount() { return commentCount; }
    public boolean isLikedByCurrentUser() { return likedByCurrentUser; }

    // Setters
    public void setId(Long id) { this.id = id; }
    public void setTitle(String title) { this.title = title; }
    public void setContent(String content) { this.content = content; }
    public void setImageUrls(List<String> imageUrls) { this.imageUrls = imageUrls; }
    public void setAuthorId(Long authorId) { this.authorId = authorId; }
    public void setAuthorUsername(String authorUsername) { this.authorUsername = authorUsername; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    public void setLikeCount(long likeCount) { this.likeCount = likeCount; }
    public void setCommentCount(long commentCount) { this.commentCount = commentCount; }
    public void setLikedByCurrentUser(boolean likedByCurrentUser) { this.likedByCurrentUser = likedByCurrentUser; }
}