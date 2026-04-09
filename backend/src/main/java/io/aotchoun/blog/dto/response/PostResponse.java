package io.aotchoun.blog.dto.response;

import io.aotchoun.blog.entity.Post;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * DTO pour la réponse d'un post
 *
 * FIX v2 : ObjectMapper statique retiré de cette classe.
 * Un ObjectMapper statique dans un DTO n'est pas thread-safe et contourne
 * l'injection Spring (configuration Jackson personnalisée ignorée).
 *
 * La désérialisation des imageUrls est maintenant déléguée à PostService
 * qui injecte l'ObjectMapper Spring via son constructeur.
 * PostResponse.from(post) ne traite plus le JSON des images directement.
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

    public PostResponse() {}

    /**
     * Factory method de base — imageUrls doit être fourni déjà parsé.
     * Utiliser PostResponse.from(post, imageUrls) depuis PostService.
     */
    public static PostResponse from(Post post, List<String> imageUrls) {
        PostResponse r = new PostResponse();
        r.id            = post.getId();
        r.title         = post.getTitle();
        r.content       = post.getContent();
        r.imageUrls     = imageUrls != null ? imageUrls : new ArrayList<>();
        r.authorId      = post.getAuthor().getId();
        r.authorUsername = post.getAuthor().getUsername();
        r.createdAt     = post.getCreatedAt();
        r.updatedAt     = post.getUpdatedAt();
        return r;
    }

    /**
     * Factory method avec compteurs — utilisée dans PostService
     */
    public static PostResponse from(Post post, List<String> imageUrls,
                                    long likeCount, long commentCount,
                                    boolean likedByCurrentUser) {
        PostResponse r = from(post, imageUrls);
        r.likeCount          = likeCount;
        r.commentCount       = commentCount;
        r.likedByCurrentUser = likedByCurrentUser;
        return r;
    }

    // Getters
    public Long getId()                     { return id; }
    public String getTitle()                { return title; }
    public String getContent()              { return content; }
    public List<String> getImageUrls()      { return imageUrls; }
    public Long getAuthorId()               { return authorId; }
    public String getAuthorUsername()       { return authorUsername; }
    public LocalDateTime getCreatedAt()     { return createdAt; }
    public LocalDateTime getUpdatedAt()     { return updatedAt; }
    public long getLikeCount()              { return likeCount; }
    public long getCommentCount()           { return commentCount; }
    public boolean isLikedByCurrentUser()   { return likedByCurrentUser; }

    // Setters
    public void setId(Long id)                              { this.id = id; }
    public void setTitle(String title)                      { this.title = title; }
    public void setContent(String content)                  { this.content = content; }
    public void setImageUrls(List<String> imageUrls)        { this.imageUrls = imageUrls; }
    public void setAuthorId(Long authorId)                  { this.authorId = authorId; }
    public void setAuthorUsername(String authorUsername)    { this.authorUsername = authorUsername; }
    public void setCreatedAt(LocalDateTime createdAt)       { this.createdAt = createdAt; }
    public void setUpdatedAt(LocalDateTime updatedAt)       { this.updatedAt = updatedAt; }
    public void setLikeCount(long likeCount)                { this.likeCount = likeCount; }
    public void setCommentCount(long commentCount)          { this.commentCount = commentCount; }
    public void setLikedByCurrentUser(boolean v)            { this.likedByCurrentUser = v; }
}