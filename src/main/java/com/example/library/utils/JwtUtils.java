package com.example.library.utils;

import com.example.library.services.VariableService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.function.Function;
import java.util.Base64;

@Component
public class JwtUtils {

    @Autowired
    private VariableService variableService;

    public String generateToken(String username) {
        String secretKey = variableService.getVariableValue("SECRET_KEY", "default_secret");

        byte[] decodedKey = Base64.getDecoder().decode(secretKey);

        long expirationTime = variableService.getVariableValueAsLong("EXPIRATION_TIME", 3600000L);

        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(SignatureAlgorithm.HS256, decodedKey)
                .compact();
    }

    public boolean validateToken(String token, String username) {
        return extractUsername(token).equals(username) && !isTokenExpired(token);
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        String secretKey = variableService.getVariableValue("SECRET_KEY", "default_secret");
        Claims claims = Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(token)
                .getBody();
        return claimsResolver.apply(claims);
    }
}
