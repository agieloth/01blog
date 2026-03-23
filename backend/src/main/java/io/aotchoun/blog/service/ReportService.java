package io.aotchoun.blog.service;

import io.aotchoun.blog.dto.request.ReportRequest;
import io.aotchoun.blog.dto.response.ReportResponse;
import io.aotchoun.blog.entity.Report;
import io.aotchoun.blog.entity.User;
import io.aotchoun.blog.exception.BadRequestException;
import io.aotchoun.blog.exception.ResourceNotFoundException;
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

    public ReportService(ReportRepository reportRepository, UserRepository userRepository) {
        this.reportRepository = reportRepository;
        this.userRepository = userRepository;
    }

    public ReportResponse createReport(Long reportedUserId, ReportRequest request, String reporterUsername) {
        User reporter = userRepository.findByUsername(reporterUsername)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + reporterUsername));
        
        User reportedUser = userRepository.findById(reportedUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + reportedUserId));

        // On ne peut pas se signaler soi-même
        if (reporter.getId().equals(reportedUserId)) {
            throw new BadRequestException("You cannot report yourself");
        }

        // Vérifier si déjà signalé
        if (reportRepository.existsByReporterIdAndReportedUserId(reporter.getId(), reportedUserId)) {
            throw new BadRequestException("You have already reported this user");
        }

        // Valider la raison
        Report.ReportReason reason;
        try {
            reason = Report.ReportReason.valueOf(request.getReason().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid report reason: " + request.getReason());
        }

        Report report = new Report(reporter, reportedUser, reason, request.getDescription());
        report = reportRepository.save(report);

        return ReportResponse.from(report);
    }

    @Transactional(readOnly = true)
    public List<ReportResponse> getAllReports() {
        return reportRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(ReportResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ReportResponse> getReportsByStatus(String statusStr) {
        Report.ReportStatus status;
        try {
            status = Report.ReportStatus.valueOf(statusStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid status: " + statusStr);
        }

        return reportRepository.findByStatusOrderByCreatedAtDesc(status)
                .stream()
                .map(ReportResponse::from)
                .collect(Collectors.toList());
    }

    public ReportResponse updateReportStatus(Long reportId, String newStatusStr, String reviewerUsername) {
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new ResourceNotFoundException("Report not found with id: " + reportId));

        Report.ReportStatus newStatus;
        try {
            newStatus = Report.ReportStatus.valueOf(newStatusStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid status: " + newStatusStr);
        }

        User reviewer = userRepository.findByUsername(reviewerUsername)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + reviewerUsername));

        report.setStatus(newStatus);
        report.setReviewedAt(LocalDateTime.now());
        report.setReviewedBy(reviewer);

        report = reportRepository.save(report);
        return ReportResponse.from(report);
    }
}