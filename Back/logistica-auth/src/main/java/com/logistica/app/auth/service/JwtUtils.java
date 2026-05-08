package com.logistica.app.auth.service;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtUtils {
    @Value("${app.jwt.secret}") private String jwtSecret;
    @Value("${app.jwt.expiration-ms}") private int jwtExpirationMs;

    public String generateJwtToken(Authentication authentication) {
        UserDetails u = (UserDetails) authentication.getPrincipal();
        return Jwts.builder().subject(u.getUsername()).issuedAt(new Date())
            .expiration(new Date(System.currentTimeMillis()+jwtExpirationMs))
            .signWith(getSigningKey()).compact();
    }
    public String getUsernameFromJwtToken(String token) {
        return Jwts.parser().verifyWith(getSigningKey()).build().parseSignedClaims(token).getPayload().getSubject();
    }
    public boolean validateJwtToken(String token) {
        try { Jwts.parser().verifyWith(getSigningKey()).build().parseSignedClaims(token); return true; }
        catch (JwtException | IllegalArgumentException e) { return false; }
    }
    private SecretKey getSigningKey() { return Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8)); }
}