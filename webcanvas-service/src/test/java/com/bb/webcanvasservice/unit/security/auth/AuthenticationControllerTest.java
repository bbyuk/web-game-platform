package com.bb.webcanvasservice.unit.security.auth;

import com.bb.webcanvasservice.common.util.FingerprintGenerator;
import com.bb.webcanvasservice.infrastructure.persistence.user.entity.UserJpaEntity;
import com.bb.webcanvasservice.domain.user.service.UserService;
import com.bb.webcanvasservice.web.security.SecurityProperties;
import com.bb.webcanvasservice.domain.auth.service.AuthenticationService;
import com.bb.webcanvasservice.common.util.JwtManager;
import com.bb.webcanvasservice.domain.auth.dto.request.LoginRequest;
import com.bb.webcanvasservice.domain.auth.dto.response.AuthenticationInnerResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.lang.reflect.Field;
import java.util.ArrayList;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("[unit] [presentation] 인증 API Controller WebMvcTest 단위테스트")
class AuthenticationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private JwtManager jwtManager;

    @MockitoBean
    private AuthenticationService authenticationService;

    @MockitoBean
    private UserService userService;

    private SecurityProperties securityProperties;

    @BeforeEach
    void setup() throws Exception {
        securityProperties = new SecurityProperties("dsnadsnaodnsaoidsnadsnaodnsaoidsnadsnaodnsaoidsnadsnaodnsaoi", 900000L, 1209600000L, 259200000L, new ArrayList<>(), new SecurityProperties.AuthenticationCookies("refresh-token"));
    }

    @Test
    @DisplayName("로그인  - 인증 토큰 발급")
    void login() throws Exception {
        // given
        String fingerprint = FingerprintGenerator.generate();
        long userId = 1L;
        JwtManager realJwtManager = new JwtManager(securityProperties);
        long expiration = 3600000; // 1시간 (ms)
        String token = realJwtManager.generateToken(userId, fingerprint, expiration);

        BDDMockito.given(jwtManager.generateToken(any(), any(), anyLong())).willReturn(token);

        BDDMockito.given(authenticationService.login(any()))
                .willReturn(new AuthenticationInnerResponse(userId, fingerprint, realJwtManager.generateToken(userId, fingerprint, expiration), realJwtManager.generateToken(userId, fingerprint, expiration), true));

        LoginRequest loginRequest = new LoginRequest(fingerprint);

        // when
        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest))
                .with(csrf())
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").exists());

        // then
    }

    @Test
    @DisplayName("로그인 - 쿠키에 Refresh token set-cookie")
    void whenLoginSuccessResponseContainsRefreshTokenCookie() throws Exception {
        // given
        String fingerprint = FingerprintGenerator.generate();
        long userId = 1L;
        JwtManager realJwtManager = new JwtManager(securityProperties);
        long expiration = 3600000; // 1시간 (ms)
        String token = realJwtManager.generateToken(userId, fingerprint, expiration);

        BDDMockito.given(jwtManager.generateToken(any(), any(), anyLong())).willReturn(token);

        BDDMockito.given(authenticationService.login(any()))
                .willReturn(new AuthenticationInnerResponse(userId, fingerprint, realJwtManager.generateToken(userId, fingerprint, expiration), realJwtManager.generateToken(userId, fingerprint, expiration), true));

        LoginRequest loginRequest = new LoginRequest(fingerprint);

        // when
        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest))
                        .with(csrf())
        )
                .andExpect(status().isOk())
                .andExpect(header().stringValues(HttpHeaders.SET_COOKIE, Matchers.hasItems(
                                Matchers.containsString("refresh-token"),
                                Matchers.containsString("HttpOnly"),
                                Matchers.containsString("Path=/"))));

        // then
    }

    @Test
    @DisplayName("토큰 refresh - expiration이 Threshold보다 적게 남은 refresh token으로 refresh 요청 시 응답 쿠키에 refresh token 포함")
    void whenRefreshTokenExpiredResponseContainsRefreshToken() throws Exception {
        // given
        String fingerprint = FingerprintGenerator.generate();
        long userId = 1L;
        JwtManager realJwtManager = new JwtManager(securityProperties);
        long refreshTokenExpiration = 24 * 3600000; // 1시간 (ms)
        String token = realJwtManager.generateToken(userId, fingerprint, refreshTokenExpiration);
        UserJpaEntity user = new UserJpaEntity(fingerprint);
        user.updateRefreshToken(token);
        Field userIdField = UserJpaEntity.class.getDeclaredField("id");
        userIdField.setAccessible(true);
        userIdField.set(user, userId);


        BDDMockito.given(authenticationService.refreshToken(any()))
                .willReturn(new AuthenticationInnerResponse(
                        userId,
                        fingerprint,
                        realJwtManager.generateToken(userId, fingerprint, refreshTokenExpiration),
                        realJwtManager.generateToken(userId, fingerprint, refreshTokenExpiration),
                        true)
                );

        // when
        mockMvc.perform(post("/auth/refresh")
                        .cookie(new Cookie("refresh-token", token))
                .contentType(MediaType.APPLICATION_JSON).with(csrf()))
                .andExpect(status().isOk())
                .andExpect(header().stringValues(HttpHeaders.SET_COOKIE, Matchers.hasItems(
                        Matchers.containsString("refresh-token"),
                        Matchers.containsString("HttpOnly"),
                        Matchers.containsString("Path=/")
                )));

        // then
    }

    @Test
    @DisplayName("토큰 refresh - expiration이 Threshold보다 많이 남아있어 refresh token이 재발급되지 않은 경우 쿠키에 refresh token 포함되지 않음")
    void whenRefreshTokenNotRefreshedThenResponseCookieNotContainsRefreshToken() throws Exception {
        // given
        // given
        String fingerprint = FingerprintGenerator.generate();
        long userId = 1L;
        JwtManager realJwtManager = new JwtManager(securityProperties);
        long accessTokenExpiration = 24 * 3600000; // 1시간 (ms)
        String token = realJwtManager.generateToken(userId, fingerprint, accessTokenExpiration);
        UserJpaEntity user = new UserJpaEntity(fingerprint);
        user.updateRefreshToken(token);
        Field userIdField = UserJpaEntity.class.getDeclaredField("id");
        userIdField.setAccessible(true);
        userIdField.set(user, userId);


        BDDMockito.given(authenticationService.refreshToken(any()))
                .willReturn(new AuthenticationInnerResponse(
                        userId,
                        fingerprint,
                        realJwtManager.generateToken(userId, fingerprint, accessTokenExpiration),
                        realJwtManager.generateToken(userId, fingerprint, accessTokenExpiration),
                        false)
                );
        // when
        mockMvc.perform(post("/auth/refresh")
                        .cookie(new Cookie("refresh-token", token))
                        .contentType(MediaType.APPLICATION_JSON).with(csrf()))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.SET_COOKIE, Matchers.nullValue()));
        // then

    }

}