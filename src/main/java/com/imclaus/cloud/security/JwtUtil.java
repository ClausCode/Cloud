package com.imclaus.cloud.security;

import com.imclaus.cloud.models.UserModel;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.*;

@Component
public class JwtUtil {
    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.access.expiration}")
    private Long accessExpiration;

    @Value("${jwt.refresh.expiration}")
    private Long refreshExpiration;

    public Claims extractClaims(String token) {
        SecretKey secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public Long extractUserId(String authToken) {
        return Long.valueOf(extractClaims(authToken)
                .getSubject());
    }

    public boolean validateExpiration(String authToken) {
        return extractClaims(authToken).getExpiration()
                .after(new Date());
    }

    public boolean validateLastUpdate(UserModel model, String authToken) {
        return Objects.equals(model.getUpdated().toEpochMilli(), extractClaims(authToken)
                .get("updated", Long.class));
    }

    public boolean validateBrowser(String browserId, String authToken) {
        return true;
//        try {
//            String tokenId = extractClaims(authToken)
//                    .get("browser", String.class);
//            return browserId.equals(tokenId);
//        } catch (Exception e) {
//            return false;
//        }
    }

    public String generateToken(UserModel user, String browserId) {
        Map<String, Object> claims = new HashMap<>();

        claims.put("updated", user.getUpdated().toEpochMilli());
        claims.put("browser", browserId);

        Date created = new Date();
        Date expiration = new Date(created.getTime() + (accessExpiration * 1000));

        SecretKey secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(user.getId().toString())
                .setIssuedAt(created)
                .setExpiration(expiration)
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateRefreshToken(Long userId, String browserId, Instant update) {
        Map<String, Object> claims = new HashMap<>();

        claims.put("updated", update.toEpochMilli());
        claims.put("browser", browserId);

        Date created = new Date();
        Date expiration = new Date(created.getTime() + (refreshExpiration * 1000));

        SecretKey secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userId.toString())
                .setIssuedAt(created)
                .setExpiration(expiration)
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }
}
