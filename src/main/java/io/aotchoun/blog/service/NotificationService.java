package io.aotchoun.blog.service;

import io.aotchoun.blog.dto.response.NotificationResponse;
import io.aotchoun.blog.entity.Notification;
import io.aotchoun.blog.entity.User;
import io.aotchoun.blog.exception.ResourceNotFoundException;
import io.aotchoun.blog.exception.UnauthorizedException;
import io.aotchoun.blog.repository.NotificationRepository;
import io.aotchoun.blog.repository.UserRepository;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final SimpMessagingTemplate messagingTemplate;

    public NotificationService(NotificationRepository notificationRepository,
                               UserRepository userRepository,
                               SimpMessagingTemplate messagingTemplate) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
        this.messagingTemplate = messagingTemplate;
    }

    /**
     * Créer une notification et la broadcaster via WebSocket
     */
    public void createNotification(Long userId, Notification.NotificationType type, 
                                   String message, Long relatedEntityId, String actorUsername) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        
        Notification notification = new Notification(user, type, message, relatedEntityId, actorUsername);
        notification = notificationRepository.save(notification);
        
        // Broadcaster la notification à l'utilisateur via WebSocket
        NotificationResponse response = NotificationResponse.from(notification);
        messagingTemplate.convertAndSend("/topic/notifications/" + userId, response);
    }

    /**
     * Récupérer toutes les notifications d'un utilisateur
     */
    @Transactional(readOnly = true)
    public List<NotificationResponse> getUserNotifications(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));
        
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(user.getId())
                .stream()
                .map(NotificationResponse::from)
                .collect(Collectors.toList());
    }

    /**
     * Compter les notifications non lues
     */
    @Transactional(readOnly = true)
    public long countUnreadNotifications(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));
        
        return notificationRepository.countByUserIdAndReadFalse(user.getId());
    }

    /**
     * Marquer une notification comme lue
     */
    public void markAsRead(Long notificationId, String username) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found with id: " + notificationId));
        
        if (!notification.getUser().getUsername().equals(username)) {
            throw new UnauthorizedException("You can only mark your own notifications as read");
        }
        
        notification.setRead(true);
        notificationRepository.save(notification);
    }

    /**
     * Marquer toutes les notifications comme lues
     */
    public void markAllAsRead(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));
        
        List<Notification> notifications = notificationRepository.findByUserIdOrderByCreatedAtDesc(user.getId());
        notifications.forEach(n -> n.setRead(true));
        notificationRepository.saveAll(notifications);
    }
}