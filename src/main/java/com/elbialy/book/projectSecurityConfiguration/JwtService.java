package com.elbialy.book.projectSecurityConfiguration;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.time.Instant;
import java.time.LocalDate;
import java.util.*;

@Service
public class JwtService {
    @Value("${application.security.jwt.secret-key}")
    private String secretKey = "secretKey";

    @Value("${application.security.jwt.expiration}")
    private Long jwtExpiration;

    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>() , userDetails);
    }
    public String generateToken(Map<String, Object> claims, UserDetails userDetails) {
        return build(claims,userDetails,jwtExpiration);
    }
    public String build(Map<String, Object> claims,
                        UserDetails userDetails,
                        Long jwtExpiration) {

        var authorities = userDetails.getAuthorities().stream().
                map(GrantedAuthority::getAuthority).toList();

        return Jwts.builder()
                .claims(claims)
                .subject(userDetails.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .claim("authorities",authorities)
                .signWith(getKey())
                .compact();
    }

    private Key getKey() {
        byte [] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

}
