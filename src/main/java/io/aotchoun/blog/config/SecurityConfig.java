package io.aotchoun.blog.config;

import io.aotchoun.blog.security.JwtAuthFilter;
import io.aotchoun.blog.security.UserDetailsServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Configuration Spring Security avec JWT
 *
 * Définit :
 * - Quelles routes sont publiques (pas besoin de token)
 * - Quelles routes sont protégées (token obligatoire)
 * - Comment vérifier les credentials (BCrypt + DB)
 * - Que l'app est STATELESS (pas de sessions HTTP, on utilise JWT)
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;
    private final UserDetailsServiceImpl userDetailsService;

    public SecurityConfig(JwtAuthFilter jwtAuthFilter, UserDetailsServiceImpl userDetailsService) {
        this.jwtAuthFilter = jwtAuthFilter;
        this.userDetailsService = userDetailsService;
    }

    /**
     * Chaîne de filtres de sécurité
     *
     * C'est ici qu'on définit les règles d'accès.
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // Désactive CSRF (inutile avec JWT stateless)
            .csrf(csrf -> csrf.disable())

            // Règles d'autorisation par route
            .authorizeHttpRequests(auth -> auth

                // Routes publiques (pas besoin de token)
                .requestMatchers("/api/auth/**").permitAll()    // login, register, health
                .requestMatchers("/", "/index.html").permitAll() // frontend

                // Routes des posts : lecture publique, écriture protégée
                .requestMatchers(HttpMethod.GET, "/api/posts/**").permitAll()

                // Toutes les autres routes nécessitent un token valide
                .anyRequest().authenticated()
            )

            // STATELESS : pas de sessions HTTP, chaque requête doit avoir son token
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )

            // Fournisseur d'authentification (vérifie username/password)
            .authenticationProvider(authenticationProvider())

            // Ajoute notre filtre JWT avant le filtre d'auth standard de Spring
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * Fournisseur d'authentification
     *
     * Connecte Spring Security à notre UserDetailsService et PasswordEncoder.
     * C'est lui qui est appelé quand on vérifie un login/password.
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    /**
     * AuthenticationManager
     *
     * Permet d'injecter le manager dans d'autres services si besoin.
     */
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config
    ) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * Encodeur de mots de passe BCrypt
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}