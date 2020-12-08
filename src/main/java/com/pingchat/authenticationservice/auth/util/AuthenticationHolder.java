package com.pingchat.authenticationservice.auth.util;

import com.pingchat.authenticationservice.data.mysql.entity.UserEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.Collections;

public class AuthenticationHolder implements Authentication {
    private final UserEntity userEntity;

    public AuthenticationHolder(UserEntity userEntity) {
        this.userEntity = userEntity;
    }

    @Override
    public Object getDetails() {
        return userEntity;
    }

    @Override
    public Object getPrincipal() {
        return this.userEntity.getCountryCode().getDialCode() + this.userEntity.getPhoneNumber();
    }

    @Override
    public String getName() {
        return this.userEntity.getFirstName() + " " + this.userEntity.getLastName();
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
