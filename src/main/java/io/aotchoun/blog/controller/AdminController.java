package io.aotchoun.blog.controller;

import io.aotchoun.blog.entity.Post;
import io.aotchoun.blog.entity.Report;
import io.aotchoun.blog.entity.User;
import io.aotchoun.blog.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * AdminController - API pour la gestion administrative
 * 
 * Toutes les routes requièrent le rôle ADMIN
 */
@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    @Autowired
    private AdminService adminService;

    // ═══════════════════════════════════════════════════
    // USERS MANAGEMENT
    // ═══════════════════════════════════════════════════

    /**
     * GET /api/admin/users
     * Liste tous les utilisateurs avec leurs stats
     */
    @GetMapping("/users")
    public ResponseEntity<?> getUsers() {
        List<User> users = adminService.getAllUsers();
        
        List<Map<String, Object>> response = users.stream().map(user -> {
            Map<String, Object> userMap = new HashMap<>();
            userMap.put("id", user.getId());
            userMap.put("username", user.getUsername());
            userMap.put("email", user.getEmail());
            userMap.put("roles", List.of(user.getRole().toString())); // ["USER"] ou ["ADMIN"]
            userMap.put("postCount", adminService.getUserPostCount(user.getId()));
            userMap.put("reportCount", adminService.getUserReportCount(user.getId()));
            userMap.put("banned", user.getIsBanned());
            userMap.put("createdAt", user.getCreatedAt());
            return userMap;
        }).collect(Collectors.toList());
        
        return ResponseEntity.ok(response);
    }

    /**
     * PATCH /api/admin/users/{id}/ban
     * Toggle ban status (ban/unban)
     */
    @PatchMapping("/users/{id}/ban")
    public ResponseEntity<?> toggleBanUser(@PathVariable Long id) {
        User user = adminService.toggleBanUser(id);
        
        Map<String, Object> response = new HashMap<>();
        response.put("id", user.getId());
        response.put("username", user.getUsername());
        response.put("banned", user.getIsBanned());
        
        return ResponseEntity.ok(response);
    }

    // ═══════════════════════════════════════════════════
    // POSTS MANAGEMENT
    // ═══════════════════════════════════════════════════

    /**
     * GET /api/admin/posts
     * Liste tous les posts avec leurs stats
     */
    @GetMapping("/posts")
    public ResponseEntity<?> getPosts() {
        List<Post> posts = adminService.getAllPosts();
        
        List<Map<String, Object>> response = posts.stream().map(post -> {
            Map<String, Object> postMap = new HashMap<>();
            postMap.put("id", post.getId());
            postMap.put("title", post.getTitle());
            postMap.put("content", post.getContent());
            postMap.put("author", post.getAuthor().getUsername());
            postMap.put("likeCount", adminService.getPostLikeCount(post.getId()));
            postMap.put("commentCount", adminService.getPostCommentCount(post.getId()));
            postMap.put("createdAt", post.getCreatedAt());
            return postMap;
        }).collect(Collectors.toList());
        
        return ResponseEntity.ok(response);
    }

    /**
     * DELETE /api/admin/posts/{id}
     * Supprimer un post (admin only)
     */
    @DeleteMapping("/posts/{id}")
    public ResponseEntity<?> deletePost(@PathVariable Long id) {
        adminService.deletePost(id);
        return ResponseEntity.noContent().build();
    }

    // ═══════════════════════════════════════════════════
    // REPORTS MANAGEMENT
    // ═══════════════════════════════════════════════════

    /**
     * GET /api/admin/reports
     * Liste tous les signalements
     */
    @GetMapping("/reports")
    public ResponseEntity<?> getReports() {
        List<Report> reports = adminService.getAllReports();
        
        List<Map<String, Object>> response = reports.stream().map(report -> {
            Map<String, Object> reportMap = new HashMap<>();
            reportMap.put("id", report.getId());
            reportMap.put("reporterUsername", report.getReporter().getUsername());
            reportMap.put("reportedUsername", report.getReportedUser().getUsername());
            reportMap.put("reason", report.getReason().toString());
            reportMap.put("description", report.getDescription());
            reportMap.put("status", report.getStatus().toString());
            reportMap.put("createdAt", report.getCreatedAt());
            return reportMap;
        }).collect(Collectors.toList());
        
        return ResponseEntity.ok(response);
    }

    /**
     * PATCH /api/admin/reports/{id}/status
     * Changer le statut d'un signalement (REVIEWED / DISMISSED)
     */
    @PatchMapping("/reports/{id}/status")
    public ResponseEntity<?> updateReportStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {
        
        String newStatus = body.get("status");
        Report report = adminService.updateReportStatus(id, newStatus);
        
        Map<String, Object> response = new HashMap<>();
        response.put("id", report.getId());
        response.put("status", report.getStatus().toString());
        
        return ResponseEntity.ok(response);
    }
}