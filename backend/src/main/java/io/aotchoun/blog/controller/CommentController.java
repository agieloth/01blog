package io.aotchoun.blog.controller;

import io.aotchoun.blog.dto.request.CommentRequest;
import io.aotchoun.blog.dto.response.CommentResponse;
import io.aotchoun.blog.service.CommentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/posts/{postId}/comments")
// @CrossOrigin(origins = "*")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    // GET /api/posts/{postId}/comments — public
    @GetMapping
    public ResponseEntity<List<CommentResponse>> getComments(@PathVariable Long postId) {
        return ResponseEntity.ok(commentService.getCommentsByPost(postId));
    }

    // POST /api/posts/{postId}/comments — protégé
    @PostMapping
    public ResponseEntity<CommentResponse> addComment(
            @PathVariable Long postId,
            @Valid @RequestBody CommentRequest request,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        CommentResponse comment = commentService.addComment(postId, request, userDetails.getUsername());
        return new ResponseEntity<>(comment, HttpStatus.CREATED);
    }

    // DELETE /api/posts/{postId}/comments/{commentId} — protégé (auteur seulement)
    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(
            @PathVariable Long postId,
            @PathVariable Long commentId,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        commentService.deleteComment(postId, commentId, userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }
}