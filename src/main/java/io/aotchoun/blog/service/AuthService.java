package io.aotchoun.blog.service;

import io.aotchoun.blog.dto.request.LoginRequest;
import io.aotchoun.blog.dto.request.RegisterRequest;
import io.aotchoun.blog.dto.response.AuthResponse;
import io.aotchoun.blog.entity.Role;
import io.aotchoun.blog.entity.User;
import io.aotchoun.blog.exception.BadRequestException;
import io.aotchoun.blog.exception.UnauthorizedException;
import io.aotchoun.blog.repository.UserRepository;
import io.aotchoun.blog.security.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service d'authentification
 * 
 * Contient toute la logique pour :
 * - Inscrire un nouvel utilisateur
 * - Connecter un utilisateur
 * 
 * @Service : Indique à Spring que c'est un service (couche métier)
 * @Transactional : Les méthodes s'exécutent dans une transaction DB
 */
@Service
@Transactional
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    /**
     * Constructeur avec injection de dépendances
     * 
     * Spring va automatiquement injecter ces dépendances
     * C'est comme si Spring disait :
     * "Tu as besoin d'un UserRepository ? Tiens, je t'en donne un !"
     */
    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtUtil jwtUtil
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    /**
     * Inscrit un nouvel utilisateur
     * 
     * @param request Les données d'inscription (username, email, password)
     * @return AuthResponse avec le token JWT
     * @throws BadRequestException si email ou username déjà utilisé
     */
    public AuthResponse register(RegisterRequest request) {
        // 1. Vérifier que l'email n'est pas déjà utilisé
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email already in use");
        }

        // 2. Vérifier que le username n'est pas déjà pris
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new BadRequestException("Username already taken");
        }

        // 3. Hasher le mot de passe
        // IMPORTANT : On ne stocke JAMAIS le mot de passe en clair !
        // BCrypt va générer un hash différent à chaque fois
        String hashedPassword = passwordEncoder.encode(request.getPassword());

        // 4. Créer le nouvel utilisateur
        User user = new User(
                request.getUsername(),
                request.getEmail(),
                hashedPassword,
                Role.USER  // Par défaut, nouveau user = USER (pas ADMIN)
        );

        // 5. Sauvegarder dans la DB
        user = userRepository.save(user);

        // 6. Générer le token JWT
        String token = jwtUtil.generateToken(user.getUsername());

        // 7. Retourner la réponse avec le token et les infos de l'utilisateur
        return new AuthResponse(
                token,
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRole()
        );
    }

    /**
     * Connecte un utilisateur
     * 
     * @param request Les données de connexion (identifier, password)
     * @return AuthResponse avec le token JWT
     * @throws UnauthorizedException si les identifiants sont incorrects
     */
    public AuthResponse login(LoginRequest request) {
        // 1. Chercher l'utilisateur par email OU username
        User user = userRepository.findByEmailOrUsername(
                request.getIdentifier(),
                request.getIdentifier()
        ).orElseThrow(() -> new UnauthorizedException("Invalid credentials"));

        // 2. Vérifier que l'utilisateur n'est pas banni
        if (user.getIsBanned()) {
            throw new UnauthorizedException("Your account has been banned");
        }

        // 3. Vérifier le mot de passe
        // passwordEncoder.matches() compare le password en clair avec le hash
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new UnauthorizedException("Invalid credentials");
        }

        // 4. Générer le token JWT
        String token = jwtUtil.generateToken(user.getUsername());

        // 5. Retourner la réponse
        return new AuthResponse(
                token,
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRole()
        );
    }

    /**
     * EXPLICATION - Pourquoi hasher les mots de passe ?
     * 
     * Si quelqu'un vole la base de données, il ne doit PAS pouvoir
     * lire les mots de passe des utilisateurs.
     * 
     * BCrypt est un algorithme de hashage :
     * - Unidirectionnel : impossible de retrouver le mot de passe depuis le hash
     * - Lent exprès : rend les attaques par force brute très difficiles
     * - Aléatoire : même mot de passe = hash différent à chaque fois
     * 
     * Exemple :
     * password123 → $2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy
     * password123 → $2a$10$UgHCn3N4w1234567890abcdefghijklmnopqrstuvwxyzABCDEFG
     *                (hash différent !)
     * 
     * Pour vérifier un mot de passe, on utilise passwordEncoder.matches()
     * qui compare le hash stocké avec le mot de passe fourni.
     */
}