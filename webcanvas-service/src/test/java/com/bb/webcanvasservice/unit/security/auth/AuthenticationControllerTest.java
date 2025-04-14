package com.bb.webcanvasservice.unit.security.auth;

import com.bb.webcanvasservice.common.FingerprintGenerator;
import com.bb.webcanvasservice.security.auth.*;
import com.bb.webcanvasservice.security.auth.dto.request.LoginRequest;
import com.bb.webcanvasservice.security.auth.dto.response.AuthenticationApiResponse;
import com.bb.webcanvasservice.security.auth.dto.response.AuthenticationInnerResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        controllers = {AuthenticationController.class},
        excludeAutoConfiguration = {SecurityAutoConfiguration.class}
)
class AuthenticationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private JwtManager jwtManager;

    @MockitoBean
    private AuthenticationService authenticationService;

    @Test
    @DisplayName("로그인 API - 인증 토큰 발급")
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
        mockMvc.perform(MockMvcRequestBuilders.post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest))
                .with(csrf())
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").exists());

        // then
    }

}