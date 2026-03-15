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
                // Auth endpoints (public)
                .requestMatchers("/api/auth/**").permitAll()
                
                // WebSocket endpoints (public)
                .requestMatchers("/ws/**").permitAll()
                
                // Static resources (public)
                .requestMatchers("/", "/index.html").permitAll()
                .requestMatchers("/uploads/**").permitAll()
                
                // Posts endpoints
                .requestMatchers(HttpMethod.GET, "/api/posts/**").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/posts").authenticated()
                .requestMatchers(HttpMethod.PUT, "/api/posts/**").authenticated()
                .requestMatchers(HttpMethod.DELETE, "/api/posts/**").authenticated()
                
                // Comments endpoints
                .requestMatchers(HttpMethod.GET, "/api/posts/*/comments").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/posts/*/comments").authenticated()
                .requestMatchers(HttpMethod.DELETE, "/api/posts/*/comments/*").authenticated()
                
                // Likes endpoints
                .requestMatchers(HttpMethod.POST, "/api/posts/*/like").authenticated()
                
                // User endpoints (public stats, authenticated follow)
                .requestMatchers(HttpMethod.GET, "/api/users/*/stats").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/users/*/posts").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/users/*/follow").authenticated()
                
                // Notifications endpoints (authenticated only)
                .requestMatchers("/api/notifications/**").authenticated()
                
                // Reports endpoints
                .requestMatchers(HttpMethod.POST, "/api/reports/user/*").authenticated()
                .requestMatchers("/api/reports/**").hasRole("ADMIN")
                
                // Admin endpoints (tous protégés par @PreAuthorize dans le controller)
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                
                // Everything else requires authentication
                .anyRequest().authenticated()
            )
            .authenticationProvider(authenticationProvider())
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        
        // Disable frame options for WebSocket (SockJS)
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