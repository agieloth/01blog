package io.aotchoun.blog.service;

import io.aotchoun.blog.entity.Post;
import io.aotchoun.blog.entity.Report;
import io.aotchoun.blog.entity.User;
import io.aotchoun.blog.exception.ResourceNotFoundException;
import io.aotchoun.blog.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * AdminService - Logique métier pour l'administration
 *
 * FIX v2 :
 * - Injection par constructeur (cohérence avec le reste du projet, testabilité)
 * - RuntimeException → ResourceNotFoundException (gestion d'erreur propre)
 * - deletePost() : suppression des likes/commentaires retirée car CascadeType.ALL
 *   sur Post.comments et Post.likes gère déjà la cascade automatiquement
 */
@Service
@Transactional
public class AdminService {

    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final LikeRepository likeRepository;
    private final CommentRepository commentRepository;
    private final ReportRepository reportRepository;

    public AdminService(UserRepository userRepository,
                        PostRepository postRepository,
                        LikeRepository likeRepository,
                        CommentRepository commentRepository,
                        ReportRepository reportRepository) {
        this.userRepository = userRepository;
        this.postRepository = postRepository;
        this.likeRepository = likeRepository;
        this.commentRepository = commentRepository;
        this.reportRepository = reportRepository;
    }

    // ══════════════════════════════════════════════════════════════════
    // USERS
    // ══════════════════════════════════════════════════════════════════

    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Transactional(readOnly = true)
    public long getUserPostCount(Long userId) {
        return postRepository.countByAuthorId(userId);
    }

    @Transactional(readOnly = true)
    public long getUserReportCount(Long userId) {
        return reportRepository.countByReportedUserId(userId);
    }

    public User toggleBanUser(Long userId) {
        // FIX : ResourceNotFoundException au lieu de RuntimeException
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        user.setIsBanned(!user.getIsBanned());
        return userRepository.save(user);
    }

    // ══════════════════════════════════════════════════════════════════
    // POSTS
    // ══════════════════════════════════════════════════════════════════

    @Transactional(readOnly = true)
    public List<Post> getAllPosts() {
        return postRepository.findAllByOrderByCreatedAtDesc();
    }

    @Transactional(readOnly = true)
    public long getPostLikeCount(Long postId) {
        return likeRepository.countByPostId(postId);
    }

    @Transactional(readOnly = true)
    public long getPostCommentCount(Long postId) {
        return commentRepository.countByPostId(postId);
    }

    public void deletePost(Long postId) {
        // FIX : ResourceNotFoundException au lieu de RuntimeException
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with id: " + postId));

        // FIX : suppression manuelle des likes/commentaires retirée.
        // Post.comments et Post.likes ont CascadeType.ALL + orphanRemoval=true
        // → JPA supprime automatiquement les enfants quand le parent est supprimé.
        // La suppression manuelle précédente causait une double suppression potentielle.
        postRepository.delete(post);
    }

    // ══════════════════════════════════════════════════════════════════
    // REPORTS
    // ══════════════════════════════════════════════════════════════════

    @Transactional(readOnly = true)
    public List<Report> getAllReports() {
        return reportRepository.findAllByOrderByCreatedAtDesc();
    }

    public Report updateReportStatus(Long reportId, String newStatus) {
        // FIX : ResourceNotFoundException au lieu de RuntimeException
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new ResourceNotFoundException("Report not found with id: " + reportId));

        Report.ReportStatus status;
        try {
            status = Report.ReportStatus.valueOf(newStatus.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new io.aotchoun.blog.exception.BadRequestException("Invalid report status: " + newStatus);
        }

        report.setStatus(status);
        return reportRepository.save(report);
    }
}