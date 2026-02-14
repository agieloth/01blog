package io.aotchoun.blog.dto.response;

import io.aotchoun.blog.entity.Comment;
import java.time.LocalDateTime;

public class CommentResponse {

    private Long id;
    private String content;
    private Long authorId;
    private String authorUsername;
    private Long postId;
    private LocalDateTime createdAt;

    public CommentResponse() {}

    public static CommentResponse from(Comment comment) {
        CommentResponse r = new CommentResponse();
        r.id = comment.getId();
        r.content = comment.getContent();
        r.authorId = comment.getAuthor().getId();
        r.authorUsername = comment.getAuthor().getUsername();
        r.postId = comment.getPost().getId();
        r.createdAt = comment.getCreatedAt();
        return r;
    }

    public Long getId() { return id; }
    public String getContent() { return content; }
    public Long getAuthorId() { return authorId; }
    public String getAuthorUsername() { return authorUsername; }
    public Long getPostId() { return postId; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setId(Long id) { this.id = id; }
    public void setContent(String content) { this.content = content; }
    public void setAuthorId(Long authorId) { this.authorId = authorId; }
    public void setAuthorUsername(String authorUsername) { this.authorUsername = authorUsername; }
    public void setPostId(Long postId) { this.postId = postId; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}