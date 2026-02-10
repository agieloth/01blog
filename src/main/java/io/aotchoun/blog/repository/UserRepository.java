package io.aotchoun.blog.repository;

import io.aotchoun.blog.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

/**
 * Repository pour l'entité User
 * 
 * POURQUOI UNE INTERFACE ET PAS UNE CLASSE ?
 * Spring Data JPA génère automatiquement l'implémentation à l'exécution !
 * Tu n'as pas besoin d'écrire le code SQL, Spring le fait pour toi.
 * 
 * ANALOGIE :
 * En Rust/Go, tu écrirais toutes les fonctions SQL manuellement.
 * Avec Spring Data, tu déclares juste ce que tu veux, Spring génère le code.
 * 
 * JpaRepository<User, Long> signifie :
 * - User : Type de l'entité
 * - Long : Type de la clé primaire (l'ID)
 * 
 * MÉTHODES HÉRITÉES AUTOMATIQUEMENT :
 * - save(user) : Sauvegarde ou met à jour un user
 * - findById(id) : Trouve un user par son ID
 * - findAll() : Récupère tous les users
 * - deleteById(id) : Supprime un user par son ID
 * - count() : Compte le nombre de users
 * - existsById(id) : Vérifie si un user existe
 * ... et plein d'autres !
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Trouve un utilisateur par son email
     * 
     * MAGIE SPRING DATA :
     * Le nom de la méthode suit une convention : findBy + NomDuChamp
     * Spring génère automatiquement la requête SQL :
     * SELECT * FROM users WHERE email = ?
     * 
     * Optional<User> : Peut contenir un User ou être vide (si non trouvé)
     * C'est comme Option<User> en Rust !
     */
    Optional<User> findByEmail(String email);

    /**
     * Trouve un utilisateur par son username
     * 
     * SQL généré : SELECT * FROM users WHERE username = ?
     */
    Optional<User> findByUsername(String username);

    /**
     * Vérifie si un email existe déjà
     * 
     * SQL généré : SELECT COUNT(*) > 0 FROM users WHERE email = ?
     * Retourne true si l'email existe, false sinon
     */
    Boolean existsByEmail(String email);

    /**
     * Vérifie si un username existe déjà
     * 
     * SQL généré : SELECT COUNT(*) > 0 FROM users WHERE username = ?
     */
    Boolean existsByUsername(String username);

    /**
     * Trouve un utilisateur par email OU username
     * 
     * CONVENTION : Or entre les champs
     * SQL généré : SELECT * FROM users WHERE email = ? OR username = ?
     */
    Optional<User> findByEmailOrUsername(String email, String username);

    // Tu peux ajouter d'autres méthodes custom ici
    // Exemples :
    // - findByRole(Role role) : Tous les users d'un certain rôle
    // - findByIsBanned(Boolean isBanned) : Tous les users bannis
    // - findByUsernameContaining(String keyword) : Recherche par username
    
    /**
     * REQUÊTES CUSTOM AVEC @Query :
     * Si tu veux écrire du SQL personnalisé, tu peux utiliser @Query
     * 
     * Exemple :
     * @Query("SELECT u FROM User u WHERE u.email = :email AND u.isBanned = false")
     * Optional<User> findActiveUserByEmail(@Param("email") String email);
     */
}