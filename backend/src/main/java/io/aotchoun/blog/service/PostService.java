package io.aotchoun.blog.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.aotchoun.blog.dto.request.PostRequest;
import io.aotchoun.blog.dto.response.PostResponse;
import io.aotchoun.blog.entity.Notification;
import io.aotchoun.blog.entity.Post;
import io.aotchoun.blog.entity.User;
import io.aotchoun.blog.exception.BadRequestException;
import io.aotchoun.blog.exception.ResourceNotFoundException;
import io.aotchoun.blog.exception.UnauthorizedException;
import io.aotchoun.blog.repository.CommentRepository;
import io.aotchoun.blog.repository.LikeRepository;
import io.aotchoun.blog.repository.PostRepository;
import io.aotchoun.blog.repository.UserRepository;
import io.aotchoun.blog.repository.FollowRepository;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final LikeRepository likeRepository;
    private final CommentRepository commentRepository;
    private final FollowRepository followRepository;
    private final NotificationService notificationService;
    private final SimpMessagingTemplate messagingTemplate;
    private final FileStorageService fileStorageService;
    private final ObjectMapper objectMapper; // FIX : injecté par Spring (bean configuré)

    public PostService(PostRepository postRepository,
                       UserRepository userRepository,
                       LikeRepository likeRepository,
                       CommentRepository commentRepository,
                       FollowRepository followRepository,
                       NotificationService notificationService,
                       SimpMessagingTemplate messagingTemplate,
                       FileStorageService fileStorageService,
                       ObjectMapper objectMapper) {
        this.postRepository    = postRepository;
        this.userRepository    = userRepository;
        this.likeRepository    = likeRepository;
        this.commentRepository = commentRepository;
        this.followRepository = followRepository;
        this.notificationService = notificationService;
        this.messagingTemplate = messagingTemplate;
        this.fileStorageService = fileStorageService;
        this.objectMapper      = objectMapper;
    }

    // ──────────────────────────────────────────────────────────────────
    // Méthode privée : enrichit un Post avec ses compteurs
    // FIX : utilise objectMapper injecté pour parser les imageUrls
    // ──────────────────────────────────────────────────────────────────

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
        List<String> imageUrls = parseImageUrls(post.getImageUrls());
        return PostResponse.from(post, imageUrls, likes, comments, liked);
    }

    /**
     * Parse le JSON des imageUrls stocké en base.
     * FIX : utilise l'ObjectMapper injecté (thread-safe, config Spring appliquée).
     */
    private List<String> parseImageUrls(String json) {
        if (json == null || json.isEmpty()) return new ArrayList<>();
        try {
            return objectMapper.readValue(json, new TypeReference<List<String>>() {});
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    // ──────────────────────────────────────────────────────────────────
    // READ
    // ──────────────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public List<PostResponse> getAllPosts(String username) {
        // Les utilisateurs normaux ne voient pas les posts masqués par l'admin
        return postRepository.findByHiddenFalseOrderByCreatedAtDesc()
                .stream()
                .map(post -> enrich(post, username))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public PostResponse getPostById(Long id, String username) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with id: " + id));
        // Un post masqué n'est pas accessible directement par un user normal
        if (Boolean.TRUE.equals(post.getHidden())) {
            throw new ResourceNotFoundException("Post not found with id: " + id);
        }
        return enrich(post, username);
    }

    @Transactional(readOnly = true)
    public List<PostResponse> getPostsByUser(Long userId, String username) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User not found with id: " + userId);
        }
        // On exclut également les posts masqués du profil public
        return postRepository.findByAuthorIdAndHiddenFalseOrderByCreatedAtDesc(userId)
                .stream()
                .map(post -> enrich(post, username))
                .collect(Collectors.toList());
    }

    // ──────────────────────────────────────────────────────────────────
    // CREATE
    // ──────────────────────────────────────────────────────────────────

    public PostResponse createPost(PostRequest request, MultipartFile[] images, String username) {
        User author = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));

        Post post = new Post(request.getTitle(), request.getContent(), author);

        if (images != null && images.length > 0) {
            if (images.length > 3) {
                throw new BadRequestException("Maximum 3 images allowed per post");
            }
            List<String> imageUrls = uploadImages(images);
            serializeImageUrls(post, imageUrls);
        }

        post = postRepository.save(post);
        PostResponse response = enrich(post, username);

        // ── Notifier tous les followers ──────────────────────────────────────
        notifyFollowers(author, post);

        messagingTemplate.convertAndSend("/topic/posts",
                new WsEvent("POST_CREATED", response));
        return response;
    }

    // ──────────────────────────────────────────────────────────────────
    // UPDATE
    // ──────────────────────────────────────────────────────────────────

    public PostResponse updatePost(Long id, PostRequest request, MultipartFile[] images, String username) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with id: " + id));

        if (!post.getAuthor().getUsername().equals(username)) {
            throw new UnauthorizedException("You can only edit your own posts");
        }

        post.setTitle(request.getTitle());
        post.setContent(request.getContent());

        if (images != null && images.length > 0) {
            if (images.length > 3) {
                throw new BadRequestException("Maximum 3 images allowed per post");
            }
            deletePostImages(post);
            List<String> imageUrls = uploadImages(images);
            serializeImageUrls(post, imageUrls);
        }

        post = postRepository.save(post);
        PostResponse response = enrich(post, username);

        messagingTemplate.convertAndSend("/topic/posts",
                new WsEvent("POST_UPDATED", response));
        return response;
    }


    /**
     * Envoie une notification NEW_POST à chaque follower de l'auteur.
     * Exécuté de façon synchrone mais dans la même transaction.
     */
    private void notifyFollowers(User author, Post post) {
        // Récupérer tous les abonnés de l'auteur
        var followers = followRepository.findByFollowedId(author.getId());
 
        for (var follow : followers) {
            User follower = follow.getFollower();
            notificationService.createNotification(
                follower.getId(),
                Notification.NotificationType.NEW_POST,
                author.getUsername() + " a publié un nouveau post : " + post.getTitle(),
                post.getId(),       // relatedEntityId = post.id → pour naviguer vers le post
                author.getUsername()
            );
        }
    }

    // ──────────────────────────────────────────────────────────────────
    // DELETE
    // ──────────────────────────────────────────────────────────────────

    public void deletePost(Long id, String username) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with id: " + id));

        if (!post.getAuthor().getUsername().equals(username)) {
            throw new UnauthorizedException("You can only delete your own posts");
        }

        deletePostImages(post);
        postRepository.deleteById(id);

        messagingTemplate.convertAndSend("/topic/posts",
                new WsEvent("POST_DELETED", id));
    }

    // ──────────────────────────────────────────────────────────────────
    // Helpers privés
    // ──────────────────────────────────────────────────────────────────

    private List<String> uploadImages(MultipartFile[] images) {
        List<String> urls = new ArrayList<>();
        for (MultipartFile image : images) {
            if (!image.isEmpty()) {
                urls.add(fileStorageService.storeFile(image));
            }
        }
        return urls;
    }

    private void serializeImageUrls(Post post, List<String> imageUrls) {
        try {
            post.setImageUrls(objectMapper.writeValueAsString(imageUrls));
        } catch (Exception e) {
            throw new RuntimeException("Error serializing image URLs", e);
        }
    }

    private void deletePostImages(Post post) {
        if (post.getImageUrls() == null || post.getImageUrls().isEmpty()) return;
        List<String> urls = parseImageUrls(post.getImageUrls());
        urls.forEach(fileStorageService::deleteFile);
    }

    // ──────────────────────────────────────────────────────────────────
    // WebSocket event wrapper
    // ──────────────────────────────────────────────────────────────────

    public static class WsEvent {
        private final String type;
        private final Object data;

        public WsEvent(String type, Object data) {
            this.type = type;
            this.data = data;
        }

        public String getType() { return type; }
        public Object getData() { return data; }
    }
}