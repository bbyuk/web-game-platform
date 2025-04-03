package com.bb.webcanvasservice.security.auth;

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
public class JwtManager {

    private final String secretKey = "E2fhmOQToTXJCtVmyCc8AzwQK2bNC9VJBMlBXi/bNEQ=";
    private final long expiration = 3600000; // 1시간 (ms)

    public static final String BEARER_TOKEN = "Authorization";
    public static final String TOKEN_PREFIX = "Bearer ";

    private final SecretKey key;

    public JwtManager() {
        this.key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey));
    }

    /**
     * userId로 토큰 발행
     * @param userId
     * @return
     */
    public String generateToken(Long userId, String fingerprint) {
        return Jwts.builder()
                .subject(userId.toString())
                .claim("fingerprint", fingerprint)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(key, Jwts.SIG.HS256)
                .compact();
    }

    /**
     * 토큰을 파싱해 userId를 리턴한다.
     * @param token
     * @return
     */
    public Long getUserIdFromToken(String token) {
        return Long.parseLong(Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject()
        );
    }

    /**
     * 토큰을 파싱해 fingerprint를 리턴한다.
     * @param token
     * @return
     */
    public String getFingerprintFromToken(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .get("fingerprint", String.class);
    }


    /**
     * 토큰이 유효한 토큰인지 검증한다.
     * @param token
     * @return isValidToken
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parser().verifyWith(key).build().parseSignedClaims(token);
            return true;
        }
        catch(Exception e) {
            return false;
        }
    }

    /**
     * HttpServletRequest의 request header로부터 bearer 토큰 값을 읽어온다.
     * @param request
     * @return
     */
    public String resolveToken(HttpServletRequest request) {
        final String bearerToken = request.getHeader(BEARER_TOKEN);
        if (bearerToken != null && bearerToken.startsWith(TOKEN_PREFIX)) {
            return bearerToken.substring(TOKEN_PREFIX.length());
        }

        return null;
    }

}
