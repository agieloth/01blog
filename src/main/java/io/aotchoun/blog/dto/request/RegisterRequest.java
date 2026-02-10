package io.aotchoun.blog.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO pour la requête d'inscription
 * 
 * DTO = Data Transfer Object
 * C'est l'objet qu'on reçoit du client lors de l'inscription.
 * 
 * POURQUOI UN DTO ET PAS DIRECTEMENT L'ENTITÉ USER ?
 * 1. Séparation des préoccupations : L'API != le modèle DB
 * 2. Validation : On valide les données avant de créer un User
 * 3. Sécurité : On ne reçoit que ce qu'on veut (pas de champ "role" ici)
 * 
 * ANNOTATIONS DE VALIDATION :
 * @NotBlank : Le champ ne peut pas être vide ou null
 * @Email : Doit être un email valide
 * @Size : Contraintes de taille (min/max)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {

    /**
     * Nom d'utilisateur
     * 
     * @NotBlank : Ne peut pas être vide
     * @Size : Entre 3 et 50 caractères
     */
    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    private String username;

    /**
     * Email
     * 
     * @Email : Doit être un format email valide (contient @, etc.)
     */
    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    @Size(max = 100, message = "Email must not exceed 100 characters")
    private String email;

    /**
     * Mot de passe
     * 
     * Minimum 6 caractères (tu peux augmenter pour plus de sécurité)
     */
    @NotBlank(message = "Password is required")
    @Size(min = 6, max = 100, message = "Password must be between 6 and 100 characters")
    private String password;

    /**
     * NOTE : On ne met PAS de champ "role" ici !
     * Pourquoi ? Pour éviter qu'un utilisateur s'inscrive directement en tant qu'ADMIN.
     * Le rôle sera toujours USER par défaut lors de l'inscription.
     * Seul un admin existant peut promouvoir un user en admin.
     */
}