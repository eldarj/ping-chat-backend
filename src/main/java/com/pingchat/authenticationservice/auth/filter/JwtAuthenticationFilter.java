package com.pingchat.authenticationservice.auth.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pingchat.authenticationservice.auth.manager.AuthenticationManager;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

/**
 * Authenticates users on API endpoint: /api/authenticate
 */
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private static final String DIAL_CODE_HEADER_KEY = "dialCode";
    private static final String TWO_WAY_PIN_HEADER_KEY = "pinCode";
    private static final String PHONE_NUMBER_HEADER_KEY = "phoneNumber";

    private final ObjectMapper objectMapper;
    private final AuthenticationManager authenticationManager;

    public JwtAuthenticationFilter(ObjectMapper objectMapper, AuthenticationManager authenticationManager) {
        this.objectMapper = objectMapper;
        this.authenticationManager = authenticationManager;
    }

    @Override
    public void doFilterInternal(HttpServletRequest httpRequest,
                                 HttpServletResponse httpResponse,
                                 FilterChain chain) throws IOException {
        log.info("Authenticating: {}", httpRequest);

        String dialCode = httpRequest.getHeader(DIAL_CODE_HEADER_KEY);
        String phoneNumber = httpRequest.getHeader(PHONE_NUMBER_HEADER_KEY);
        String twoWayPin = httpRequest.getHeader(TWO_WAY_PIN_HEADER_KEY);

        if (StringUtils.isNoneBlank(dialCode, phoneNumber)) {
            try {
                Object responseObject;
                boolean isTwoWayPinVerified = false;

                if (StringUtils.isBlank(twoWayPin)) {
                    responseObject = authenticationManager.authenticateByPhoneNumber(phoneNumber, dialCode);
                } else {
                    Map<String, Object> jwt = authenticationManager.authenticateByTwoWayPin(
                            phoneNumber, dialCode, Integer.parseInt(twoWayPin));
                    responseObject = jwt.get("user");
                    isTwoWayPinVerified = true;

                    httpResponse.addHeader("Access-Control-Expose-Headers", "Authorization");
                    httpResponse.addHeader("Authorization", "Bearer " + jwt.get("token"));
                }

                String responseAsString = objectMapper.writeValueAsString(Map.of(
                        "user", responseObject,
                        "verified", isTwoWayPinVerified,
                        "success", true
                ));
                httpResponse.setContentType("application/json; charset=utf-8");
                httpResponse.getWriter().write(responseAsString);
                return;
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                returnInvalidRequest(httpResponse);
                return;
            }
        }

        returnInvalidRequest(httpResponse);
    }

    private void returnInvalidRequest(HttpServletResponse httpServletResponse) throws IOException {
        httpServletResponse.setStatus(400);
        httpServletResponse.getWriter().write(objectMapper.writeValueAsString(Map.of(
                "success", false,
                "error", "Invalid request, check if you have supplied valid authentication params.")
        ));
    }
}
