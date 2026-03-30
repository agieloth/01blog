package io.aotchoun.blog.controller;

import io.aotchoun.blog.dto.request.PostRequest;
import io.aotchoun.blog.dto.response.PostResponse;
import io.aotchoun.blog.service.PostService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
// @CrossOrigin(origins = "*", allowedHeaders = "*")
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping
    public ResponseEntity<List<PostResponse>> getAllPosts(Authentication auth) {
        String username = auth != null ? auth.getName() : null;
        return ResponseEntity.ok(postService.getAllPosts(username));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PostResponse> getPostById(@PathVariable Long id, Authentication auth) {
        String username = auth != null ? auth.getName() : null;
        return ResponseEntity.ok(postService.getPostById(id, username));
    }

    /**
     * Créer un post avec jusqu'à 3 images
     * Content-Type: multipart/form-data
     * 
     * Params:
     * - title (string, required)
     * - content (string, required)
     * - images (file[], optional, max 3)
     */
    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<PostResponse> createPost(
            @RequestParam("title") String title,
            @RequestParam("content") String content,
            @RequestParam(value = "images", required = false) MultipartFile[] images,
            Authentication auth) {
        
        PostRequest request = new PostRequest();
        request.setTitle(title);
        request.setContent(content);
        
        PostResponse post = postService.createPost(request, images, auth.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(post);
    }

    /**
     * Modifier un post avec possibilité de changer les images
     * Content-Type: multipart/form-data
     * 
     * Params:
     * - title (string, required)
     * - content (string, required)
     * - images (file[], optional, max 3) - si fourni, remplace toutes les anciennes images
     */
    @PutMapping(value = "/{id}", consumes = "multipart/form-data")
    public ResponseEntity<PostResponse> updatePost(
            @PathVariable Long id,
            @RequestParam("title") String title,
            @RequestParam("content") String content,
            @RequestParam(value = "images", required = false) MultipartFile[] images,
            Authentication auth) {
        
        PostRequest request = new PostRequest();
        request.setTitle(title);
        request.setContent(content);
        
        PostResponse post = postService.updatePost(id, request, images, auth.getName());
        return ResponseEntity.ok(post);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable Long id, Authentication auth) {
        postService.deletePost(id, auth.getName());
        return ResponseEntity.noContent().build();
    }
}