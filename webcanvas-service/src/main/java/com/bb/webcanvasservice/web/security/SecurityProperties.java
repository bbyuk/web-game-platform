package com.bb.webcanvasservice.web.security;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

/**
 * security 및 인증 관련 주입 설정 클래스
 * @param secretKey JWT 비밀키
 * @param accessTokenExpiration Access Token 만료시간 - 15분 (ms)
 * @param refreshTokenExpiration Refresh Token 만료시간 - 14일 (ms)
 * @param refreshTokenReissueThreshold Refresh Token 재발급 임계 시간 - 3일 (72시간) (ms)
 *                                     현재 시각부터 입력받은 refresh token의 expiration time 까지 남은 시간이 refreshTokenThreshold보다 작을 경우 Refresh Token 재발급 및 rotate
 * @param whiteList Security 예외 목록
 * @param cookie 인증 관련 쿠키 name 리스트
 */
@ConfigurationProperties(prefix = "application.security")
public record SecurityProperties(

        String secretKey,

        long accessTokenExpiration,

        long refreshTokenExpiration,

        long refreshTokenReissueThreshold,

        List<String> whiteList,

        AuthenticationCookies cookie
) {
    public SecurityProperties {
        if (accessTokenExpiration == 0) accessTokenExpiration = 900;
        if (refreshTokenExpiration == 0) refreshTokenExpiration = 1209600;
        if (refreshTokenReissueThreshold == 0) refreshTokenReissueThreshold = 259200;
        if (whiteList == null) whiteList = new ArrayList<>();
    }

    public long accessTokenExpiration() {
        return accessTokenExpiration * 1000;
    }

    public long accessTokenExpirationSeconds() {
        return accessTokenExpiration;
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

    public record AuthenticationCookies(String refreshToken) {}
}
