// package io.aotchoun.blog.service;

// import io.aotchoun.blog.dto.request.ReportRequest;
// import io.aotchoun.blog.dto.response.ReportResponse;
// import io.aotchoun.blog.entity.Report;
// import io.aotchoun.blog.entity.User;
// import io.aotchoun.blog.exception.BadRequestException;
// import io.aotchoun.blog.exception.ResourceNotFoundException;
// import io.aotchoun.blog.repository.ReportRepository;
// import io.aotchoun.blog.repository.UserRepository;
// import org.springframework.stereotype.Service;
// import org.springframework.transaction.annotation.Transactional;

// import java.time.LocalDateTime;
// import java.util.List;
// import java.util.stream.Collectors;

// @Service
// @Transactional
// public class ReportService {

//     private final ReportRepository reportRepository;
//     private final UserRepository userRepository;

//     public ReportService(ReportRepository reportRepository, UserRepository userRepository) {
//         this.reportRepository = reportRepository;
//         this.userRepository = userRepository;
//     }

//     public ReportResponse createReport(Long reportedUserId, ReportRequest request, String reporterUsername) {
//         User reporter = userRepository.findByUsername(reporterUsername)
//                 .orElseThrow(() -> new ResourceNotFoundException("User not found: " + reporterUsername));
        
//         User reportedUser = userRepository.findById(reportedUserId)
//                 .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + reportedUserId));

//         // On ne peut pas se signaler soi-même
//         if (reporter.getId().equals(reportedUserId)) {
//             throw new BadRequestException("You cannot report yourself");
//         }

//         // Vérifier si déjà signalé
//         if (reportRepository.existsByReporterIdAndReportedUserId(reporter.getId(), reportedUserId)) {
//             throw new BadRequestException("You have already reported this user");
//         }

//         // Valider la raison
//         Report.ReportReason reason;
//         try {
//             reason = Report.ReportReason.valueOf(request.getReason().toUpperCase());
//         } catch (IllegalArgumentException e) {
//             throw new BadRequestException("Invalid report reason: " + request.getReason());
//         }

//         Report report = new Report(reporter, reportedUser, reason, request.getDescription());
//         report = reportRepository.save(report);

//         return ReportResponse.from(report);
//     }

//     @Transactional(readOnly = true)
//     public List<ReportResponse> getAllReports() {
//         return reportRepository.findAllByOrderByCreatedAtDesc()
//                 .stream()
//                 .map(ReportResponse::from)
//                 .collect(Collectors.toList());
//     }

//     @Transactional(readOnly = true)
//     public List<ReportResponse> getReportsByStatus(String statusStr) {
//         Report.ReportStatus status;
//         try {
//             status = Report.ReportStatus.valueOf(statusStr.toUpperCase());
//         } catch (IllegalArgumentException e) {
//             throw new BadRequestException("Invalid status: " + statusStr);
//         }

//         return reportRepository.findByStatusOrderByCreatedAtDesc(status)
//                 .stream()
//                 .map(ReportResponse::from)
//                 .collect(Collectors.toList());
//     }

//     public ReportResponse updateReportStatus(Long reportId, String newStatusStr, String reviewerUsername) {
//         Report report = reportRepository.findById(reportId)
//                 .orElseThrow(() -> new ResourceNotFoundException("Report not found with id: " + reportId));

//         Report.ReportStatus newStatus;
//         try {
//             newStatus = Report.ReportStatus.valueOf(newStatusStr.toUpperCase());
//         } catch (IllegalArgumentException e) {
//             throw new BadRequestException("Invalid status: " + newStatusStr);
//         }

//         User reviewer = userRepository.findByUsername(reviewerUsername)
//                 .orElseThrow(() -> new ResourceNotFoundException("User not found: " + reviewerUsername));

//         report.setStatus(newStatus);
//         report.setReviewedAt(LocalDateTime.now());
//         report.setReviewedBy(reviewer);

//         report = reportRepository.save(report);
//         return ReportResponse.from(report);
//     }
// }



