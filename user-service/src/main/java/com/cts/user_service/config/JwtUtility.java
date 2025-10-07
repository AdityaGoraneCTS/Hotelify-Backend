package com.cts.user_service.config;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtility {
    // It's recommended to move this secret to your application.properties or environment variables
    public static final String SECRET = "5367566859703373367639792F423F452848284D6251655468576D5A71347437";
    public static final long EXPIRATION_TIME_MS = 1000 * 60 * 30; // 30 minutes

    /**
     * Generates a JWT for an authenticated user.
     * @param userId The unique, immutable UUID of the user. (CHANGED to String)
     * @param email The user's email, included as a custom claim.
     * @param authority The user's role, included as a custom claim.
     * @return A signed JWT string.
     */
    public String generateToken(String userId, String email, String authority) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, userId, email, authority);
    }

    private String createToken(Map<String, Object> claims, String userId, String email, String authority) {
        // Add custom claims. You can add any other non-sensitive user data here.
        claims.put("ROLES", authority);
        claims.put("email", email); // Add email as a custom claim

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userId) // <-- UPDATED: No need for String.valueOf() as userId is already a String
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME_MS))
                .signWith(getSignKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    private Key getSignKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Extracts the userId (from the subject claim) from the token.
     * Renamed from extractUsername for clarity.
     * @param token The JWT token.
     * @return The user ID as a String.
     */
    public String extractUserId(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * Validates the token against UserDetails.
     * NOTE: This method now validates against the user's primary identifier (ID),
     * not their email. Your UserDetailsService should load the user by their ID,
     * which you extract from the token.
     */
    public Boolean validateToken(String token, UserDetails userDetails) {
        final String userId = extractUserId(token);
        // In your UserDetails implementation, getUsername() should return the user's ID as a String.
        return (userId.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
}