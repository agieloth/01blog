package io.aotchoun.blog.repository;

import io.aotchoun.blog.entity.Follow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FollowRepository extends JpaRepository<Follow, Long> {
    
    // Vérifier si A suit B
    boolean existsByFollowerIdAndFollowedId(Long followerId, Long followedId);
    
    // Trouver la relation A → B
    Optional<Follow> findByFollowerIdAndFollowedId(Long followerId, Long followedId);
    
    // Liste des gens que je suis
    List<Follow> findByFollowerId(Long followerId);
    
    // Liste de mes abonnés
    List<Follow> findByFollowedId(Long followedId);
    
    // Compter combien de personnes suivent X
    long countByFollowedId(Long followedId);
    
    // Compter combien de personnes X suit
    long countByFollowerId(Long followerId);
}