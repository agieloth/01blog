package io.aotchoun.blog.config;

import io.aotchoun.blog.security.JwtAuthFilter;
import io.aotchoun.blog.security.UserDetailsServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    private final UserDetailsServiceImpl userDetailsService;
    private final JwtAuthFilter jwtAuthFilter;

    public SecurityConfig(UserDetailsServiceImpl userDetailsService, JwtAuthFilter jwtAuthFilter) {
        this.userDetailsService = userDetailsService;
        this.jwtAuthFilter = jwtAuthFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                // Auth (public)
                .requestMatchers("/api/auth/**").permitAll()

                // WebSocket (public)
                .requestMatchers("/ws/**").permitAll()

                // Static resources (public)
                .requestMatchers("/", "/index.html").permitAll()
                .requestMatchers("/uploads/**").permitAll()

                // Posts
                .requestMatchers(HttpMethod.GET,    "/api/posts/**").permitAll()
                .requestMatchers(HttpMethod.POST,   "/api/posts").authenticated()
                .requestMatchers(HttpMethod.PUT,    "/api/posts/**").authenticated()
                .requestMatchers(HttpMethod.DELETE, "/api/posts/**").authenticated()

                // Comments
                .requestMatchers(HttpMethod.GET,    "/api/posts/*/comments").permitAll()
                .requestMatchers(HttpMethod.POST,   "/api/posts/*/comments").authenticated()
                .requestMatchers(HttpMethod.DELETE, "/api/posts/*/comments/*").authenticated()

                // Likes
                .requestMatchers(HttpMethod.POST, "/api/posts/*/like").authenticated()

                // Users
                .requestMatchers(HttpMethod.GET,  "/api/users/*/stats").permitAll()
                .requestMatchers(HttpMethod.GET,  "/api/users/*/posts").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/users/*/follow").authenticated()

                // Notifications
                .requestMatchers("/api/notifications/**").authenticated()

                // Reports — signalement de post ET de user : authentifié
                .requestMatchers(HttpMethod.POST, "/api/reports/post/*").authenticated()
                .requestMatchers(HttpMethod.POST, "/api/reports/user/*").authenticated()
                // Lecture et gestion des reports : admin seulement
                .requestMatchers("/api/reports/**").hasRole("ADMIN")

                // Admin
                .requestMatchers("/api/admin/**").hasRole("ADMIN")

                .anyRequest().authenticated()
            )
            .authenticationProvider(authenticationProvider())
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        http.headers(headers -> headers.frameOptions(frameOptions -> frameOptions.disable()));

        return http.build();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}