package io.aotchoun.blog.service;

import io.aotchoun.blog.dto.request.ReportRequest;
import io.aotchoun.blog.dto.response.ReportResponse;
import io.aotchoun.blog.entity.Post;
import io.aotchoun.blog.entity.Report;
import io.aotchoun.blog.entity.User;
import io.aotchoun.blog.exception.BadRequestException;
import io.aotchoun.blog.exception.ResourceNotFoundException;
import io.aotchoun.blog.repository.PostRepository;
import io.aotchoun.blog.repository.ReportRepository;
import io.aotchoun.blog.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ReportService {

    private final ReportRepository reportRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;

    public ReportService(ReportRepository reportRepository,
                         UserRepository userRepository,
                         PostRepository postRepository) {
        this.reportRepository = reportRepository;
        this.userRepository   = userRepository;
        this.postRepository   = postRepository;
    }

    // ── Signaler un post ──────────────────────────────────────────────────────

    /**
     * Signale un post. Le reportedUser est l'auteur du post.
     * Un utilisateur peut signaler plusieurs posts du même auteur
     * (contrairement au signalement user qui est unique par couple reporter/reported).
     */
    public ReportResponse createPostReport(Long postId, ReportRequest request, String reporterUsername) {
        User reporter = userRepository.findByUsername(reporterUsername)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + reporterUsername));

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with id: " + postId));

        User reportedUser = post.getAuthor();

        // On ne peut pas signaler son propre post
        if (reporter.getId().equals(reportedUser.getId())) {
            throw new BadRequestException("You cannot report your own post");
        }

        // Valider la raison
        Report.ReportReason reason = parseReason(request.getReason());

        Report report = new Report(
            reporter,
            reportedUser,
            post.getId(),
            post.getTitle(),
            reason,
            request.getDescription()
        );

        report = reportRepository.save(report);
        return ReportResponse.from(report);
    }

    // ── Signaler un utilisateur ───────────────────────────────────────────────

    public ReportResponse createReport(Long reportedUserId, ReportRequest request, String reporterUsername) {
        User reporter = userRepository.findByUsername(reporterUsername)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + reporterUsername));

        User reportedUser = userRepository.findById(reportedUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + reportedUserId));

        if (reporter.getId().equals(reportedUserId)) {
            throw new BadRequestException("You cannot report yourself");
        }

        // Un seul signalement par couple reporter/reported (pour les users)
        if (reportRepository.existsByReporterIdAndReportedUserIdAndPostIdIsNull(
                reporter.getId(), reportedUserId)) {
            throw new BadRequestException("You have already reported this user");
        }

        Report.ReportReason reason = parseReason(request.getReason());
        Report report = new Report(reporter, reportedUser, reason, request.getDescription());
        report = reportRepository.save(report);
        return ReportResponse.from(report);
    }

    // ── Admin ─────────────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public List<ReportResponse> getAllReports() {
        return reportRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(ReportResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ReportResponse> getReportsByStatus(String statusStr) {
        Report.ReportStatus status = parseStatus(statusStr);
        return reportRepository.findByStatusOrderByCreatedAtDesc(status)
                .stream()
                .map(ReportResponse::from)
                .collect(Collectors.toList());
    }

    public ReportResponse updateReportStatus(Long reportId, String newStatusStr, String reviewerUsername) {
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new ResourceNotFoundException("Report not found with id: " + reportId));

        User reviewer = userRepository.findByUsername(reviewerUsername)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + reviewerUsername));

        report.setStatus(parseStatus(newStatusStr));
        report.setReviewedAt(LocalDateTime.now());
        report.setReviewedBy(reviewer);

        return ReportResponse.from(reportRepository.save(report));
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private Report.ReportReason parseReason(String raw) {
        try {
            return Report.ReportReason.valueOf(raw.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid report reason: " + raw);
        }
    }

    private Report.ReportStatus parseStatus(String raw) {
        try {
            return Report.ReportStatus.valueOf(raw.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid status: " + raw);
        }
    }
}