package io.aotchoun.blog.exception;

/**
 * Exception levée quand l'authentification échoue
 * 
 * Exemples d'utilisation :
 * - Mot de passe incorrect
 * - Token JWT invalide
 * - Token expiré
 * 
 * Code HTTP : 401 UNAUTHORIZED
 */
public class UnauthorizedException extends RuntimeException {

    /**
     * Constructeur avec message
     * 
     * @param message Le message d'erreur
     */
    public UnauthorizedException(String message) {
        super(message);
    }

    /**
     * Constructeur avec message et cause
     * 
     * @param message Le message d'erreur
     * @param cause La cause de l'erreur
     */
    public UnauthorizedException(String message, Throwable cause) {
        super(message, cause);
    }
}