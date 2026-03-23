package io.aotchoun.blog.service;

import io.aotchoun.blog.dto.response.FollowResponse;
import io.aotchoun.blog.dto.response.UserStatsResponse;
import io.aotchoun.blog.entity.Follow;
import io.aotchoun.blog.entity.Notification;
import io.aotchoun.blog.entity.User;
import io.aotchoun.blog.exception.BadRequestException;
import io.aotchoun.blog.exception.ResourceNotFoundException;
import io.aotchoun.blog.repository.FollowRepository;
import io.aotchoun.blog.repository.PostRepository;
import io.aotchoun.blog.repository.UserRepository;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class FollowService {

    private final FollowRepository followRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final NotificationService notificationService;

    public FollowService(FollowRepository followRepository,
                        UserRepository userRepository,
                        PostRepository postRepository,
                        SimpMessagingTemplate messagingTemplate,
                        NotificationService notificationService) {
        this.followRepository = followRepository;
        this.userRepository = userRepository;
        this.postRepository = postRepository;
        this.messagingTemplate = messagingTemplate;
        this.notificationService = notificationService;
    }

    public FollowResponse toggleFollow(Long targetUserId, String currentUsername) {
        User currentUser = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + currentUsername));
        
        User targetUser = userRepository.findById(targetUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + targetUserId));

        if (currentUser.getId().equals(targetUserId)) {
            throw new BadRequestException("You cannot follow yourself");
        }

        var existing = followRepository.findByFollowerIdAndFollowedId(currentUser.getId(), targetUserId);
        
        boolean following;
        if (existing.isPresent()) {
            followRepository.delete(existing.get());
            following = false;
        } else {
            followRepository.save(new Follow(currentUser, targetUser));
            following = true;
            
            // ✅ Créer une notification
            notificationService.createNotification(
                targetUserId,
                Notification.NotificationType.NEW_FOLLOWER,
                currentUser.getUsername() + " a commencé à vous suivre",
                currentUser.getId(),
                currentUser.getUsername()
            );
        }

        long followerCount = followRepository.countByFollowedId(targetUserId);
        FollowResponse response = new FollowResponse(following, followerCount);

        // Broadcaster la mise à jour du compteur de followers
        messagingTemplate.convertAndSend("/topic/follows",
            new FollowerUpdate(targetUserId, followerCount));

        return response;
    }

    @Transactional(readOnly = true)
    public UserStatsResponse getUserStats(Long userId, String currentUsername) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        long postCount = postRepository.countByAuthorId(userId);
        long followerCount = followRepository.countByFollowedId(userId);
        long followingCount = followRepository.countByFollowerId(userId);

        boolean followedByCurrentUser = false;
        if (currentUsername != null) {
            var currentUser = userRepository.findByUsername(currentUsername);
            if (currentUser.isPresent()) {
                followedByCurrentUser = followRepository.existsByFollowerIdAndFollowedId(
                    currentUser.get().getId(), userId
                );
            }
        }

        return new UserStatsResponse(
            user.getId(),
            user.getUsername(),
            postCount,
            followerCount,
            followingCount,
            followedByCurrentUser
        );
    }

    public static class FollowerUpdate {
        private Long userId;
        private long followerCount;
        
        public FollowerUpdate(Long userId, long followerCount) {
            this.userId = userId;
            this.followerCount = followerCount;
        }
        
        public Long getUserId() { return userId; }
        public long getFollowerCount() { return followerCount; }
    }
}