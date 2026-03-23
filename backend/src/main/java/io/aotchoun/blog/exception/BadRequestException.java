package io.aotchoun.blog.exception;

/**
 * Exception levée quand la requête est invalide
 * 
 * Exemples d'utilisation :
 * - Email déjà utilisé lors de l'inscription
 * - Username déjà pris
 * - Données de requête invalides
 * 
 * Code HTTP : 400 BAD REQUEST
 */
public class BadRequestException extends RuntimeException {

    /**
     * Constructeur avec message
     * 
     * @param message Le message d'erreur
     */
    public BadRequestException(String message) {
        super(message);
    }

    /**
     * Constructeur avec message et cause
     * 
     * @param message Le message d'erreur
     * @param cause La cause de l'erreur
     */
    public BadRequestException(String message, Throwable cause) {
        super(message, cause);
    }
}