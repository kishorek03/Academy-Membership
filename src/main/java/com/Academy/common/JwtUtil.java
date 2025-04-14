package com.Academy.common;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtUtil {

    private final String SECRET_KEY = "MYSECRETKEYFOR=MYTTACADEMYAPPLICATION=BYKISHORE";
    private final long EXPIRATION_TIME = 1000 * 60 * 60; // 10 hours

    // Generate token with username and email as claims
    public String generateToken(String username, String email,Long id ,String role) {
        return Jwts.builder()
                .setSubject(username) // Set username as subject
                .claim("email", email)// set email as claims
                .claim("userId", id)
                .claim("role", role)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
    }

    // Extract username from token
    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    // Extract email from token
    public String extractEmail(String token) {
        return extractAllClaims(token).get("email", String.class); // Get email from claims
    }

    // Validate token
    public boolean validateToken(String token, String username) {
        String extractedUsername = extractUsername(token);
        return (extractedUsername.equals(username) && !isTokenExpired(token));
    }

    // Check if token is expired
    private boolean isTokenExpired(String token) {
        return extractAllClaims(token).getExpiration().before(new Date());
    }

    // Extract claims
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .parseClaimsJws(token)
                .getBody();
    }
}
