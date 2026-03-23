package io.aotchoun.blog.exception;

/**
 * Exception levée quand une ressource n'existe pas
 *
 * Exemples : post introuvable, user introuvable
 * Code HTTP : 404 NOT FOUND
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }
}