package io.aotchoun.blog.service;

import io.aotchoun.blog.dto.response.LikeResponse;
import io.aotchoun.blog.entity.Like;
import io.aotchoun.blog.entity.Post;
import io.aotchoun.blog.entity.User;
import io.aotchoun.blog.exception.ResourceNotFoundException;
import io.aotchoun.blog.repository.LikeRepository;
import io.aotchoun.blog.repository.PostRepository;
import io.aotchoun.blog.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class LikeService {

    private final LikeRepository likeRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public LikeService(LikeRepository likeRepository,
                       PostRepository postRepository,
                       UserRepository userRepository) {
        this.likeRepository = likeRepository;
        this.postRepository = postRepository;
        this.userRepository = userRepository;
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

        // Chercher un like existant de cet utilisateur sur ce post
        java.util.Optional<Like> existing = likeRepository.findByUserIdAndPostId(user.getId(), postId);

        boolean liked;
        if (existing.isPresent()) {
            // Déjà liké → on unlike
            likeRepository.delete(existing.get());
            liked = false;
        } else {
            // Pas encore liké → on like
            likeRepository.save(new Like(user, post));
            liked = true;
        }

        long count = likeRepository.countByPostId(postId);
        return new LikeResponse(postId, count, liked);
    }

    @Transactional(readOnly = true)
    public LikeResponse getLikeInfo(Long postId, String username) {
        if (!postRepository.existsById(postId)) {
            throw new ResourceNotFoundException("Post not found with id: " + postId);
        }
        long count = likeRepository.countByPostId(postId);
        boolean liked = false;
        if (username != null) {
            userRepository.findByUsername(username).ifPresent(u -> {});
            var user = userRepository.findByUsername(username);
            if (user.isPresent()) {
                liked = likeRepository.existsByUserIdAndPostId(user.get().getId(), postId);
            }
        }
        return new LikeResponse(postId, count, liked);
    }
}