package com.pingchat.authenticationservice.auth.util;

import com.pingchat.authenticationservice.data.mysql.entity.UserEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityContextUserProvider {
    /**
     * @return This request's Authenticated user's dial code and phone number
     */
    public static String currentUserPrincipal() {
        return (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
