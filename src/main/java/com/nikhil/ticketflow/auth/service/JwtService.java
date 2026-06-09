package com.nikhil.ticketflow.auth.service;

import com.nikhil.ticketflow.users.entity.UserEntity;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Arrays;
import java.util.Date;
import java.util.UUID;

@Service
public class JwtService {

    private final Key key;
    private final Long accessValidity;

    public JwtService(
            @Value("${auth.jwt.secret}") String secret,
            @Value("${auth.jwt.access-token-validity-seconds}") Long accessValidity
    ){
        this. key = Keys.hmacShaKeyFor(Arrays.copyOf(secret.getBytes(), 64));
        this.accessValidity = accessValidity;
    }

    public String generateToken(UserEntity user){
        long now = System.currentTimeMillis();

        return Jwts.builder()
                .subject(user.getId().toString())
                .claim("role", user.getRole())
                .issuedAt(new Date(now))
                .expiration(new Date(now + accessValidity * 1000))
                .signWith(key)
                .compact();
    }

    public Claims extractClaims(String token){
        return Jwts.parser()
                .verifyWith((javax.crypto.SecretKey) key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public UUID extractUserId(String token){
        return UUID.fromString(extractClaims(token).getSubject());
    }

    public String extractRole(String token){
        return extractClaims(token).get("role", String.class);
    }

    public boolean isTokenValid(String token){
        try{
            extractClaims(token);
            return true;
        } catch(Exception e){
            return false;
        }
    }
}
