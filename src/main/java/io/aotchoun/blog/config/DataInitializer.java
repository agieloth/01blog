package io.aotchoun.blog.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import io.aotchoun.blog.entity.User;
import io.aotchoun.blog.repository.UserRepository;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initDatabase(UserRepository userRepository, 
                                   PasswordEncoder passwordEncoder) {
        return args -> {
            // Créer un utilisateur de test s'il n'existe pas
            if (!userRepository.findByUsername("test").isPresent()) {
                User testUser = new User();
                testUser.setUsername("test");
                testUser.setEmail("test@example.com");
                testUser.setPassword(passwordEncoder.encode("test123"));
                
                userRepository.save(testUser);
                System.out.println("✅ User créé : test / test123");
            }

            // Créer un admin s'il n'existe pas
            if (!userRepository.findByUsername("admin").isPresent()) {
                User adminUser = new User();
                adminUser.setUsername("admin");
                adminUser.setEmail("admin@example.com");
                adminUser.setPassword(passwordEncoder.encode("admin123"));
                
                userRepository.save(adminUser);
                System.out.println("✅ Admin créé : admin / admin123");
            }
        };
    }
}