package io.aotchoun.blog.security;

import io.aotchoun.blog.entity.User;
import io.aotchoun.blog.repository.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Implémentation de UserDetailsService pour Spring Security
 *
 * Spring Security ne sait pas comment charger un utilisateur depuis
 * notre base de données. Cette classe lui explique comment faire.
 *
 * ANALOGIE :
 * Spring Security est un vigile qui veut vérifier une carte d'identité.
 * Il sait vérifier une carte, mais il ne sait pas où chercher dans nos fichiers.
 * UserDetailsService, c'est l'assistant qui dit :
 * "Attends, je vais chercher dans la base de données pour toi."
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Charge un utilisateur par son username (appelé par Spring Security)
     *
     * @param username Le username à chercher
     * @return Un UserDetails (objet Spring Security représentant l'utilisateur)
     * @throws UsernameNotFoundException si l'utilisateur n'existe pas
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "User not found with username: " + username
                ));

        // On convertit notre User en UserDetails que Spring Security comprend
        // Le rôle doit être préfixé par "ROLE_" (convention Spring Security)
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()))
        );
    }
}