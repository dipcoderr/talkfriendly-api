package com.talkfriendly.api.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.*;
import javax.crypto.SecretKey;
import org.springframework.stereotype.Service;

@Service
public class JwtService {
    private final JwtProperties properties;
    private final SecretKey signingKey;
    public JwtService(JwtProperties properties) {
        this.properties=properties;
        try { this.signingKey=Keys.hmacShaKeyFor(Base64.getDecoder().decode(properties.secret())); }
        catch (IllegalArgumentException ex) { throw new IllegalStateException("JWT_SECRET must be Base64-encoded", ex); }
    }
    public String generateAccessToken(UUID userId, String email, String role) {
        Instant now=Instant.now();
        return Jwts.builder().subject(userId.toString()).claim("email",email).claim("role",role).issuedAt(Date.from(now)).expiration(Date.from(now.plus(properties.accessTokenExpiration()))).signWith(signingKey).compact();
    }
    public UUID extractUserId(String token) { return UUID.fromString(parse(token).getPayload().getSubject()); }
    public boolean isValid(String token) { try { parse(token); return true; } catch (JwtException | IllegalArgumentException ex) { return false; } }
    private Jws<Claims> parse(String token) { return Jwts.parser().verifyWith(signingKey).build().parseSignedClaims(token); }
}
