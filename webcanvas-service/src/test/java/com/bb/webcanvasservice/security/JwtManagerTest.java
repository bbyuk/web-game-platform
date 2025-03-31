package com.bb.webcanvasservice.security;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class JwtManagerTest {

    private JwtManager jwtManager = new JwtManager();

    @Test
    @DisplayName("userId로 토큰 만들기 - 토큰으로부터 userId 파싱")
    void generateTokenAndGetUserIdFromToken() {
        // given
        Long userId = 1L;

        // when
        String token = jwtManager.generateToken(userId);

        // then
        Assertions.assertThat(token).isNotBlank();
        Assertions.assertThat(jwtManager.getUserIdFromToken(token)).isEqualTo(userId);
    }

}