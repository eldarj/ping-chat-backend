package com.pingchat.authenticationservice.auth.manager;

import com.pingchat.authenticationservice.auth.util.AuthenticationHolder;
import com.pingchat.authenticationservice.auth.util.JwtTokenHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AuthorizationManager {
    private final JwtTokenHandler jwtTokenHandler;

    public AuthorizationManager(JwtTokenHandler jwtTokenHandler) {
        this.jwtTokenHandler = jwtTokenHandler;
    }

    public AuthenticationHolder authorizeByToken(String token) {
        return new AuthenticationHolder(
                jwtTokenHandler.getPhoneNumber(token),
                jwtTokenHandler.getUserId(token)
        );
    }
}
