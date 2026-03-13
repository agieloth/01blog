package io.aotchoun.blog.dto.response;

import io.aotchoun.blog.entity.Notification;
import java.time.LocalDateTime;

public class NotificationResponse {

    private Long id;
    private String type;
    private String message;
    private Long relatedEntityId;
    private String actorUsername;
    private boolean read;
    private LocalDateTime createdAt;

    public NotificationResponse() {}

    public static NotificationResponse from(Notification notification) {
        NotificationResponse response = new NotificationResponse();
        response.id = notification.getId();
        response.type = notification.getType().name();
        response.message = notification.getMessage();
        response.relatedEntityId = notification.getRelatedEntityId();
        response.actorUsername = notification.getActorUsername();
        response.read = notification.isRead();
        response.createdAt = notification.getCreatedAt();
        return response;
    }

    // Getters & Setters
    public Long getId() { return id; }
    public String getType() { return type; }
    public String getMessage() { return message; }
    public Long getRelatedEntityId() { return relatedEntityId; }
    public String getActorUsername() { return actorUsername; }
    public boolean isRead() { return read; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    public void setId(Long id) { this.id = id; }
    public void setType(String type) { this.type = type; }
    public void setMessage(String message) { this.message = message; }
    public void setRelatedEntityId(Long relatedEntityId) { this.relatedEntityId = relatedEntityId; }
    public void setActorUsername(String actorUsername) { this.actorUsername = actorUsername; }
    public void setRead(boolean read) { this.read = read; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}