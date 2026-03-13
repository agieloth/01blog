package io.aotchoun.blog.controller;

import io.aotchoun.blog.dto.response.NotificationResponse;
import io.aotchoun.blog.service.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    /**
     * Récupérer toutes les notifications de l'utilisateur connecté
     */
    @GetMapping
    public ResponseEntity<List<NotificationResponse>> getMyNotifications(Authentication auth) {
        List<NotificationResponse> notifications = notificationService.getUserNotifications(auth.getName());
        return ResponseEntity.ok(notifications);
    }

    /**
     * Compter les notifications non lues
     */
    @GetMapping("/unread-count")
    public ResponseEntity<Map<String, Long>> getUnreadCount(Authentication auth) {
        long count = notificationService.countUnreadNotifications(auth.getName());
        return ResponseEntity.ok(Map.of("count", count));
    }

    /**
     * Marquer une notification comme lue
     */
    @PatchMapping("/{id}/read")
    public ResponseEntity<Void> markAsRead(@PathVariable Long id, Authentication auth) {
        notificationService.markAsRead(id, auth.getName());
        return ResponseEntity.noContent().build();
    }

    /**
     * Marquer toutes les notifications comme lues
     */
    @PatchMapping("/read-all")
    public ResponseEntity<Void> markAllAsRead(Authentication auth) {
        notificationService.markAllAsRead(auth.getName());
        return ResponseEntity.noContent().build();
    }
}