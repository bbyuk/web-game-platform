package com.bb.webcanvasservice.auth.presentation.controller;

import com.bb.webcanvasservice.auth.application.service.AuthenticationService;
import com.bb.webcanvasservice.auth.presentation.response.AuthenticationInnerResponse;
import com.bb.webcanvasservice.infrastructure.security.http.Authenticated;
import com.bb.webcanvasservice.infrastructure.security.http.WebCanvasAuthentication;
import com.bb.webcanvasservice.auth.presentation.mapper.AuthenticationCommandMapper;
import com.bb.webcanvasservice.auth.presentation.mapper.AuthenticationPresentationDtoMapper;
import com.bb.webcanvasservice.auth.presentation.request.LoginRequest;
import com.bb.webcanvasservice.auth.presentation.response.AuthenticationApiResponse;
import com.bb.webcanvasservice.auth.presentation.util.AuthenticationCookieManager;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 로그인 및 인증 처리 API의 엔드포인트
 */
@Tag(name = "Authentication API", description = "로그인 및 인증 처리 관련 API")
@RestController
@RequestMapping("auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;
    private final AuthenticationCookieManager authenticationCookieManager;

    /**
     * accessToken으로 인증 처리
     */
    @Operation(summary = "토큰 인증", description = "클라이언트의 토큰을 받아 인증 체크 후 userId를 리턴한다.")
    @GetMapping("authentication")
    public ResponseEntity<Long> authenticateWithAccessToken(@Authenticated WebCanvasAuthentication authentication) {
        return ResponseEntity.ok(authentication.getUserId());
    }

    /**
     * 로그인
     * <p>
     * 로그인 처리 후 발급된 토큰을 리턴한다.
     *
     * @param loginRequest
     * @return
     */
    @Operation(summary = "로그인", description = "로그인 처리 후 발급된 토큰을 리턴한다.")
    @PostMapping("login")
    public ResponseEntity<AuthenticationApiResponse> login(@RequestBody LoginRequest loginRequest) {
        AuthenticationInnerResponse authenticationInnerResponse = AuthenticationPresentationDtoMapper
                .toAuthenticationInnerResponse(
                        authenticationService.login(
                                AuthenticationCommandMapper
                                        .toLoginCommand(loginRequest)
                        )
                );

        return ResponseEntity.ok()
                .header(
                        HttpHeaders.SET_COOKIE,
                        authenticationCookieManager.getRefreshTokenResponseCookie(authenticationInnerResponse.refreshToken()).toString()
                )
                .body(AuthenticationPresentationDtoMapper.toAuthenticationApiResponse(authenticationInnerResponse));
    }


    /**
     * 토큰 refresh
     * <p>
     * 로그인 시 accessToken과 함께 발급받은 refreshToken으로 토큰을 refresh한다.
     *
     * @param request
     * @return
     */
    @Operation(summary = "토큰 refresh", description = "로그인 시 access token과 함께 발급받은 refresh token으로 토큰을 refresh한다.")
    @PostMapping("refresh")
    public ResponseEntity<AuthenticationApiResponse> refreshToken(HttpServletRequest request) {
        String refreshTokenFromClient = authenticationCookieManager.parseRefreshCookieFrom(request);
        AuthenticationInnerResponse authenticationInnerResponse = AuthenticationPresentationDtoMapper.toAuthenticationInnerResponse(
                authenticationService.refreshToken(refreshTokenFromClient)
        );
        AuthenticationApiResponse authenticationApiResponse = AuthenticationPresentationDtoMapper.toAuthenticationApiResponse(authenticationInnerResponse);

        ResponseEntity.BodyBuilder responseBuilder = ResponseEntity.ok();

        if (authenticationInnerResponse.refreshTokenReissued()) {
            String refreshCookie = authenticationCookieManager
                    .getRefreshTokenResponseCookie(authenticationInnerResponse.refreshToken())
                    .toString();

            responseBuilder.header(HttpHeaders.SET_COOKIE, refreshCookie);
        }

        return responseBuilder.body(authenticationApiResponse);
    }
}
