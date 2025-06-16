package com.bb.webcanvasservice.application.auth.service;

import com.bb.webcanvasservice.application.auth.command.LoginCommand;
import com.bb.webcanvasservice.application.auth.dto.LoginSuccessDto;
import com.bb.webcanvasservice.application.auth.dto.TokenRefreshSuccessDto;
import com.bb.webcanvasservice.common.code.ErrorCode;
import com.bb.webcanvasservice.common.util.FingerprintGenerator;
import com.bb.webcanvasservice.common.util.JwtManager;
import com.bb.webcanvasservice.domain.user.model.User;
import com.bb.webcanvasservice.domain.user.service.UserService;
import com.bb.webcanvasservice.common.web.security.SecurityProperties;
import com.bb.webcanvasservice.common.web.security.exception.ApplicationAuthenticationException;
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

    private final JwtManager jwtManager;
    private final UserService userService;
    private final SecurityProperties securityProperties;

    /**
     * fingerprint를 username으로 로그인 처리를 수행한다.
     * <p>
     * accessToken과 refreshToken을 새로 발급
     * fingerprint로 등록된 유저가 없을 시 유저 생성 서비스 요청
     * User 엔티티에 refreshToken update
     *
     * @param command 로그인 요청 커맨드
     * @return (accessToken, refreshToken) 새로 발급받은 accessToken, refreshToken 튜플
     */
    @Transactional
    public LoginSuccessDto login(LoginCommand command) {

        com.bb.webcanvasservice.domain.user.model.User user = userService.findOrCreateUser(
                StringUtils.isBlank(command.fingerprint())
                        ? FingerprintGenerator.generate()
                        : command.fingerprint());

        String accessToken = jwtManager.generateToken(user.getId(), user.getFingerprint(), securityProperties.accessTokenExpiration());
        String refreshToken = jwtManager.generateToken(user.getId(), user.getFingerprint(), securityProperties.refreshTokenExpiration());

        userService.updateRefreshToken(user.getId(), refreshToken);

        return new LoginSuccessDto(user.getId(), user.getFingerprint(), accessToken, refreshToken, true);
    }

    /**
     * refreshToken을 받아 올바른 요청인지 확인 후 인증 토큰을 refresh한다.
     * @param token
     * @return
     */
    @Transactional
    public TokenRefreshSuccessDto refreshToken(String token) {
        jwtManager.validateToken(token);

        /**
         * 이 토큰이 유저에게 할당된 refreshToken인지 validation
         */
        Long userId = jwtManager.getUserIdFromToken(token);
        User user = userService.findUser(userId);

        if (!user.getRefreshToken().equals(token)) {
            log.error("유저에게 할당되지 않은 refresh token 입니다.");
            log.error("유저 ID : {}", userId);
            log.error("요청된 refreshToken : {}", token);
            throw new ApplicationAuthenticationException(ErrorCode.INVALID_TOKEN);
        }

        /**
         * validation 통과시 Access Token 재발급
         */
        String reissuedAccessToken = jwtManager.generateToken(userId, user.getFingerprint(), securityProperties.accessTokenExpiration());

        boolean refreshTokenReissued = false;
        /**
         * refreshToken reissue check
         * expiration까지 남은 시간이 reissue threshold에 도달한 경우
         *
         * refreshToken 재발급 및 rotate
         */
        if (jwtManager.calculateRemainingExpiration(token) <= securityProperties.refreshTokenReissueThreshold()) {
            String reissuedRefreshToken = jwtManager.generateToken(userId, user.getFingerprint(), securityProperties.refreshTokenExpiration());
            userService.updateRefreshToken(userId, reissuedRefreshToken);
            refreshTokenReissued = true;
        }

        return new TokenRefreshSuccessDto(user.getId(), user.getFingerprint(), reissuedAccessToken, user.getRefreshToken(), refreshTokenReissued);
    }
}
