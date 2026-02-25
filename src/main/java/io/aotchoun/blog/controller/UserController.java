package io.aotchoun.blog.controller;

import io.aotchoun.blog.dto.response.FollowResponse;
import io.aotchoun.blog.dto.response.PostResponse;
import io.aotchoun.blog.dto.response.UserStatsResponse;
import io.aotchoun.blog.service.FollowService;
import io.aotchoun.blog.service.PostService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {

    private final FollowService followService;
    private final PostService postService;

    public UserController(FollowService followService, PostService postService) {
        this.followService = followService;
        this.postService = postService;
    }

    /**
     * GET /api/users/{userId}/stats
     * Public — stats d'un utilisateur (posts, followers, following)
     */
    @GetMapping("/{userId}/stats")
    public ResponseEntity<UserStatsResponse> getUserStats(
            @PathVariable Long userId,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        String username = userDetails != null ? userDetails.getUsername() : null;
        return ResponseEntity.ok(followService.getUserStats(userId, username));
    }

    /**
     * POST /api/users/{userId}/follow
     * Protégé — toggle follow (suivre ou unfollow)
     */
    @PostMapping("/{userId}/follow")
    public ResponseEntity<FollowResponse> toggleFollow(
            @PathVariable Long userId,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        return ResponseEntity.ok(followService.toggleFollow(userId, userDetails.getUsername()));
    }

    /**
     * GET /api/users/{userId}/posts
     * Public — tous les posts d'un utilisateur
     */
    @GetMapping("/{userId}/posts")
    public ResponseEntity<List<PostResponse>> getUserPosts(
            @PathVariable Long userId,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        String username = userDetails != null ? userDetails.getUsername() : null;
        return ResponseEntity.ok(postService.getPostsByUser(userId, username));
    }
}