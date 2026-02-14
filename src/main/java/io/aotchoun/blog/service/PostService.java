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

/**
 * Service pour la gestion des posts
 *
 * Contient toute la logique métier :
 * - Récupérer les posts
 * - Créer un post
 * - Modifier un post (seulement son auteur)
 * - Supprimer un post (seulement son auteur)
 */
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

    /**
     * Enrichit un PostResponse avec les compteurs de likes et commentaires.
     * username peut être null (utilisateur non connecté → likedByCurrentUser = false)
     */
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

    /**
     * Récupère tous les posts (du plus récent au plus ancien)
     *
     * @return Liste de PostResponse
     *
     * EXPLICATION .stream().map().collect() :
     * C'est l'équivalent Java de .map() en JavaScript ou Rust.
     * On transforme chaque Post en PostResponse avec PostResponse.from()
     */
    @Transactional(readOnly = true)
    public List<PostResponse> getAllPosts(String username) {
        return postRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(post -> enrich(post, username))
                .collect(Collectors.toList());
    }

    /**
     * Récupère un post par son ID
     *
     * @param id L'ID du post
     * @return PostResponse
     * @throws ResourceNotFoundException si le post n'existe pas
     */
    @Transactional(readOnly = true)
    public PostResponse getPostById(Long id, String username) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with id: " + id));
        return enrich(post, username);
    }

    /**
     * Crée un nouveau post
     *
     * @param request Les données du post (title, content)
     * @param username Le username de l'auteur (extrait du token JWT)
     * @return PostResponse du post créé
     */
    public PostResponse createPost(PostRequest request, String username) {
        User author = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));
        Post post = new Post(request.getTitle(), request.getContent(), author);
        post = postRepository.save(post);
        PostResponse response = enrich(post, username);

        // Broadcaster à tous les clients connectés
        messagingTemplate.convertAndSend("/topic/posts", 
            new WsEvent("POST_CREATED", response));
        return response;
    }

    /**
     * Modifie un post existant
     *
     * @param id L'ID du post à modifier
     * @param request Les nouvelles données
     * @param username Le username de celui qui fait la requête
     * @return PostResponse mis à jour
     * @throws ResourceNotFoundException si le post n'existe pas
     * @throws UnauthorizedException si l'utilisateur n'est pas l'auteur
     */
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

    /**
     * Supprime un post
     *
     * @param id L'ID du post à supprimer
     * @param username Le username de celui qui fait la requête
     * @throws ResourceNotFoundException si le post n'existe pas
     * @throws UnauthorizedException si l'utilisateur n'est pas l'auteur
     */
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

    // Classe interne simple pour wrapper les événements WebSocket
    public static class WsEvent {
        private String type;
        private Object data;
        public WsEvent(String type, Object data) { this.type = type; this.data = data; }
        public String getType() { return type; }
        public Object getData() { return data; }
    }
}