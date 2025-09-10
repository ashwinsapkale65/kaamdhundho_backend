package com.backend.KaamDhundho.config;


import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.security.Key;
import java.time.Instant;
import java.util.Date;
import java.util.Map;

@Component
public class JwtService {
    private final Key key;
    private final String issuer;
    public final int accessMinutes;
    public final int refreshTokenMinutes; // Duration for Refresh Token

    public JwtService(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.issuer}") String issuer,
            @Value("${jwt.access-token-minutes}") int accessMinutes,
            @Value("${jwt.refresh-token-minutes}") int refreshTokenMinutes // Add refresh token expiration time
    ) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
        this.issuer = issuer;
        this.accessMinutes = accessMinutes;
        this.refreshTokenMinutes = refreshTokenMinutes;
    }

    // Method to generate Access Token (JWT)
    public String generateAccessToken(Long userId, String role) {
        Instant now = Instant.now();
        Instant exp = now.plusSeconds(accessMinutes * 60L);
        return Jwts.builder()
                .setIssuer(issuer)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(exp))
                .addClaims(Map.of("userId", userId, "role", role))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    // Method to generate Refresh Token (longer expiration)
    public String generateRefreshToken(Long userId) {
        Instant now = Instant.now();
        Instant exp = now.plusSeconds(refreshTokenMinutes * 60L);
        return Jwts.builder()
                .setIssuer(issuer)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(exp))
                .addClaims(Map.of("userId", userId))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    // Method to parse a token (either Access or Refresh Token)
    public Jws<Claims> parse(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
    }

    // ✅ Extract userId from JWT token
    public Long extractUserId(String token) {
        try {
            Claims claims = parse(token).getBody();
            Object userIdObj = claims.get("userId");
            if (userIdObj instanceof Integer) {
                return ((Integer) userIdObj).longValue();
            } else if (userIdObj instanceof Long) {
                return (Long) userIdObj;
            } else if (userIdObj instanceof String) {
                return Long.parseLong((String) userIdObj);
            }
            throw new RuntimeException("Invalid userId type in JWT");
        } catch (JwtException | IllegalArgumentException e) {
            throw new RuntimeException("Invalid JWT token: " + e.getMessage());
        }
    }

    // ✅ Optionally, extract role if needed
    public String extractRole(String token) {
        Claims claims = parse(token).getBody();
        return claims.get("role", String.class);
    }
}
