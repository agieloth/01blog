package io.aotchoun.blog.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Configuration temporaire de Spring Security
 * 
 * CETTE CONFIG EST TEMPORAIRE pour permettre à l'app de démarrer.
 * On va la remplacer par une vraie config sécurisée plus tard.
 * 
 * Pour l'instant, on désactive la sécurité sur toutes les routes.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * Configuration du filtre de sécurité
     * 
     * Pour l'instant : on autorise TOUTES les requêtes (permitAll)
     * C'est temporaire, juste pour tester que l'app démarre !
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())  // Désactive CSRF (on utilise JWT)
            .authorizeHttpRequests(auth -> auth
                .anyRequest().permitAll()  // TEMPORAIRE : Autorise tout
            );
        
        return http.build();
    }

    /**
     * Bean pour encoder les mots de passe
     * 
     * BCrypt est l'algorithme standard pour hasher les passwords
     * Il est lent exprès pour rendre les attaques par force brute difficiles
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}