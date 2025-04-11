package com.bb.webcanvasservice.security.auth;

import com.bb.webcanvasservice.common.FingerprintGenerator;
import com.bb.webcanvasservice.domain.user.User;
import com.bb.webcanvasservice.domain.user.UserService;
import com.bb.webcanvasservice.security.auth.dto.response.LoginResponse;
import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 인증, 로그인 처리를 담당하는 서비스
 */
@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final JwtManager jwtManager;
    private final UserService userService;

    /**
     * fingerprint를 username으로 로그인 처리를 수행한다.
     * <p>
     * accessToken과 refreshToken을 새로 발급
     * fingerprint로 등록된 유저가 없을 시 유저 생성 서비스 요청
     * User 엔티티에 refreshToken update
     *
     * @param fingerprint 유저 fingerprint(username)
     * @return (accessToken, refreshToken) 새로 발급받은 accessToken, refreshToken 튜플
     */
    @Transactional
    public LoginResponse login(String fingerprint) {

        User user = userService.findOrCreateUser(
                StringUtils.isBlank(fingerprint)
                        ? FingerprintGenerator.generate()
                        : fingerprint);

        String accessToken = jwtManager.generateToken(user.getId(), user.getFingerprint());
        String refreshToken = jwtManager.generateToken(user.getId(), user.getFingerprint());

        user.updateRefreshToken(refreshToken);

        return new LoginResponse(user.getFingerprint(), accessToken, refreshToken);
    }
}
