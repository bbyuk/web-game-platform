package com.bb.webcanvasservice.unit.security.auth;

import com.bb.webcanvasservice.security.auth.*;
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

import static org.junit.jupiter.api.Assertions.*;
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

    private JwtManager jwtManager = new JwtManager();

    @MockitoBean
    private AuthenticationService authenticationService;

    @Test
    @DisplayName("로그인 API - 인증 토큰 발급")
    void login() throws Exception {
        // given
        String fingerprint = "3f8d47a3a92b77e5";

        BDDMockito.given(authenticationService.login(fingerprint))
                .willReturn(new LoginResponse(jwtManager.generateToken(1L, fingerprint), jwtManager.generateToken(1L, fingerprint)));

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