package com.bb.webcanvasservice.auth.domain.port;

import com.bb.webcanvasservice.user.domain.view.UserInfo;

import java.util.Optional;

/**
 * 유저 도메인 조회 포트
 */
public interface AuthUserQueryPort {
    /**
     * fingerprint로 유저 정보를 찾는다.
     * fingerprint로 찾지 못했을 경우 콜백 로직을 클라이언트쪽에서 구현해야 하므로 Optional로 감싸 리턴한다.
     * @param fingerprint 대상 유저 fingerprint
     * @return 유저 정보 view
     */
    Optional<UserInfo> findUserInfoWith(String fingerprint);

    /**
     * 유저 ID로 유저 정보를 찾는다.
     * @param userId 대상 유저 ID
     * @return 유저 정보 view
     */
    Optional<UserInfo> findUserInfoWith(Long userId);
}
