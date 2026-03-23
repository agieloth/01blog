package io.aotchoun.blog.repository;

import io.aotchoun.blog.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository pour les Posts
 *
 * Spring Data génère automatiquement les implémentations SQL.
 *
 * JpaRepository<Post, Long> :
 * - Post = l'entité gérée
 * - Long = le type de l'ID
 *
 * Méthodes héritées gratuitement :
 * findAll(), findById(), save(), deleteById(), count()...
 */
@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    /**
     * Récupère tous les posts d'un auteur, triés du plus récent au plus ancien
     * SQL généré : SELECT * FROM posts WHERE author_id = ? ORDER BY created_at DESC
     */
    List<Post> findByAuthorIdOrderByCreatedAtDesc(Long authorId);

    /**
     * Récupère tous les posts triés du plus récent au plus ancien
     * SQL généré : SELECT * FROM posts ORDER BY created_at DESC
     */
    List<Post> findAllByOrderByCreatedAtDesc();
    long countByAuthorId(Long authorId); 
    // long countByUserId(Long userId);
}