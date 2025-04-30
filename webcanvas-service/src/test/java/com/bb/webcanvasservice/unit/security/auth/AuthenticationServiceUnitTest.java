package com.bb.webcanvasservice.unit.security.auth;

import com.bb.webcanvasservice.common.FingerprintGenerator;
import com.bb.webcanvasservice.domain.user.User;
import com.bb.webcanvasservice.domain.user.UserService;
import com.bb.webcanvasservice.security.SecurityProperties;
import com.bb.webcanvasservice.security.auth.AuthenticationService;
import com.bb.webcanvasservice.security.auth.JwtManager;
import com.bb.webcanvasservice.security.auth.dto.response.AuthenticationInnerResponse;
import com.bb.webcanvasservice.security.exception.ApplicationAuthenticationException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.util.ArrayList;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("인증 서비스 단위테스트")
class AuthenticationServiceUnitTest {

    @Mock
    private UserService userService;

    @Mock
    private JwtManager jwtManager;

    /**
     * Mock
     */
    private SecurityProperties securityProperties = new SecurityProperties(900000L, 1209600000L, 259200000L, new ArrayList<>());
    private AuthenticationService authenticationService;

    @BeforeEach
    void setup() throws Exception {
        authenticationService = new AuthenticationService(jwtManager, userService, securityProperties);
    }

    @Test
    @DisplayName("로그인 - fingerprint로 등록되어 있는지 여부와 상관없이 토큰이 발급되어 리턴되어야 한다.")
    void testLogin() throws Exception {
        // given
        String fingerprint = "asdwqujdqwi12j3b1jbsd";
        JwtManager realJwtManagerObject = new JwtManager();

        User user = new User(fingerprint);
        Field idField = User.class.getDeclaredField("id");
        idField.setAccessible(true);
        long userId = 1L;
        idField.set(user, userId);

        long expiration = 3600000; // 1시간 (m)

        when(userService.findOrCreateUser(any()))
                .thenReturn(user);
        when(jwtManager.generateToken(any(), any(), anyLong()))
                .thenReturn(realJwtManagerObject.generateToken(userId, fingerprint, expiration));

        // when
        AuthenticationInnerResponse authenticationInnerResponse = authenticationService.login(fingerprint);

        String accessToken = authenticationInnerResponse.accessToken();
        String refreshToken = authenticationInnerResponse.refreshToken();

        // then
        Assertions.assertThat(accessToken).isNotBlank();
        Assertions.assertThat(refreshToken).isNotBlank();
    }


    @Test
    @DisplayName("토큰 리프레시 - 유저에게 할당되지 않은 refreshToken으로 refresh 요청시 ApplicationAuthenticationException 발생")
    void testRefreshTokenFailedWhenNotUserOwnToken() throws Exception{
        // given
        JwtManager realJwtManagerObject = new JwtManager();
        Long userId = 1L;
        User user = new User(FingerprintGenerator.generate());
        Field idField = User.class.getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(user, userId);
        String userRefreshToken = realJwtManagerObject.generateToken(userId, user.getFingerprint(), 15 * 60 * 1000);
        user.updateRefreshToken(userRefreshToken);

        when(userService.findUserByUserId(any(Long.class))).thenReturn(user);
        when(jwtManager.getUserIdFromToken(any())).thenReturn(userId);

        // when
        String anotherValidToken = realJwtManagerObject.generateToken(userId, user.getFingerprint(), 15 * 60 * 1001);

        Assertions.assertThatThrownBy(() -> authenticationService.refreshToken(anotherValidToken))
                .isInstanceOf(ApplicationAuthenticationException.class);

        // then
    }

    @Test
    @DisplayName("토큰 리프레시 - refreshToken의 expiration까지 3일 이내로 남았을 경우 refreshToken도 재발급 및 rotate")
    void refreshTokenRefreshedEitherWhenExpirationLessThanThreshold() throws Exception {
        // given
        JwtManager realJwtManagerObject = new JwtManager();
        Long userId = 1L;
        User user = new User(FingerprintGenerator.generate());
        String userRefreshToken = realJwtManagerObject.generateToken(userId, FingerprintGenerator.generate(), 20000);
        user.updateRefreshToken(userRefreshToken);

        Field idField = User.class.getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(user, userId);

        when(userService.findUserByUserId(any(Long.class))).thenReturn(user);
        when(jwtManager.getUserIdFromToken(any())).thenReturn(userId);
        when(jwtManager.generateToken(any(), any(), anyLong())).thenReturn(realJwtManagerObject.generateToken(user.getId(), user.getFingerprint(), 15 * 60 * 1000));
        when(jwtManager.calculateRemainingExpiration(any())).thenReturn(Long.valueOf(3 * 24 * 60 * 60 * 1000));

        // when
        AuthenticationInnerResponse tokenRefreshResponse = authenticationService.refreshToken(userRefreshToken);

        // then
        Assertions.assertThat(tokenRefreshResponse.refreshToken()).isNotEqualTo(userRefreshToken);
        Assertions.assertThat(tokenRefreshResponse.refreshTokenReissued()).isTrue();
    }

}