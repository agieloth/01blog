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
 * Charge l'utilisateur depuis la BDD et vérifie qu'il n'est pas banni.
 * Un utilisateur banni ne peut pas s'authentifier même avec un token JWT valide.
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
     * FIX : vérifie maintenant que l'utilisateur n'est pas banni.
     * Un utilisateur banni reçoit un compte désactivé (enabled=false),
     * ce qui provoque un DisabledException → 401 côté client.
     *
     * @param username Le username à chercher
     * @return Un UserDetails avec enabled=false si banni
     * @throws UsernameNotFoundException si l'utilisateur n'existe pas
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "User not found with username: " + username
                ));

        // FIX : un utilisateur banni ne peut pas se connecter
        boolean isEnabled = !user.getIsBanned();

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                isEnabled,           // enabled  — false si banni
                true,                // accountNonExpired
                true,                // credentialsNonExpired
                true,                // accountNonLocked
                List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()))
        );
    }
}