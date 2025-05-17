package com.bb.webcanvasservice.unit.util;

import com.bb.webcanvasservice.web.security.SecurityProperties;
import com.bb.webcanvasservice.common.JwtManager;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("[unit] [util] JWT 처리 로직 단위 테스트")
class JwtManagerTest {

    @Mock
    private SecurityProperties securityProperties;

    @Test
    @DisplayName("userId, fingerprint로 토큰 만들기 - 토큰으로부터 userId, fingerprint 파싱")
    void generateTokenAndGetUserIdFromToken() {
        when(securityProperties.secretKey()).thenReturn("doqwijsakdjoisajdksaodijqwdiqjxdxccxxzczxsaoijwqd");

        // given
        Long userId = 1L;
        String fingerprint = "abcdefg";
        long expiration = 3600000; // 1시간 (ms)


        JwtManager jwtManager = new JwtManager(securityProperties);


        // when
        String token = jwtManager.generateToken(userId, fingerprint, expiration);

        // then
        Assertions.assertThat(token).isNotBlank();
        Assertions.assertThat(jwtManager.getUserIdFromToken(token)).isEqualTo(userId);
        Assertions.assertThat(jwtManager.getFingerprintFromToken(token)).isEqualTo(fingerprint);
    }

}