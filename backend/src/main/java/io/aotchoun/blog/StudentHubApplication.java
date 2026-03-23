package io.aotchoun.blog;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Classe principale de l'application StudentHub
 * 
 * @SpringBootApplication est une annotation composite qui combine:
 * - @Configuration: indique que cette classe contient des beans Spring
 * - @EnableAutoConfiguration: active la configuration automatique de Spring Boot
 * - @ComponentScan: scanne le package et ses sous-packages pour les composants Spring
 */
@SpringBootApplication
public class StudentHubApplication {

    /**
     * Point d'entrée de l'application
     * 
     * @param args arguments de ligne de commande
     */
    public static void main(String[] args) {
        SpringApplication.run(StudentHubApplication.class, args);
        System.out.println("\n==============================================");
        System.out.println("🚀 StudentHub is running!");
        System.out.println("📍 API available at: http://localhost:8080");
        System.out.println("==============================================\n");
    }
}