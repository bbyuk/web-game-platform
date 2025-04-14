package com.bb.webcanvasservice.security.auth;

import com.bb.webcanvasservice.common.FingerprintGenerator;
import com.bb.webcanvasservice.common.code.ErrorCode;
import com.bb.webcanvasservice.domain.user.User;
import com.bb.webcanvasservice.domain.user.UserService;
import com.bb.webcanvasservice.security.auth.dto.response.AuthenticationResponse;
import com.bb.webcanvasservice.security.exception.NotAuthenticatedException;
import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 인증, 로그인 처리를 담당하는 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthenticationService {

    /**
     * Access Token 만료시간
     * 15분 (ms)
     */
    private final long accessTokenExpiration = 15 * 60 * 1000;

    /**
     * Refresh Token 만료시간
     * 14일 (ms)
     */
    private final long refreshTokenExpiration = 14 * 24 * 60 * 60 * 1000;
    /**
     * Refresh Token 재발급 임계 시간
     * 현재 시각부터 입력받은 refresh token의 expiration time 까지 남은 시간이
     * refreshTokenReissueThreshold보다 작을 경우 Refresh Token 재발급 및 rotate
     * 3일 (72시간) (ms)
     */
    private final long refreshTokenReissueThreshold = 3 * 24 * 60 * 60 * 1000;

    private final JwtManager jwtManager;
    private final UserService userService;

    /**
     * fingerprint를 username으로 로그인 처리를 수행한다.
     * <p>
     * accessToken과 refreshToken을 새로 발급
     * fingerprint로 등록된 유저가 없을 시 유저 생성 서비스 요청
     * User 엔티티에 refreshToken update
     *
     * @param fingerprint 유저 fingerprint(username)
     * @return (accessToken, refreshToken) 새로 발급받은 accessToken, refreshToken 튜플
     */
    @Transactional
    public AuthenticationResponse login(String fingerprint) {

        User user = userService.findOrCreateUser(
                StringUtils.isBlank(fingerprint)
                        ? FingerprintGenerator.generate()
                        : fingerprint);

        String accessToken = jwtManager.generateToken(user.getId(), user.getFingerprint(), accessTokenExpiration);
        String refreshToken = jwtManager.generateToken(user.getId(), user.getFingerprint(), refreshTokenExpiration);

        user.updateRefreshToken(refreshToken);

        return new AuthenticationResponse(user.getFingerprint(), accessToken, refreshToken);
    }

    /**
     * refreshToken을 받아 올바른 요청인지 확인 후 인증 토큰을 refresh한다.
     * @param token
     * @return
     */
    @Transactional
    public AuthenticationResponse refreshToken(String token) {
        /**
         * 이 토큰이 유저에게 할당된 refreshToken인지 validation
         */
        Long userId = jwtManager.getUserIdFromToken(token);

        User user = userService.findUserByUserId(userId);
        if (!user.getRefreshToken().equals(token)) {
            log.error("유저에게 할당되지 않은 refresh token 입니다.");
            log.error("유저 ID : {}", userId);
            log.error("요청된 refreshToken : {}", token);
            throw new NotAuthenticatedException(ErrorCode.INVALID_TOKEN);
        }

        /**
         * validation 통과시 Access Token 재발급
         */
        String reissuedAccessToken = jwtManager.generateToken(userId, user.getFingerprint(), accessTokenExpiration);

        /**
         * refreshToken reissue check
         * expiration까지 남은 시간이 reissue threshold에 도달한 경우
         *
         * refreshToken 재발급 및 rotate
         */
        if (jwtManager.calculateRemainingExpiration(token) <= refreshTokenReissueThreshold) {
            user.updateRefreshToken(jwtManager.generateToken(userId, user.getFingerprint(), refreshTokenExpiration));
        }

        return new AuthenticationResponse(user.getFingerprint(), reissuedAccessToken, user.getRefreshToken());
    }
}
