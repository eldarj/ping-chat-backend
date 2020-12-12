package com.pingchat.authenticationservice.auth.ws;

import com.pingchat.authenticationservice.auth.util.JwtTokenHandler;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class WebSocketHandshakeHandler extends DefaultHandshakeHandler {
    private final JwtTokenHandler jwtTokenHandler;

    public WebSocketHandshakeHandler(JwtTokenHandler jwtTokenHandler) {
        this.jwtTokenHandler = jwtTokenHandler;
    }

    @Override
    protected Principal determineUser(ServerHttpRequest request,
                                      WebSocketHandler wsHandler,
                                      Map<String, Object> attributes) {
        StompPrincipal stompPrincipal = new StompPrincipal("anonymous");
        try {
            List<String> authorization = request.getHeaders().get("Authorization");
            if (authorization != null) {
                String token = authorization.get(0);

                if (!StringUtils.isBlank(token)) {
                    Jws<Claims> parsedToken = jwtTokenHandler.parse(token);
                    stompPrincipal.setName(parsedToken.getBody().getSubject());
                }
            }
        } catch (Exception e) {
            log.warn("Could not determine user during socket connection.", e);
        }

        log.info("Setting stomp principal for inbound socket connection: {}.", stompPrincipal);
        return stompPrincipal;
    }
}
