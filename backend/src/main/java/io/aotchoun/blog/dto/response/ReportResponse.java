// package io.aotchoun.blog.dto.response;

// import io.aotchoun.blog.entity.Report;
// import java.time.LocalDateTime;

// public class ReportResponse {

//     private Long id;
//     private Long reporterId;
//     private String reporterUsername;
//     private Long reportedUserId;
//     private String reportedUsername;
//     private String reason;
//     private String description;
//     private String status;
//     private LocalDateTime createdAt;
//     private LocalDateTime reviewedAt;
//     private String reviewedByUsername;

//     public ReportResponse() {}

//     public static ReportResponse from(Report report) {
//         ReportResponse response = new ReportResponse();
//         response.id = report.getId();
//         response.reporterId = report.getReporter().getId();
//         response.reporterUsername = report.getReporter().getUsername();
//         response.reportedUserId = report.getReportedUser().getId();
//         response.reportedUsername = report.getReportedUser().getUsername();
//         response.reason = report.getReason().name();
//         response.description = report.getDescription();
//         response.status = report.getStatus().name();
//         response.createdAt = report.getCreatedAt();
//         response.reviewedAt = report.getReviewedAt();
//         if (report.getReviewedBy() != null) {
//             response.reviewedByUsername = report.getReviewedBy().getUsername();
//         }
//         return response;
//     }

//     // Getters & Setters
//     public Long getId() { return id; }
//     public Long getReporterId() { return reporterId; }
//     public String getReporterUsername() { return reporterUsername; }
//     public Long getReportedUserId() { return reportedUserId; }
//     public String getReportedUsername() { return reportedUsername; }
//     public String getReason() { return reason; }
//     public String getDescription() { return description; }
//     public String getStatus() { return status; }
//     public LocalDateTime getCreatedAt() { return createdAt; }
//     public LocalDateTime getReviewedAt() { return reviewedAt; }
//     public String getReviewedByUsername() { return reviewedByUsername; }

//     public void setId(Long id) { this.id = id; }
//     public void setReporterId(Long reporterId) { this.reporterId = reporterId; }
//     public void setReporterUsername(String reporterUsername) { this.reporterUsername = reporterUsername; }
//     public void setReportedUserId(Long reportedUserId) { this.reportedUserId = reportedUserId; }
//     public void setReportedUsername(String reportedUsername) { this.reportedUsername = reportedUsername; }
//     public void setReason(String reason) { this.reason = reason; }
//     public void setDescription(String description) { this.description = description; }
//     public void setStatus(String status) { this.status = status; }
//     public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
//     public void setReviewedAt(LocalDateTime reviewedAt) { this.reviewedAt = reviewedAt; }
//     public void setReviewedByUsername(String reviewedByUsername) { this.reviewedByUsername = reviewedByUsername; }
// }


package io.aotchoun.blog.dto.response;

import io.aotchoun.blog.entity.Report;
import java.time.LocalDateTime;

public class ReportResponse {

    private Long id;
    private Long reporterId;
    private String reporterUsername;
    private Long reportedUserId;
    private String reportedUsername;
    private Long postId;        // null si signalement d'un user
    private String postTitle;   // null si signalement d'un user
    private String reason;
    private String description;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime reviewedAt;
    private String reviewedByUsername;

    public ReportResponse() {}

    public static ReportResponse from(Report report) {
        ReportResponse response = new ReportResponse();
        response.id                  = report.getId();
        response.reporterId          = report.getReporter().getId();
        response.reporterUsername    = report.getReporter().getUsername();
        response.reportedUserId      = report.getReportedUser().getId();
        response.reportedUsername    = report.getReportedUser().getUsername();
        response.postId              = report.getPostId();
        response.postTitle           = report.getPostTitle();
        response.reason              = report.getReason().name();
        response.description         = report.getDescription();
        response.status              = report.getStatus().name();
        response.createdAt           = report.getCreatedAt();
        response.reviewedAt          = report.getReviewedAt();
        if (report.getReviewedBy() != null) {
            response.reviewedByUsername = report.getReviewedBy().getUsername();
        }
        return response;
    }

    // Getters & Setters
    public Long getId()                          { return id; }
    public Long getReporterId()                  { return reporterId; }
    public String getReporterUsername()          { return reporterUsername; }
    public Long getReportedUserId()              { return reportedUserId; }
    public String getReportedUsername()          { return reportedUsername; }
    public Long getPostId()                      { return postId; }
    public String getPostTitle()                 { return postTitle; }
    public String getReason()                    { return reason; }
    public String getDescription()               { return description; }
    public String getStatus()                    { return status; }
    public LocalDateTime getCreatedAt()          { return createdAt; }
    public LocalDateTime getReviewedAt()         { return reviewedAt; }
    public String getReviewedByUsername()        { return reviewedByUsername; }

    public void setId(Long id)                              { this.id = id; }
    public void setReporterId(Long reporterId)               { this.reporterId = reporterId; }
    public void setReporterUsername(String v)                { this.reporterUsername = v; }
    public void setReportedUserId(Long v)                    { this.reportedUserId = v; }
    public void setReportedUsername(String v)                { this.reportedUsername = v; }
    public void setPostId(Long postId)                       { this.postId = postId; }
    public void setPostTitle(String postTitle)               { this.postTitle = postTitle; }
    public void setReason(String reason)                     { this.reason = reason; }
    public void setDescription(String description)           { this.description = description; }
    public void setStatus(String status)                     { this.status = status; }
    public void setCreatedAt(LocalDateTime createdAt)        { this.createdAt = createdAt; }
    public void setReviewedAt(LocalDateTime reviewedAt)      { this.reviewedAt = reviewedAt; }
    public void setReviewedByUsername(String v)              { this.reviewedByUsername = v; }
}