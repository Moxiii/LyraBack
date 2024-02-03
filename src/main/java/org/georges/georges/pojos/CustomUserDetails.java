package org.georges.georges.pojos;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

public class CustomUserDetails implements UserDetails {
    private final String username;
    private final String password;

    public User getUser() {
        return user;
    }

    private final User user;
    public CustomUserDetails(String username, String password , User user) {
        this.username = username;
        this.password = password;
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Retournez les rôles/granted authorities de l'utilisateur s'il y en a
        // Exemple fictif : return Arrays.asList(new SimpleGrantedAuthority("ROLE_USER"));
        return null;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // Mettez à true si le compte n'expire jamais
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // Mettez à true si le compte n'est jamais verrouillé
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // Mettez à true si les informations d'identification ne expirent jamais
    }

    @Override
    public boolean isEnabled() {
        return true; // Mettez à true si le compte est activé
    }
}
