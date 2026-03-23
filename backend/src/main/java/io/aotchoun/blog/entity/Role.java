package io.aotchoun.blog.entity;

/**
 * Énumération des rôles utilisateur
 * 
 * En Java, un enum est comme un type avec des valeurs fixes.
 * Analogie Rust : enum Role { User, Admin }
 * 
 * USER : utilisateur normal (peut créer des posts, commenter, etc.)
 * ADMIN : administrateur (peut modérer, bannir des users, etc.)
 */
public enum Role {
    /**
     * Utilisateur normal
     */
    USER,
    
    /**
     * Administrateur avec privilèges étendus
     */
    ADMIN
}