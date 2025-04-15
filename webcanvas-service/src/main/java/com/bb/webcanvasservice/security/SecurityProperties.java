package com.bb.webcanvasservice.security;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * security 및 인증 관련 주입 설정 클래스
 * @param accessTokenExpiration
 * @param refreshTokenExpiration
 * @param refreshTokenReissueThreshold
 */
@ConfigurationProperties
public record SecurityProperties(
        /**
         * Access Token 만료시간
         * 15분 (ms)
         */
        long accessTokenExpiration,
        /**
         * Refresh Token 만료시간
         * 14일 (ms)
         */
        long refreshTokenExpiration,
        /**
         * Refresh Token 재발급 임계 시간
         * 현재 시각부터 입력받은 refresh token의 expiration time 까지 남은 시간이
         * refreshTokenReissueThreshold보다 작을 경우 Refresh Token 재발급 및 rotate
         * 3일 (72시간) (ms)
         */
        long refreshTokenReissueThreshold
) {
    public SecurityProperties {
        if (accessTokenExpiration == 0) accessTokenExpiration = 900;
        if (refreshTokenExpiration == 0) refreshTokenExpiration = 1209600;
        if (refreshTokenReissueThreshold == 0) refreshTokenReissueThreshold = 259200;
    }

    public long accessTokenExpiration() {
        return accessTokenExpiration * 1000;
    }

    public long refreshTokenExpiration() {
        return refreshTokenExpiration * 1000;
    }

    public long refreshTokenExpirationSeconds() {
        return refreshTokenExpiration;
    }

    public long refreshTokenReissueThreshold() {
        return refreshTokenReissueThreshold * 1000;
    }
}
