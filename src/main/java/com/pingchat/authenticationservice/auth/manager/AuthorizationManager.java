package com.pingchat.authenticationservice.auth.manager;

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

    // TODO: Adjust this later, to return UserEntity and store in SecurityContextHolder, if needed
    public boolean authorizeByToken(String token) {
        return jwtTokenHandler.isValid(token);
    }
}
