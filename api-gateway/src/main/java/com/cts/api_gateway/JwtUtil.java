package com.cts.api_gateway;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.security.Key;

@Component
public class JwtUtil {

    private static final Logger logger = LoggerFactory.getLogger(JwtUtil.class);
    public static final String SECRET = "5367566859703373367639792F423F452848284D6251655468576D5A71347437";

    /**
     * Validates the JWT token.
     * Checks for signature validity, expiration, and other potential issues.
     * @param token The JWT token string.
     * @return true if the token is invalid, false if it is valid.
     */
    public boolean isInvalid(String token) {
        try {
            // Jwts.parserBuilder will verify the signature and expiration.
            // If anything is wrong, it will throw a JwtException.
            Jwts.parserBuilder().setSigningKey(getSignKey()).build().parseClaimsJws(token);
            return false; // Token is valid
        } catch (JwtException e) {
            // This catches all JWT-related exceptions: expired, malformed, invalid signature, etc.
            logger.error("JWT validation error: {}", e.getMessage());
            return true; // Token is invalid
        }
    }

    /**
     * Extracts all claims from a validated token.
     * Note: This should only be called AFTER validating the token with isInvalid().
     */
    public Claims getAllClaimsFromToken(String token) {
        return Jwts.parserBuilder().setSigningKey(getSignKey()).build().parseClaimsJws(token).getBody();
    }

    private Key getSignKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}