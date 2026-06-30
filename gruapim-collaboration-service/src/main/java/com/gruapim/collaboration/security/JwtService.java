package com.gruapim.collaboration.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;

@Service
public class JwtService {

    @Value("${gruapim.security.jwt.secret}")
    private String secret;

    public boolean isTokenValid(String token) {
        try {
            Claims claims = parseAllClaims(token);
            return !claims.getExpiration().before(new Date());
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public String extractEmail(String token) {
        return parseAllClaims(token).getSubject();
    }

    public UUID extractUserId(String token) {
        String uid = parseAllClaims(token).get("uid", String.class);
        return uid != null ? UUID.fromString(uid) : null;
    }

    public String extractName(String token) {
        return parseAllClaims(token).get("name", String.class);
    }

    private Claims parseAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(signingKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey signingKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }
}
