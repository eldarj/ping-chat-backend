package com.pingchat.authenticationservice.auth.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pingchat.authenticationservice.auth.filter.JwtAuthenticationFilter;
import com.pingchat.authenticationservice.auth.filter.JwtAuthorizationFilter;
import com.pingchat.authenticationservice.auth.manager.AuthenticationManager;
import com.pingchat.authenticationservice.auth.manager.AuthorizationManager;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class SecurityFilterBeans {
    public static final String AUTHENTICATION_ENDPOINT = "/api/authenticate";
    public static final String COUNTRY_CODES_ENDPOINT = "/api/country-codes";
    public static final String STATIC_FILES_ENDPOINT = "/files";

    public static final Set<String> PUBLIC_ENDPOINTS = Set.of(
            AUTHENTICATION_ENDPOINT,
            COUNTRY_CODES_ENDPOINT
    );

    private final ObjectMapper objectMapper;
    private final AuthenticationManager authenticationManager;
    private final AuthorizationManager authorizationManager;

    public SecurityFilterBeans(ObjectMapper objectMapper,
                               AuthenticationManager authenticationManager,
                               AuthorizationManager authorizationManager) {
        this.objectMapper = objectMapper;
        this.authenticationManager = authenticationManager;
        this.authorizationManager = authorizationManager;
    }

    @Bean
    public FilterRegistrationBean<JwtAuthenticationFilter> authenticationFilter() {
        FilterRegistrationBean<JwtAuthenticationFilter> authenticationBean = new FilterRegistrationBean<>();

        authenticationBean.setFilter(new JwtAuthenticationFilter(objectMapper, authenticationManager));
        authenticationBean.addUrlPatterns(AUTHENTICATION_ENDPOINT);

        return authenticationBean;
    }

    @Bean
    public JwtAuthorizationFilter authorizationFilter() {
        return new JwtAuthorizationFilter(objectMapper, authorizationManager, PUBLIC_ENDPOINTS, STATIC_FILES_ENDPOINT);
    }
}
