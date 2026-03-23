package io.aotchoun.blog.service;

import io.aotchoun.blog.entity.Post;
import io.aotchoun.blog.entity.Report;
import io.aotchoun.blog.entity.User;
import io.aotchoun.blog.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * AdminService - Logique métier pour l'administration
 */
@Service
public class AdminService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private LikeRepository likeRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private ReportRepository reportRepository;

    // ═══════════════════════════════════════════════════
    // USERS
    // ═══════════════════════════════════════════════════

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public long getUserPostCount(Long userId) {
        return postRepository.countByAuthorId(userId);
    }

    public long getUserReportCount(Long userId) {
        // Nombre de fois que cet utilisateur a été signalé
        return reportRepository.countByReportedUserId(userId);
    }

    @Transactional
    public User toggleBanUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        // Toggle le statut banned
        user.setIsBanned(!user.getIsBanned());
        return userRepository.save(user);
    }

    // ═══════════════════════════════════════════════════
    // POSTS
    // ═══════════════════════════════════════════════════

    public List<Post> getAllPosts() {
        return postRepository.findAllByOrderByCreatedAtDesc();
    }

    public long getPostLikeCount(Long postId) {
        return likeRepository.countByPostId(postId);
    }

    public long getPostCommentCount(Long postId) {
        return commentRepository.countByPostId(postId);
    }

    @Transactional
    public void deletePost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        
        // Supprimer d'abord les dépendances
        likeRepository.deleteByPostId(postId);
        commentRepository.deleteByPostId(postId);
        
        // Puis le post
        postRepository.delete(post);
    }

    // ═══════════════════════════════════════════════════
    // REPORTS
    // ═══════════════════════════════════════════════════

    public List<Report> getAllReports() {
        return reportRepository.findAllByOrderByCreatedAtDesc();
    }

    @Transactional
    public Report updateReportStatus(Long reportId, String newStatus) {
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new RuntimeException("Report not found"));
        
        // Convertir String vers Enum
        Report.ReportStatus status = Report.ReportStatus.valueOf(newStatus);
        report.setStatus(status);
        
        return reportRepository.save(report);
    }
}