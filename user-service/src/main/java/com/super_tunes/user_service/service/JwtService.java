package com.super_tunes.user_service.service;

import io.jsonwebtoken.Jwts;
  import io.jsonwebtoken.io.Decoders;
  import io.jsonwebtoken.security.Keys;
  import org.springframework.beans.factory.annotation.Value;
  import org.springframework.stereotype.Service;

  import javax.crypto.SecretKey;
  import java.util.Date;
  import java.util.HashMap;
  import java.util.Map;

  @Service
  public class JwtService {

      @Value("${app.jwt.secret}")
      private String jwtSecret;

      @Value("${app.jwt.expiration-ms}")
      private long jwtExpirationMs;

      public String generateToken(Long userId, String username, String email)
      {
          Map<String, Object> claims = new HashMap<>();
          claims.put("userId", userId);
          claims.put("username", username);

          Date now = new Date();
          Date expiry = new Date(now.getTime() + jwtExpirationMs);

          return Jwts.builder()
              .claims(claims)
              .subject(email)
              .issuedAt(now)
              .expiration(expiry)
              .signWith(getSigningKey())
              .compact();
      }

      private SecretKey getSigningKey() {
          byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
          return Keys.hmacShaKeyFor(keyBytes);
      }
  }