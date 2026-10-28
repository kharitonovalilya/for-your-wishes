package com.dev.foryourwishes.security;

import org.jspecify.annotations.Nullable;
import org.springframework.security.core.CredentialsContainer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public final class DatabaseUserDetails extends AuthUser implements UserDetails, CredentialsContainer {
    private String passwordHash;

    public DatabaseUserDetails(Long id, String login, String passwordHash) {
        super(id, login);
        this.passwordHash = passwordHash;
    }

    @Override
    public void eraseCredentials() {
        passwordHash = null;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public @Nullable String getPassword() {
        return passwordHash;
    }

    @Override
    public String getUsername() {
        return getLogin();
    }
}
