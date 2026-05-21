package com.gateway.gatewayservice.util;

import com.gateway.gatewayservice.constants.SecurityConstants;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtUtil {

    private final SecretKey secretKey =
            Keys.hmacShaKeyFor(SecurityConstants.SECRET_KEY.getBytes());

    /*
     * Generate JWT Token
     */
    public String generateToken(String username) {

        Date now = new Date();

        Date expiryDate = new Date(
                now.getTime() + SecurityConstants.JWT_EXPIRATION
        );

        return Jwts.builder()
                .subject(username)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(secretKey)
                .compact();
    }

    /*
     * Extract Username from Token
     */
    public String extractUsername(String token) {
        return extractClaims(token).getSubject();
    }

    /*
     * Validate Token
     */
    public boolean validateToken(String token) {
        try {
            extractClaims(token);
            return !isTokenExpired(token);
        } catch (Exception ex) {
            return false;
        }
    }

    /*
     * Check Token Expiration
     */
    private boolean isTokenExpired(String token) {
        return extractClaims(token)
                .getExpiration()
                .before(new Date());
    }

    /*
     * Extract All Claims
     */
    private Claims extractClaims(String token) {

        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}