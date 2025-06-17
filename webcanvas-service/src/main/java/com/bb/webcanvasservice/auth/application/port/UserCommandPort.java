package com.bb.webcanvasservice.auth.application.port;

import com.bb.webcanvasservice.user.domain.view.UserInfo;

/**
 * 유저 도메인 명령 포트
 */
public interface UserCommandPort {
    /**
     * 유저 생성 command
     * @param fingerprint 요청받은 fingerprint
     * @return 생성된 유저 정보
     */
    UserInfo createUser(String fingerprint);

    /**
     * 유저 리프레쉬 토큰을 업데이트한다.
     * @param userId
     * @param refreshToken
     */
    void updateUserRefreshToken(Long userId, String refreshToken);
}
