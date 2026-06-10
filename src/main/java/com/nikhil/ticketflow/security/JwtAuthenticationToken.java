package com.nikhil.ticketflow.security;

import lombok.Getter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.UUID;

@Getter
public class JwtAuthenticationToken extends AbstractAuthenticationToken {

    private final UUID userId;
    private final String userRole;
    public JwtAuthenticationToken(UUID userId, String userRole,Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.userId = userId;
        this.userRole = userRole;
        setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return userId;
    }

}
