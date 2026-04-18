// package io.aotchoun.blog.entity;

// import jakarta.persistence.*;
// import java.time.LocalDateTime;

// @Entity
// @Table(name = "reports")
// public class Report {

//     @Id
//     @GeneratedValue(strategy = GenerationType.IDENTITY)
//     private Long id;

//     @ManyToOne(fetch = FetchType.LAZY)
//     @JoinColumn(name = "reporter_id", nullable = false)
//     private User reporter; // Celui qui signale

//     @ManyToOne(fetch = FetchType.LAZY)
//     @JoinColumn(name = "reported_user_id", nullable = false)
//     private User reportedUser; // Celui qui est signalé

//     @Enumerated(EnumType.STRING)
//     @Column(nullable = false, length = 50)
//     private ReportReason reason;

//     @Column(length = 500)
//     private String description;

//     @Enumerated(EnumType.STRING)
//     @Column(nullable = false, length = 20)
//     private ReportStatus status = ReportStatus.PENDING;

//     @Column(nullable = false, updatable = false)
//     private LocalDateTime createdAt;

//     @Column
//     private LocalDateTime reviewedAt;

//     @ManyToOne(fetch = FetchType.LAZY)
//     @JoinColumn(name = "reviewed_by_id")
//     private User reviewedBy; // Admin qui a traité le report

//     public Report() {}

//     public Report(User reporter, User reportedUser, ReportReason reason, String description) {
//         this.reporter = reporter;
//         this.reportedUser = reportedUser;
//         this.reason = reason;
//         this.description = description;
//     }

//     @PrePersist
//     protected void onCreate() {
//         this.createdAt = LocalDateTime.now();
//     }

//     public enum ReportReason {
//         SPAM,
//         HARASSMENT,
//         INAPPROPRIATE_CONTENT,
//         HATE_SPEECH,
//         OTHER
//     }

//     public enum ReportStatus {
//         PENDING,
//         REVIEWED,
//         DISMISSED
//     }

//     // Getters & Setters
//     public Long getId() { return id; }
//     public User getReporter() { return reporter; }
//     public User getReportedUser() { return reportedUser; }
//     public ReportReason getReason() { return reason; }
//     public String getDescription() { return description; }
//     public ReportStatus getStatus() { return status; }
//     public LocalDateTime getCreatedAt() { return createdAt; }
//     public LocalDateTime getReviewedAt() { return reviewedAt; }
//     public User getReviewedBy() { return reviewedBy; }

//     public void setId(Long id) { this.id = id; }
//     public void setReporter(User reporter) { this.reporter = reporter; }
//     public void setReportedUser(User reportedUser) { this.reportedUser = reportedUser; }
//     public void setReason(ReportReason reason) { this.reason = reason; }
//     public void setDescription(String description) { this.description = description; }
//     public void setStatus(ReportStatus status) { this.status = status; }
//     public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
//     public void setReviewedAt(LocalDateTime reviewedAt) { this.reviewedAt = reviewedAt; }
//     public void setReviewedBy(User reviewedBy) { this.reviewedBy = reviewedBy; }
// }




package io.aotchoun.blog.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "reports")
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reporter_id", nullable = false)
    private User reporter;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reported_user_id", nullable = false)
    private User reportedUser;

    // ← NOUVEAU : lien optionnel vers un post signalé (null si signalement d'un user)
    @Column(name = "post_id")
    private Long postId;

    // ← NOUVEAU : titre du post au moment du signalement (dénormalisé pour éviter une jointure)
    @Column(name = "post_title", length = 200)
    private String postTitle;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private ReportReason reason;

    @Column(length = 500)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ReportStatus status = ReportStatus.PENDING;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column
    private LocalDateTime reviewedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewed_by_id")
    private User reviewedBy;

    public Report() {}

    // Constructeur pour signalement d'un user
    public Report(User reporter, User reportedUser, ReportReason reason, String description) {
        this.reporter = reporter;
        this.reportedUser = reportedUser;
        this.reason = reason;
        this.description = description;
    }

    // Constructeur pour signalement d'un post
    public Report(User reporter, User reportedUser, Long postId, String postTitle,
                  ReportReason reason, String description) {
        this.reporter = reporter;
        this.reportedUser = reportedUser;
        this.postId = postId;
        this.postTitle = postTitle;
        this.reason = reason;
        this.description = description;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public enum ReportReason {
        SPAM,
        HARASSMENT,
        INAPPROPRIATE_CONTENT,
        HATE_SPEECH,
        OTHER
    }

    public enum ReportStatus {
        PENDING,
        REVIEWED,
        DISMISSED
    }

    // Getters & Setters
    public Long getId()                    { return id; }
    public User getReporter()              { return reporter; }
    public User getReportedUser()          { return reportedUser; }
    public Long getPostId()                { return postId; }
    public String getPostTitle()           { return postTitle; }
    public ReportReason getReason()        { return reason; }
    public String getDescription()         { return description; }
    public ReportStatus getStatus()        { return status; }
    public LocalDateTime getCreatedAt()    { return createdAt; }
    public LocalDateTime getReviewedAt()   { return reviewedAt; }
    public User getReviewedBy()            { return reviewedBy; }

    public void setId(Long id)                        { this.id = id; }
    public void setReporter(User reporter)             { this.reporter = reporter; }
    public void setReportedUser(User reportedUser)     { this.reportedUser = reportedUser; }
    public void setPostId(Long postId)                 { this.postId = postId; }
    public void setPostTitle(String postTitle)         { this.postTitle = postTitle; }
    public void setReason(ReportReason reason)         { this.reason = reason; }
    public void setDescription(String description)     { this.description = description; }
    public void setStatus(ReportStatus status)         { this.status = status; }
    public void setCreatedAt(LocalDateTime createdAt)  { this.createdAt = createdAt; }
    public void setReviewedAt(LocalDateTime reviewedAt){ this.reviewedAt = reviewedAt; }
    public void setReviewedBy(User reviewedBy)         { this.reviewedBy = reviewedBy; }
}