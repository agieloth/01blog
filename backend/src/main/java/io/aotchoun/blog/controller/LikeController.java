package io.aotchoun.blog.controller;

import io.aotchoun.blog.dto.response.LikeResponse;
import io.aotchoun.blog.service.LikeService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/posts/{postId}/like")
// @CrossOrigin(origins = "*")
public class LikeController {

    private final LikeService likeService;

    public LikeController(LikeService likeService) {
        this.likeService = likeService;
    }

    // POST /api/posts/{postId}/like — toggle like (protégé)
    @PostMapping
    public ResponseEntity<LikeResponse> toggleLike(
            @PathVariable Long postId,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        return ResponseEntity.ok(likeService.toggleLike(postId, userDetails.getUsername()));
    }
}