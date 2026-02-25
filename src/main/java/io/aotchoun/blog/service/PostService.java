package io.aotchoun.blog.service;

import io.aotchoun.blog.dto.request.PostRequest;
import io.aotchoun.blog.dto.response.PostResponse;
import io.aotchoun.blog.entity.Post;
import io.aotchoun.blog.entity.User;
import io.aotchoun.blog.exception.ResourceNotFoundException;
import io.aotchoun.blog.exception.UnauthorizedException;
import io.aotchoun.blog.repository.CommentRepository;
import io.aotchoun.blog.repository.LikeRepository;
import io.aotchoun.blog.repository.PostRepository;
import io.aotchoun.blog.repository.UserRepository;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final LikeRepository likeRepository;
    private final CommentRepository commentRepository;
    private final SimpMessagingTemplate messagingTemplate;

    public PostService(PostRepository postRepository,
                       UserRepository userRepository,
                       LikeRepository likeRepository,
                       CommentRepository commentRepository,
                       SimpMessagingTemplate messagingTemplate) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.likeRepository = likeRepository;
        this.commentRepository = commentRepository;
        this.messagingTemplate = messagingTemplate;
    }

    private PostResponse enrich(Post post, String username) {
        long likes    = likeRepository.countByPostId(post.getId());
        long comments = commentRepository.countByPostId(post.getId());
        boolean liked = false;
        if (username != null) {
            var user = userRepository.findByUsername(username);
            if (user.isPresent()) {
                liked = likeRepository.existsByUserIdAndPostId(user.get().getId(), post.getId());
            }
        }
        return PostResponse.from(post, likes, comments, liked);
    }

    @Transactional(readOnly = true)
    public List<PostResponse> getAllPosts(String username) {
        return postRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(post -> enrich(post, username))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public PostResponse getPostById(Long id, String username) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with id: " + id));
        return enrich(post, username);
    }

    /**
     * Récupérer tous les posts d'un utilisateur
     */
    @Transactional(readOnly = true)
    public List<PostResponse> getPostsByUser(Long userId, String username) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User not found with id: " + userId);
        }
        return postRepository.findByAuthorIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(post -> enrich(post, username))
                .collect(Collectors.toList());
    }

    public PostResponse createPost(PostRequest request, String username) {
        User author = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));
        Post post = new Post(request.getTitle(), request.getContent(), author);
        post = postRepository.save(post);
        PostResponse response = enrich(post, username);

        messagingTemplate.convertAndSend("/topic/posts", 
            new WsEvent("POST_CREATED", response));
        return response;
    }

    public PostResponse updatePost(Long id, PostRequest request, String username) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with id: " + id));
        if (!post.getAuthor().getUsername().equals(username)) {
            throw new UnauthorizedException("You can only edit your own posts");
        }
        post.setTitle(request.getTitle());
        post.setContent(request.getContent());
        post = postRepository.save(post);
        PostResponse response = enrich(post, username);

        messagingTemplate.convertAndSend("/topic/posts",
            new WsEvent("POST_UPDATED", response));
        return response;
    }

    public void deletePost(Long id, String username) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with id: " + id));
        if (!post.getAuthor().getUsername().equals(username)) {
            throw new UnauthorizedException("You can only delete your own posts");
        }
        postRepository.deleteById(id);

        messagingTemplate.convertAndSend("/topic/posts",
            new WsEvent("POST_DELETED", id));
    }

    public static class WsEvent {
        private String type;
        private Object data;
        public WsEvent(String type, Object data) { this.type = type; this.data = data; }
        public String getType() { return type; }
        public Object getData() { return data; }
    }
}