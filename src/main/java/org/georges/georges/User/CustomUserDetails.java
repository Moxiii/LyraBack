package org.georges.georges.User;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_USER")); // Exemple de rôle
        return authorities;
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
