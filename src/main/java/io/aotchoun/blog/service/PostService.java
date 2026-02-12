package io.aotchoun.blog.service;

import io.aotchoun.blog.dto.request.PostRequest;
import io.aotchoun.blog.dto.response.PostResponse;
import io.aotchoun.blog.entity.Post;
import io.aotchoun.blog.entity.User;
import io.aotchoun.blog.exception.BadRequestException;
import io.aotchoun.blog.exception.ResourceNotFoundException;
import io.aotchoun.blog.exception.UnauthorizedException;
import io.aotchoun.blog.repository.PostRepository;
import io.aotchoun.blog.repository.UserRepository;
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

    public PostService(PostRepository postRepository, UserRepository userRepository) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
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
    public List<PostResponse> getAllPosts() {
        return postRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(PostResponse::from)
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
    public PostResponse getPostById(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Post not found with id: " + id
                ));
        return PostResponse.from(post);
    }

    /**
     * Crée un nouveau post
     *
     * @param request Les données du post (title, content)
     * @param username Le username de l'auteur (extrait du token JWT)
     * @return PostResponse du post créé
     */
    public PostResponse createPost(PostRequest request, String username) {
        // Récupérer l'utilisateur depuis la DB
        User author = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User not found: " + username
                ));

        // Créer et sauvegarder le post
        Post post = new Post(request.getTitle(), request.getContent(), author);
        post = postRepository.save(post);

        return PostResponse.from(post);
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
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Post not found with id: " + id
                ));

        // Vérifier que c'est bien l'auteur qui modifie
        // SÉCURITÉ : sans cette vérification, n'importe qui pourrait modifier n'importe quel post !
        if (!post.getAuthor().getUsername().equals(username)) {
            throw new UnauthorizedException("You can only edit your own posts");
        }

        post.setTitle(request.getTitle());
        post.setContent(request.getContent());
        post = postRepository.save(post);

        return PostResponse.from(post);
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
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Post not found with id: " + id
                ));

        // Vérifier que c'est bien l'auteur qui supprime
        if (!post.getAuthor().getUsername().equals(username)) {
            throw new UnauthorizedException("You can only delete your own posts");
        }

        postRepository.deleteById(id);
    }
}