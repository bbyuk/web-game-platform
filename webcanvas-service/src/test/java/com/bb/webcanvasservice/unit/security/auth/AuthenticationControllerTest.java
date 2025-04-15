package com.bb.webcanvasservice.unit.security.auth;

import com.bb.webcanvasservice.common.FingerprintGenerator;
import com.bb.webcanvasservice.domain.user.User;
import com.bb.webcanvasservice.domain.user.UserService;
import com.bb.webcanvasservice.security.SecurityProperties;
import com.bb.webcanvasservice.security.auth.AuthenticationController;
import com.bb.webcanvasservice.security.auth.AuthenticationService;
import com.bb.webcanvasservice.security.auth.JwtManager;
import com.bb.webcanvasservice.security.auth.dto.request.LoginRequest;
import com.bb.webcanvasservice.security.auth.dto.response.AuthenticationInnerResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.lang.reflect.Field;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
        controllers = {AuthenticationController.class},
        excludeAutoConfiguration = {SecurityAutoConfiguration.class}
)
@DisplayName("인증 API Controller WebMvcTest 단위테스트")
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

    @MockitoBean
    private SecurityProperties securityProperties;

    @BeforeEach
    void setup() throws Exception {
        securityProperties = new SecurityProperties(900000, 1209600000, 259200000);
    }

    @Test
    @DisplayName("로그인  - 인증 토큰 발급")
    void login() throws Exception {
        // given
        String fingerprint = FingerprintGenerator.generate();
        long userId = 1L;
        JwtManager realJwtManager = new JwtManager();
        long expiration = 3600000; // 1시간 (ms)
        String token = realJwtManager.generateToken(userId, fingerprint, expiration);

        BDDMockito.given(jwtManager.generateToken(any(), any(), anyLong())).willReturn(token);

        BDDMockito.given(authenticationService.login(any()))
                .willReturn(new AuthenticationInnerResponse(fingerprint, realJwtManager.generateToken(userId, fingerprint, expiration), realJwtManager.generateToken(userId, fingerprint, expiration), true));

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
        JwtManager realJwtManager = new JwtManager();
        long expiration = 3600000; // 1시간 (ms)
        String token = realJwtManager.generateToken(userId, fingerprint, expiration);

        BDDMockito.given(jwtManager.generateToken(any(), any(), anyLong())).willReturn(token);

        BDDMockito.given(authenticationService.login(any()))
                .willReturn(new AuthenticationInnerResponse(fingerprint, realJwtManager.generateToken(userId, fingerprint, expiration), realJwtManager.generateToken(userId, fingerprint, expiration), true));

        LoginRequest loginRequest = new LoginRequest(fingerprint);

        // when
        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest))
                        .with(csrf())
        )
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.SET_COOKIE, Matchers.containsString("refresh-token")))
                .andExpect(header().string(HttpHeaders.SET_COOKIE, Matchers.containsString("HttpOnly")))
                .andExpect(header().string(HttpHeaders.SET_COOKIE, Matchers.containsString("Path=/")));

        // then
    }

    @Test
    @DisplayName("토큰 refresh - expiration이 Threshold보다 적게 남은 refresh token으로 refresh 요청 시 응답 쿠키에 refresh token 포함")
    void whenRefreshTokenExpiredResponseContainsRefreshToken() throws Exception {
        // given
        String fingerprint = FingerprintGenerator.generate();
        long userId = 1L;
        JwtManager realJwtManager = new JwtManager();
        long accessTokenExpiration = 24 * 3600000; // 1시간 (ms)
        String token = realJwtManager.generateToken(userId, fingerprint, accessTokenExpiration);
        User user = new User(fingerprint);
        user.updateRefreshToken(token);
        Field userIdField = User.class.getDeclaredField("id");
        userIdField.setAccessible(true);
        userIdField.set(user, userId);


        BDDMockito.given(authenticationService.refreshToken(any()))
                .willReturn(new AuthenticationInnerResponse(
                        fingerprint,
                        realJwtManager.generateToken(userId, fingerprint, accessTokenExpiration),
                        realJwtManager.generateToken(userId, fingerprint, accessTokenExpiration),
                        true)
                );

        // when
        mockMvc.perform(post("/auth/refresh")
                        .cookie(new Cookie("refresh-token", token))
                .contentType(MediaType.APPLICATION_JSON).with(csrf()))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.SET_COOKIE, Matchers.containsString("refresh-token")))
                .andExpect(header().string(HttpHeaders.SET_COOKIE, Matchers.containsString("HttpOnly")))
                .andExpect(header().string(HttpHeaders.SET_COOKIE, Matchers.containsString("Path=/")));

        // then
    }

    @Test
    @DisplayName("토큰 refresh - expiration이 Threshold보다 많이 남아있어 refresh token이 재발급되지 않은 경우 쿠키에 refresh token 포함되지 않음")
    void whenRefreshTokenNotRefreshedThenResponseCookieNotContainsRefreshToken() throws Exception {
        // given
        // given
        String fingerprint = FingerprintGenerator.generate();
        long userId = 1L;
        JwtManager realJwtManager = new JwtManager();
        long accessTokenExpiration = 24 * 3600000; // 1시간 (ms)
        String token = realJwtManager.generateToken(userId, fingerprint, accessTokenExpiration);
        User user = new User(fingerprint);
        user.updateRefreshToken(token);
        Field userIdField = User.class.getDeclaredField("id");
        userIdField.setAccessible(true);
        userIdField.set(user, userId);


        BDDMockito.given(authenticationService.refreshToken(any()))
                .willReturn(new AuthenticationInnerResponse(
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