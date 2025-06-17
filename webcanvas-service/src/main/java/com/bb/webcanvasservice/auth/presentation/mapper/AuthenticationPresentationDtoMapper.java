package com.bb.webcanvasservice.auth.presentation.mapper;

import com.bb.webcanvasservice.auth.application.dto.LoginSuccessDto;
import com.bb.webcanvasservice.auth.application.dto.TokenRefreshSuccessDto;
import com.bb.webcanvasservice.auth.presentation.response.AuthenticationApiResponse;
import com.bb.webcanvasservice.auth.presentation.response.AuthenticationInnerResponse;

/**
 * Application Layer -> Presentation Layer Dto Mapper
 * Authentication
 */
public class AuthenticationPresentationDtoMapper {

    public static AuthenticationApiResponse toAuthenticationApiResponse(AuthenticationInnerResponse authenticationInnerResponse) {
        return new AuthenticationApiResponse(authenticationInnerResponse.userId(), authenticationInnerResponse.fingerprint(), authenticationInnerResponse.accessToken(), authenticationInnerResponse.refreshTokenReissued());
    }

    public static AuthenticationInnerResponse toAuthenticationInnerResponse(LoginSuccessDto loginSuccessDto) {
        return new AuthenticationInnerResponse(loginSuccessDto.userId(), loginSuccessDto.fingerprint(), loginSuccessDto.accessToken(), loginSuccessDto.refreshToken(), loginSuccessDto.refreshTokenReissued());
    }

    public static AuthenticationInnerResponse toAuthenticationInnerResponse(TokenRefreshSuccessDto tokenRefreshSuccessDto) {
        return new AuthenticationInnerResponse(tokenRefreshSuccessDto.userId(), tokenRefreshSuccessDto.fingerprint(), tokenRefreshSuccessDto.accessToken(), tokenRefreshSuccessDto.refreshToken(), tokenRefreshSuccessDto.refreshTokenReissued());
    }
}
