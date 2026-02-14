package io.aotchoun.blog.service;

import io.aotchoun.blog.dto.response.LikeResponse;
import io.aotchoun.blog.entity.Like;
import io.aotchoun.blog.entity.Post;
import io.aotchoun.blog.entity.User;
import io.aotchoun.blog.exception.ResourceNotFoundException;
import io.aotchoun.blog.repository.LikeRepository;
import io.aotchoun.blog.repository.PostRepository;
import io.aotchoun.blog.repository.UserRepository;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class LikeService {

    private final LikeRepository likeRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final SimpMessagingTemplate messagingTemplate;

    public LikeService(LikeRepository likeRepository,
                       PostRepository postRepository,
                       UserRepository userRepository,
                       SimpMessagingTemplate messagingTemplate) {
        this.likeRepository = likeRepository;
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.messagingTemplate = messagingTemplate;
    }

    /**
     * Toggle like : like si pas déjà liké, unlike sinon.
     * Retourne l'état mis à jour (count + likedByCurrentUser).
     */
    public LikeResponse toggleLike(Long postId, String username) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with id: " + postId));
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));

        var existing = likeRepository.findByUserIdAndPostId(user.getId(), postId);
        boolean liked;
        if (existing.isPresent()) {
            likeRepository.delete(existing.get());
            liked = false;
        } else {
            likeRepository.save(new Like(user, post));
            liked = true;
        }
        long count = likeRepository.countByPostId(postId);
        LikeResponse response = new LikeResponse(postId, count, liked);

        // Broadcaster le nouveau count de likes à tous les clients
        // Chaque client recalculera likedByCurrentUser côté frontend
        messagingTemplate.convertAndSend("/topic/likes",
            new LikeUpdate(postId, count));
        return response;
    }

    // Payload public (sans likedByCurrentUser — chaque client gère son propre état)
    public static class LikeUpdate {
        private Long postId; private long likeCount;
        public LikeUpdate(Long postId, long likeCount) { this.postId = postId; this.likeCount = likeCount; }
        public Long getPostId() { return postId; }
        public long getLikeCount() { return likeCount; }
    }

    // @Transactional(readOnly = true)
    // public LikeResponse getLikeInfo(Long postId, String username) {
    //     if (!postRepository.existsById(postId)) {
    //         throw new ResourceNotFoundException("Post not found with id: " + postId);
    //     }
    //     long count = likeRepository.countByPostId(postId);
    //     boolean liked = false;
    //     if (username != null) {
    //         userRepository.findByUsername(username).ifPresent(u -> {});
    //         var user = userRepository.findByUsername(username);
    //         if (user.isPresent()) {
    //             liked = likeRepository.existsByUserIdAndPostId(user.get().getId(), postId);
    //         }
    //     }
    //     return new LikeResponse(postId, count, liked);
    // }
}