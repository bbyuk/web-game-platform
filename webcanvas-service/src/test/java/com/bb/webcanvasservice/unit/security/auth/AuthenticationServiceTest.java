package com.bb.webcanvasservice.unit.security.auth;

import com.bb.webcanvasservice.domain.user.User;
import com.bb.webcanvasservice.domain.user.UserService;
import com.bb.webcanvasservice.security.auth.AuthenticationService;
import com.bb.webcanvasservice.security.auth.JwtManager;
import com.bb.webcanvasservice.security.auth.dto.response.AuthenticationResponse;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

    @Mock
    private UserService userService;

    @Mock
    private JwtManager jwtManager;

    @InjectMocks
    private AuthenticationService authenticationService;

    @Test
    @DisplayName("로그인 단위테스트 - fingerprint로 등록되어 있는지 여부와 상관없이 토큰이 발급되어 리턴되어야 한다.")
    void testLogin() throws Exception {
        // given
        String fingerprint = "asdwqujdqwi12j3b1jbsd";
        JwtManager realJwtManagerObject = new JwtManager();

        User user = new User(fingerprint);
        Field idField = User.class.getDeclaredField("id");
        idField.setAccessible(true);
        long userId = 1L;
        idField.set(user, userId);

        long expiration = 3600000; // 1시간 (ms)

        when(userService.findOrCreateUser(any()))
                .thenReturn(user);
        when(jwtManager.generateToken(any(), any(), anyLong()))
                .thenReturn(realJwtManagerObject.generateToken(userId, fingerprint, expiration));

        // when
        AuthenticationResponse authenticationResponse = authenticationService.login(fingerprint);

        String accessToken = authenticationResponse.accessToken();
        String refreshToken = authenticationResponse.refreshToken();

        // then
        Assertions.assertThat(accessToken).isNotBlank();
        Assertions.assertThat(refreshToken).isNotBlank();
    }
}