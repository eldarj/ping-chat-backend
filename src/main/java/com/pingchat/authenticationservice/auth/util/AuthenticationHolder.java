package com.pingchat.authenticationservice.auth.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.Collections;

public class AuthenticationHolder implements Authentication {
    private final String phoneNumber;

    public AuthenticationHolder(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @Override
    public Object getDetails() {
        return phoneNumber;
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
        // always be authenticated
        // if this object doesn't exist in a given request, it means that request is NOT authenticated
    }
}
