package com.dev.foryourwishes.security;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@Getter
public class AuthUser implements OAuth2AuthenticatedPrincipal {
    private final Long id;
    private final String login;
    private final Map<String, Object> attributes;

    public AuthUser(Long id, String login) {
        this(id, login, Map.of());
    }

    public AuthUser(Long id, String login, Map<String, Object> attributes) {
        this.id = id;
        this.login = login;
        this.attributes = Map.copyOf(attributes);
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public String getName() {
        return login;
    }
}
