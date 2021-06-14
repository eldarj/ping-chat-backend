package com.pingchat.authenticationservice.auth.util;

import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityContextUserProvider {
    public static String currentPhoneNumber() {
        return (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    public static Long currentUserId() {
        return (Long) SecurityContextHolder.getContext().getAuthentication().getDetails();
    }
}
