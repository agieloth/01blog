package io.aotchoun.blog.controller;

import io.aotchoun.blog.dto.request.LoginRequest;
import io.aotchoun.blog.dto.request.RegisterRequest;
import io.aotchoun.blog.dto.response.AuthResponse;
import io.aotchoun.blog.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller d'authentification
 * 
 * Expose les endpoints REST pour l'inscription et la connexion
 * 
 * @RestController : Combine @Controller + @ResponseBody
 *                   Toutes les méthodes retournent du JSON automatiquement
 * @RequestMapping : Préfixe toutes les routes avec /api/auth
 * @CrossOrigin : Autorise les requêtes depuis le frontend (CORS)
 */
@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")  // Pour l'instant, autorise toutes les origines
public class AuthController {

    private final AuthService authService;

    /**
     * Injection de dépendances par constructeur
     * 
     * Spring injecte automatiquement AuthService
     */
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * Endpoint d'inscription
     * 
     * POST /api/auth/register
     * 
     * Body JSON :
     * {
     *   "username": "johndoe",
     *   "email": "john@example.com",
     *   "password": "password123"
     * }
     * 
     * Réponse (201 CREATED) :
     * {
     *   "token": "eyJhbGciOiJIUzI1NiJ9...",
     *   "type": "Bearer",
     *   "id": 1,
     *   "username": "johndoe",
     *   "email": "john@example.com",
     *   "role": "USER"
     * }
     * 
     * @Valid : Valide automatiquement le DTO avec les annotations
     *          (@NotBlank, @Email, etc.)
     * @RequestBody : Indique que les données viennent du body de la requête
     */
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(
            @Valid @RequestBody RegisterRequest request
    ) {
        AuthResponse response = authService.register(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * Endpoint de connexion
     * 
     * POST /api/auth/login
     * 
     * Body JSON :
     * {
     *   "identifier": "john@example.com",  // ou "johndoe"
     *   "password": "password123"
     * }
     * 
     * Réponse (200 OK) :
     * {
     *   "token": "eyJhbGciOiJIUzI1NiJ9...",
     *   "type": "Bearer",
     *   "id": 1,
     *   "username": "johndoe",
     *   "email": "john@example.com",
     *   "role": "USER"
     * }
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @Valid @RequestBody LoginRequest request
    ) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint pour tester si l'API fonctionne
     * 
     * GET /api/auth/health
     * 
     * Réponse :
     * "Auth API is running!"
     */
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Auth API is running!");
    }
}