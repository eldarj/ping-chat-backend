package com.pingchat.authenticationservice.auth.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.Collections;

public class AuthenticationHolder implements Authentication {
    private final String phoneNumber;

    private final Long userId;

    public AuthenticationHolder(String phoneNumber, Long userId) {
        this.phoneNumber = phoneNumber;
        this.userId = userId;
    }

    @Override
    public Object getDetails() {
        return userId;
    }

    @Override
    public Object getPrincipal() {
        return phoneNumber;
    }

    @Override
    public String getName() {
        return phoneNumber;
    }

    @Override
    public boolean isAuthenticated() {
        return true;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.emptyList();
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public void setAuthenticated(boolean b) throws IllegalArgumentException {
        // See isAuthenticated() - which should always return true
        // if the AuthenticationHolder object doesn't exist in a given request,
        // it means that request is NOT authenticated
    }
}
