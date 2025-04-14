package com.bb.webcanvasservice.security.auth;

import com.bb.webcanvasservice.common.code.ErrorCode;
import com.bb.webcanvasservice.security.exception.ApplicationAuthenticationException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

/**
 * JWT 토큰의 발급 및 검증 등 JWT 토큰 관리 컴포넌트 클래스
 *
 * TODO signedClaims 캐싱 처리 (우선순위 낮음)
 */
@Component
public class JwtManager {

    private final String secretKey = "E2fhmOQToTXJCtVmyCc8AzwQK2bNC9VJBMlBXi/bNEQ=";

    public static final String BEARER_TOKEN = "Authorization";
    public static final String TOKEN_PREFIX = "Bearer ";

    private final SecretKey key;

    public JwtManager() {
        this.key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey));
    }

    /**
     * userId, fingerprint 토큰 발행
     * @param userId 유저 ID
     * @param fingerprint 서버 등록시 발급된 유저 fingerprint
     * @param expiration 토큰 만료 시간 (ms)
     * @return
     */
    public String generateToken(Long userId, String fingerprint, long expiration) {
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
    public void validateToken(String token) {
        try {
            Jwts.parser().verifyWith(key).build().parseSignedClaims(token);
        }
        catch (ExpiredJwtException e) {
            throw new ApplicationAuthenticationException(ErrorCode.TOKEN_EXPIRED);
        } catch (SignatureException e) {
            throw new ApplicationAuthenticationException(ErrorCode.INVALID_SIGNATURE);
        } catch (MalformedJwtException e) {
            throw new ApplicationAuthenticationException(ErrorCode.MALFORMED_TOKEN);
        } catch (UnsupportedJwtException e) {
            throw new ApplicationAuthenticationException(ErrorCode.UNSUPPORTED_TOKEN);
        } catch (Exception e) {
            throw new ApplicationAuthenticationException(ErrorCode.INVALID_TOKEN);
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

    /**
     * 토큰의 expiration time까지 남은 시간을 계산하여 ms로 리턴
     */
    public long calculateRemainingExpiration(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload().getExpiration().getTime() - System.currentTimeMillis();
    }

}
