package com.bb.webcanvasservice.integration.security.auth;

import com.bb.webcanvasservice.domain.user.User;
import com.bb.webcanvasservice.domain.user.UserRepository;
import com.bb.webcanvasservice.security.auth.AuthenticationService;
import com.bb.webcanvasservice.security.auth.JwtManager;
import com.bb.webcanvasservice.security.auth.dto.response.AuthenticationApiResponse;
import com.bb.webcanvasservice.security.auth.dto.response.AuthenticationInnerResponse;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@SpringBootTest
@DisplayName("[integration] [service] 인증 서비스 통합테스트")
class AuthenticationServiceIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    AuthenticationService authenticationService;

    @Autowired
    JwtManager jwtManager;

    @Test
    @DisplayName("로그인 통합테스트 - fingerprint로 등록된 유저가 있을 경우 새로 발급된 accessToken, refreshToken 리턴 및 유저 refreshToken update")
    void loginSuccessWhenFingerprintRegistered() {
        // given
        String fingerprint = "asdwqujdqwi12j3b1jbsd";
        User savedUser = userRepository.save(new User(fingerprint));

        // when
        AuthenticationInnerResponse authenticationApiResponse = authenticationService.login(fingerprint);
        User findUser = userRepository.findById(savedUser.getId()).get();

        // then
        Assertions.assertThat(jwtManager.getFingerprintFromToken(authenticationApiResponse.accessToken())).isEqualTo(fingerprint);
        Assertions.assertThat(jwtManager.getFingerprintFromToken(authenticationApiResponse.refreshToken())).isEqualTo(fingerprint);

        Assertions.assertThat(findUser.getRefreshToken()).isEqualTo(authenticationApiResponse.refreshToken());
    }

    @Test
    @DisplayName("로그인 통합테스트 - fingerprint로 등록된 유저가 없을 경우에도 새로 발급된 accessToken, refreshToken 리턴 및 유저 refreshToken update")
    void loginSuccessWhenFingerprintNotRegistered() {
        // given
        String fingerprint = "asdwqujdqwi12j3b1jbsd";

        // when
        AuthenticationInnerResponse authenticationApiResponse = authenticationService.login(fingerprint);

        // then
        Assertions.assertThat(jwtManager.getFingerprintFromToken(authenticationApiResponse.accessToken())).isEqualTo(fingerprint);
        Assertions.assertThat(jwtManager.getFingerprintFromToken(authenticationApiResponse.refreshToken())).isEqualTo(fingerprint);


        userRepository.findByFingerprint(fingerprint)
                .ifPresentOrElse(
                        user -> Assertions.assertThat(user.getRefreshToken()).isEqualTo(authenticationApiResponse.refreshToken()),
                        org.junit.jupiter.api.Assertions::fail
                );
    }
}