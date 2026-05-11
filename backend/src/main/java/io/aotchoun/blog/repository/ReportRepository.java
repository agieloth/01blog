// package io.aotchoun.blog.repository;

// import io.aotchoun.blog.entity.Report;
// import org.springframework.data.jpa.repository.JpaRepository;
// import org.springframework.stereotype.Repository;

// import java.util.List;
// import java.util.Optional;

// @Repository
// public interface ReportRepository extends JpaRepository<Report, Long> {
    
//     List<Report> findAllByOrderByCreatedAtDesc();
    
//     List<Report> findByStatusOrderByCreatedAtDesc(Report.ReportStatus status);
    
//     Optional<Report> findByReporterIdAndReportedUserId(Long reporterId, Long reportedUserId);
    
//     boolean existsByReporterIdAndReportedUserId(Long reporterId, Long reportedUserId);
    
//     // Pour l'admin : compter combien de fois un user a été signalé
//     long countByReportedUserId(Long userId);
// }



package io.aotchoun.blog.repository;

import io.aotchoun.blog.entity.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {

    List<Report> findAllByOrderByCreatedAtDesc();

    List<Report> findByStatusOrderByCreatedAtDesc(Report.ReportStatus status);

    Optional<Report> findByReporterIdAndReportedUserId(Long reporterId, Long reportedUserId);

    // ← Utilisé pour le signalement user : un seul par couple (hors signalements de posts)
    boolean existsByReporterIdAndReportedUserIdAndPostIdIsNull(Long reporterId, Long reportedUserId);

    // Pour l'admin : nb de fois qu'un user a été signalé
    long countByReportedUserId(Long userId);
}