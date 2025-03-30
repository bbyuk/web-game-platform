package com.bb.webcanvasservice.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

/**
 * JWT 토큰의 발급 및 검증 등 JWT 토큰 관리 컴포넌트 클래스
 */
@Component
public class JwtTokenManager {

    private final String secretKey = "E2fhmOQToTXJCtVmyCc8AzwQK2bNC9VJBMlBXi/bNEQ=";
    private final long expiration = 3600000; // 1시간 (ms)

    public static final String BEARER_TOKEN = "Authorization";
    public static final String TOKEN_PREFIX = "Bearer ";

    private final SecretKey key;

    public JwtTokenManager() {
        this.key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey));
    }

    public String generateToken(Long userId) {
        return Jwts.builder()
                .subject(userId.toString())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(key, Jwts.SIG.HS256)
                .compact();
    }

    public Long getUserIdFromToken(String token) {
        return Long.parseLong(Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject()
        );
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser().verifyWith(key).build().parseSignedClaims(token);
            return true;
        }
        catch(Exception e) {
            return false;
        }
    }

    public String resolveToken(HttpServletRequest request) {
        final String bearerToken = request.getHeader(BEARER_TOKEN);
        if (bearerToken != null && bearerToken.startsWith(TOKEN_PREFIX)) {
            return bearerToken.substring(TOKEN_PREFIX.length());
        }

        return null;
    }

}
