package ru.merezh.authservice.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.merezh.authservice.dto.TokensDto;

import java.security.Key;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Service
@Slf4j
public class JwtService {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.token.expiration.access}")
    private int tokenAccessExpiration;

    @Value("${jwt.token.expiration.refresh}")
    private int tokenRefreshExpiration;

    public TokensDto getTokens(long id, String role) {
        return new TokensDto(
                generateAccessToken(id, role),
                generateRefreshToken(id, role)
        );
    }

    public boolean checkValidateRefreshToken(String refreshToken, long userId) {
        try {
            Claims claims = getAllClaims(refreshToken);

            return getUserIdFromToken(refreshToken)
                    .equals(userId) &&
                    !isTokenExpired(claims) &&
                    "refresh".equals(claims.get("type"));
        }
        catch (Exception e) {
            log.error("Ошибка при валидации токена - {} - {}", e.getClass(), e.getMessage());

            return false;
        }
    }

    public Long getUserIdFromToken(String refreshToken) {
        String userIdString = Jwts.parserBuilder()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(refreshToken)
                .getBody()
                .getSubject();

        return Long.valueOf(userIdString);
    }

    public String getUserRoleFromToken(String refreshToken) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(refreshToken)
                .getBody()
                .get("role").toString();
    }

    private boolean isTokenExpired(Claims claims) {
        return claims.getExpiration().before(new Date());
    }

    private Claims getAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private String generateRefreshToken(long id, String role) {
        return Jwts.builder()
                .signWith(getSignKey())
                .setSubject(String.valueOf(id))
                .setExpiration(generateExperationDate(tokenRefreshExpiration))
                .claim("type", "refresh")
                .claim("role", role)
                .compact();

    }

    private String generateAccessToken(long id, String role) {
        return Jwts.builder()
                .signWith(getSignKey())
                .setSubject(String.valueOf(id))
                .setExpiration(generateExperationDate(tokenAccessExpiration))
                .claim("type", "access")
                .claim("role", role)
                .compact();

    }

    private Date generateExperationDate(int seconds) {
        return Date.from(LocalDateTime.now()
                 .plusSeconds(seconds)
                .atZone(ZoneId.systemDefault())
                .toInstant()
        );
    }

    private Key getSignKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
