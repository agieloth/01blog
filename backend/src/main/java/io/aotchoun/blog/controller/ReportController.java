package io.aotchoun.blog.controller;

import io.aotchoun.blog.dto.request.ReportRequest;
import io.aotchoun.blog.dto.response.ReportResponse;
import io.aotchoun.blog.service.ReportService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reports")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    /**
     * Créer un signalement (accessible à tous les utilisateurs authentifiés)
     */
    @PostMapping("/user/{userId}")
    public ResponseEntity<ReportResponse> reportUser(
            @PathVariable Long userId,
            @Valid @RequestBody ReportRequest request,
            Authentication auth) {
        ReportResponse report = reportService.createReport(userId, request, auth.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(report);
    }

    /**
     * Récupérer tous les signalements (admin uniquement)
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ReportResponse>> getAllReports() {
        List<ReportResponse> reports = reportService.getAllReports();
        return ResponseEntity.ok(reports);
    }

    /**
     * Récupérer les signalements par statut (admin uniquement)
     */
    @GetMapping("/status/{status}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ReportResponse>> getReportsByStatus(@PathVariable String status) {
        List<ReportResponse> reports = reportService.getReportsByStatus(status);
        return ResponseEntity.ok(reports);
    }

    /**
     * Mettre à jour le statut d'un signalement (admin uniquement)
     */
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ReportResponse> updateReportStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> body,
            Authentication auth) {
        String newStatus = body.get("status");
        ReportResponse report = reportService.updateReportStatus(id, newStatus, auth.getName());
        return ResponseEntity.ok(report);
    }
}