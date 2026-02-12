package io.aotchoun.blog.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Utilitaire pour gérer les tokens JWT (JSON Web Tokens)
 * 
 * JWT = JSON Web Token
 * C'est un token signé qui contient des informations (claims) sur l'utilisateur.
 * Format: header.payload.signature
 * 
 * ANALOGIE :
 * Imagine un badge d'entreprise avec ta photo et ton nom.
 * Le badge est signé par l'entreprise (signature).
 * Tout le monde peut lire le badge, mais seule l'entreprise peut le créer.
 * 
 * @Component : Indique à Spring que c'est un bean (composant réutilisable)
 */
@Component
public class JwtUtil {

    /**
     * Clé secrète pour signer les tokens
     * 
     * @Value : Injecte la valeur depuis application.properties
     * Cette clé NE DOIT JAMAIS être partagée publiquement !
     */
    @Value("${jwt.secret}")
    private String secret;

    /**
     * Durée de validité du token en millisecondes
     * 
     * Dans application.properties, on a mis 86400000 ms = 24 heures
     */
    @Value("${jwt.expiration}")
    private Long expiration;

    /**
     * Génère la clé de signature à partir du secret
     * 
     * HMAC-SHA : Algorithme de signature sécurisé
     */
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    /**
     * Génère un token JWT pour un utilisateur
     * 
     * @param username Le nom d'utilisateur
     * @return Le token JWT sous forme de String
     * 
     * EXEMPLE DE TOKEN :
     * eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJqb2huZG9lIiwiaWF0IjoxNjE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c
     */
    public String generateToken(String username) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, username);
    }

    /**
     * Crée le token avec les claims (informations)
     * 
     * @param claims Informations supplémentaires à inclure (vide pour l'instant)
     * @param subject L'utilisateur (username)
     * @return Le token signé
     */
    private String createToken(Map<String, Object> claims, String subject) {
        Date now = new Date();
        Date expirationDate = new Date(now.getTime() + expiration);

        return Jwts.builder()
                .claims(claims)              // Données supplémentaires
                .subject(subject)            // L'utilisateur (username)
                .issuedAt(now)               // Date de création
                .expiration(expirationDate)  // Date d'expiration
                .signWith(getSigningKey())    // Signature (HS256 par défaut avec SecretKey)
                .compact();
    }

    /**
     * Extrait le username du token
     * 
     * @param token Le token JWT
     * @return Le username
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extrait la date d'expiration du token
     * 
     * @param token Le token JWT
     * @return La date d'expiration
     */
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Extrait une information spécifique du token
     * 
     * @param token Le token JWT
     * @param claimsResolver Fonction pour extraire le claim voulu
     * @return L'information extraite
     * 
     * EXPLICATION :
     * Les "claims" sont les informations contenues dans le token
     * (username, date d'expiration, etc.)
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Extrait tous les claims du token
     * 
     * @param token Le token JWT
     * @return Tous les claims
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * Vérifie si le token est expiré
     * 
     * @param token Le token JWT
     * @return true si expiré, false sinon
     */
    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * Valide un token
     * 
     * @param token Le token JWT
     * @param username Le username attendu
     * @return true si le token est valide, false sinon
     * 
     * Un token est valide si :
     * 1. Le username dans le token correspond à celui attendu
     * 2. Le token n'est pas expiré
     */
    public Boolean validateToken(String token, String username) {
        final String extractedUsername = extractUsername(token);
        return (extractedUsername.equals(username) && !isTokenExpired(token));
    }

    /**
     * Valide un token (version simplifiée)
     * 
     * @param token Le token JWT
     * @return true si le token est valide, false sinon
     */
    public Boolean validateToken(String token) {
        try {
            Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token);
            return !isTokenExpired(token);
        } catch (Exception e) {
            return false;
        }
    }
}