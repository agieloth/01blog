package io.aotchoun.blog.controller;

import io.aotchoun.blog.dto.request.PostRequest;
import io.aotchoun.blog.dto.response.PostResponse;
import io.aotchoun.blog.service.PostService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller pour les Posts
 *
 * @AuthenticationPrincipal :
 * Injecte automatiquement l'utilisateur authentifié depuis le SecurityContext.
 * C'est Spring Security qui remplit ça grâce à notre JwtAuthFilter.
 *
 * C'est l'équivalent de "req.user" dans Express.js (Node.js).
 */
@RestController
@RequestMapping("/api/posts")
@CrossOrigin(origins = "*")
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    /**
     * GET /api/posts
     * Public — retourne tous les posts
     */
    @GetMapping
    public ResponseEntity<List<PostResponse>> getAllPosts(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        // username peut être null si l'utilisateur n'est pas connecté
        String username = userDetails != null ? userDetails.getUsername() : null;
        return ResponseEntity.ok(postService.getAllPosts(username));
    }

    /**
     * GET /api/posts/{id}
     * Public — retourne un post par son ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<PostResponse> getPostById(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        String username = userDetails != null ? userDetails.getUsername() : null;
        return ResponseEntity.ok(postService.getPostById(id, username));
    }

    /**
     * POST /api/posts
     * Protégé — crée un nouveau post
     *
     * @AuthenticationPrincipal UserDetails userDetails
     * → Spring injecte l'utilisateur connecté automatiquement
     * → userDetails.getUsername() donne le username depuis le token JWT
     */
    @PostMapping
    public ResponseEntity<PostResponse> createPost(
            @Valid @RequestBody PostRequest request,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        PostResponse post = postService.createPost(request, userDetails.getUsername());
        return new ResponseEntity<>(post, HttpStatus.CREATED);
    }

    /**
     * PUT /api/posts/{id}
     * Protégé — modifie un post (seulement l'auteur)
     */
    @PutMapping("/{id}")
    public ResponseEntity<PostResponse> updatePost(
            @PathVariable Long id,
            @Valid @RequestBody PostRequest request,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        return ResponseEntity.ok(postService.updatePost(id, request, userDetails.getUsername()));
    }

    /**
     * DELETE /api/posts/{id}
     * Protégé — supprime un post (seulement l'auteur)
     *
     * 204 No Content : succès mais pas de body dans la réponse
     * C'est la convention REST pour les suppressions.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        postService.deletePost(id, userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }
}