package io.aotchoun.blog.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Filtre JWT - Intercepte chaque requête HTTP
 *
 * OncePerRequestFilter garantit que ce filtre s'exécute
 * exactement une fois par requête (même si Spring le détecte plusieurs fois).
 *
 * FLOW pour chaque requête :
 * 1. Lit le header "Authorization"
 * 2. Extrait le token JWT (après "Bearer ")
 * 3. Valide le token
 * 4. Charge l'utilisateur depuis la DB
 * 5. Définit l'authentification dans le SecurityContext
 *
 * Si une étape échoue → la requête continue sans authentification
 * → Spring Security bloquera les routes protégées (401)
 */
@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserDetailsServiceImpl userDetailsService;

    public JwtAuthFilter(JwtUtil jwtUtil, UserDetailsServiceImpl userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        // 1. Lire le header Authorization
        // Format attendu : "Bearer eyJhbGciOiJIUzI1NiJ9..."
        final String authHeader = request.getHeader("Authorization");

        // 2. Si pas de header ou pas le bon format → passer au filtre suivant
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 3. Extraire le token (enlever "Bearer ")
        final String token = authHeader.substring(7);

        // 4. Extraire le username du token
        final String username = jwtUtil.extractUsername(token);

        // 5. Si username trouvé ET pas encore authentifié dans ce contexte
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            // 6. Charger l'utilisateur depuis la DB
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            // 7. Valider le token
            if (jwtUtil.validateToken(token, userDetails.getUsername())) {

                // 8. Créer l'objet d'authentification Spring Security
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,                        // pas besoin des credentials
                                userDetails.getAuthorities() // les rôles (ROLE_USER, ROLE_ADMIN)
                        );

                // 9. Ajouter les détails de la requête (IP, session, etc.)
                authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );

                // 10. Enregistrer l'authentification dans le contexte de sécurité
                // Après ça, Spring Security sait que l'utilisateur est authentifié
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        // 11. Continuer la chaîne de filtres
        filterChain.doFilter(request, response);
    }
}