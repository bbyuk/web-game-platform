package com.bb.webcanvasservice.user.domain.view;

import com.bb.webcanvasservice.user.domain.model.UserState;

import java.util.Objects;

/**
 * 타 도메인 포트에서 사용할 UserInfo View 모델
 */
public record UserInfo(
        /**
         * 유저 식별자
         */
        Long id,

        /**
         * 서버에서 유저 등록시 생성된 유저 fingerprint
         */
        String fingerprint,

        /**
         * 발급된 accessToken이 만료될 경우 refresh를 위한 토큰
         */
        String refreshToken,

        /**
         * 유저 상태
         */
        UserState state
) {
    public static UserInfo ofNull() {
        return new UserInfo(null, null, null, null);
    }


}
