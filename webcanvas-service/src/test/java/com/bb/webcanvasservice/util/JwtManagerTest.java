package com.bb.webcanvasservice.util;

import com.bb.webcanvasservice.security.auth.JwtManager;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class JwtManagerTest {

    private JwtManager jwtManager = new JwtManager();

    @Test
    @DisplayName("userId, fingerprint로 토큰 만들기 - 토큰으로부터 userId, fingerprint 파싱")
    void generateTokenAndGetUserIdFromToken() {
        // given
        Long userId = 1L;
        String fingerprint = "abcdefg";

        // when
        String token = jwtManager.generateToken(userId, fingerprint);

        // then
        Assertions.assertThat(token).isNotBlank();
        Assertions.assertThat(jwtManager.getUserIdFromToken(token)).isEqualTo(userId);
        Assertions.assertThat(jwtManager.getFingerprintFromToken(token)).isEqualTo(fingerprint);
    }

}