package com.pingchat.authenticationservice.auth.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Slf4j
@Setter
@Component
@ConfigurationProperties(prefix = "jwt")
public class JwtTokenHandler {
    private static final Long JWT_VALID_FOR_MILLIS = 50 * 365 * 24 * 60 * 60 * 1000L; // 50 years (50 * 365)

    private String secret;
    private String issuer;

    public String generateToken(String subject, List<String> roles, String audience) {
        return Jwts.builder()
                .signWith(SignatureAlgorithm.HS512, secret)
                .setHeaderParam("typ", "JWT")
                .setIssuer(issuer)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setAudience(audience)
                .setSubject(subject)
                .addClaims(Map.of("rol", roles))
                .setExpiration(new Date(System.currentTimeMillis() + JWT_VALID_FOR_MILLIS))
                .compact();
    }

    public Jws<Claims> parse(String bearerToken) {
        return Jwts.parser()
                .setSigningKey(secret)
                .parseClaimsJws(bearerToken.replace("Bearer ", ""));
    }

    public String getSubject(String bearerToken) {
        return Jwts.parser()
                .setSigningKey(secret)
                .parseClaimsJws(bearerToken.replace("Bearer ", ""))
                .getBody()
                .getSubject();
    }

    public boolean isValid(String bearerToken) {
        try {
            Claims claims = parse(bearerToken).getBody();

            boolean isValidIssuer = claims.getIssuer().equals(issuer);
            boolean isNotExpired = claims.getIssuedAt().before(claims.getExpiration());

            return isValidIssuer && isNotExpired;
        } catch (Exception ignored) {
        }

        return false;
    }
}
