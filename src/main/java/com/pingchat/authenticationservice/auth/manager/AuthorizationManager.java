package com.pingchat.authenticationservice.auth.manager;

import com.pingchat.authenticationservice.auth.util.JwtTokenHandler;
import com.pingchat.authenticationservice.data.mysql.entity.UserEntity;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AuthorizationManager {
    private final JwtTokenHandler jwtTokenHandler;

    public AuthorizationManager(JwtTokenHandler jwtTokenHandler) {
        this.jwtTokenHandler = jwtTokenHandler;
    }

    public String authorizeByToken(String token) {
        return jwtTokenHandler.getSubject(token);
    }
}
