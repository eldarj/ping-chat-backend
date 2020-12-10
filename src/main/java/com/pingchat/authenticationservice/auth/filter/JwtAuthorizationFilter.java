package com.pingchat.authenticationservice.auth.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pingchat.authenticationservice.auth.manager.AuthorizationManager;
import com.pingchat.authenticationservice.auth.util.AuthenticationHolder;
import com.pingchat.authenticationservice.data.mysql.entity.UserEntity;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import java.util.Set;

@Slf4j
public class JwtAuthorizationFilter extends OncePerRequestFilter {
    private final AuthorizationManager authorizationManager;
    private final ObjectMapper objectMapper;

    private final Set<String> publicEndpoints;
    private final String staticFilesEndpoint;

    public JwtAuthorizationFilter(ObjectMapper objectMapper,
                                  AuthorizationManager authorizationManager,
                                  Set<String> publicEndpoints,
                                  String staticFilesEndpoint) {
        this.objectMapper = objectMapper;
        this.authorizationManager = authorizationManager;
        this.publicEndpoints = publicEndpoints;
        this.staticFilesEndpoint = staticFilesEndpoint;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String servletPath = request.getServletPath();
        return publicEndpoints.contains(servletPath) || servletPath.startsWith(staticFilesEndpoint);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest httpRequest,
                                    HttpServletResponse httpResponse,
                                    FilterChain chain) throws ServletException, IOException {
        String token = httpRequest.getHeader("Authorization");

        if (!StringUtils.isBlank(token) && token.startsWith("Bearer ")) {
            String userPhoneNumber = authorizationManager.authorizeByToken(token.replace("Bearer ", ""));
            if (userPhoneNumber != null) {
                SecurityContextHolder.getContext().setAuthentication(new AuthenticationHolder(userPhoneNumber));
                chain.doFilter(httpRequest, httpResponse);
                return;
            }


            log.warn("Bearer token invalid or expired.");
        } else {
            log.warn("Request doesn't contain Bearer token.");
        }

        httpResponse.setStatus(401);
        httpResponse.getWriter().write(objectMapper.writeValueAsString(Map.of(
                "error", "Access denied, unauthorized.")
        ));
    }
}
