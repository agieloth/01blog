package io.aotchoun.blog.security;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Filtre JWT — intercepte chaque requête HTTP
 *
 * FIX v2 :
 * - Gère ExpiredJwtException → 401 avec message clair
 * - Gère MalformedJwtException / SignatureException → 401
 * - Gère DisabledException (utilisateur banni) → 403
 * - Remplace les catch(Exception) silencieux par des réponses HTTP explicites
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

        final String authHeader = request.getHeader("Authorization");

        // Pas de header Authorization → continuer sans authentification
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        final String token = authHeader.substring(7);

        try {
            final String username = jwtUtil.extractUsername(token);

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                // FIX : vérifier que le compte est actif (non banni)
                if (!userDetails.isEnabled()) {
                    sendError(response, HttpStatus.FORBIDDEN, "Account is banned");
                    return;
                }

                if (jwtUtil.validateToken(token, userDetails.getUsername())) {
                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,
                                    userDetails.getAuthorities()
                            );
                    authToken.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request)
                    );
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }

        } catch (ExpiredJwtException e) {
            // FIX : token expiré → 401 explicite (pas 500)
            sendError(response, HttpStatus.UNAUTHORIZED, "Token expired");
            return;
        } catch (MalformedJwtException | SignatureException | UnsupportedJwtException e) {
            // FIX : token invalide / signature incorrecte → 401
            sendError(response, HttpStatus.UNAUTHORIZED, "Invalid token");
            return;
        } catch (DisabledException e) {
            // FIX : compte désactivé (banni) → 403
            sendError(response, HttpStatus.FORBIDDEN, "Account is banned");
            return;
        } catch (UsernameNotFoundException e) {
            // Utilisateur supprimé mais token encore valide → 401
            sendError(response, HttpStatus.UNAUTHORIZED, "User not found");
            return;
        } catch (Exception e) {
            // Toute autre erreur → 401 générique (ne pas exposer les détails)
            sendError(response, HttpStatus.UNAUTHORIZED, "Authentication failed");
            return;
        }

        filterChain.doFilter(request, response);
    }

    /**
     * Envoie une réponse d'erreur JSON structurée
     */
    private void sendError(HttpServletResponse response, HttpStatus status, String message)
            throws IOException {
        response.setStatus(status.value());
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(
                String.format("{\"status\":%d,\"error\":\"%s\",\"message\":\"%s\"}",
                        status.value(), status.getReasonPhrase(), message)
        );
    }
}