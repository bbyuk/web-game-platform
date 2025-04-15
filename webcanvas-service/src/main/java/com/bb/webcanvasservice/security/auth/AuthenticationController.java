package com.bb.webcanvasservice.security.auth;

import com.bb.webcanvasservice.common.code.ErrorCode;
import com.bb.webcanvasservice.security.SecurityProperties;
import com.bb.webcanvasservice.security.auth.dto.request.LoginRequest;
import com.bb.webcanvasservice.security.auth.dto.response.AuthenticationApiResponse;
import com.bb.webcanvasservice.security.auth.dto.response.AuthenticationInnerResponse;
import com.bb.webcanvasservice.security.exception.ApplicationAuthenticationException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;

/**
 * 로그인 및 인증 처리 API의 엔드포인트
 */
@Tag(name = "Authentication API", description = "로그인 및 인증 처리 관련 API")
@RestController
@RequestMapping("auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;
    private final SecurityProperties securityProperties;

    private ResponseCookie getResponseCookie(String token) {
        return ResponseCookie.from("refresh-token", token)
                .httpOnly(true)
                .path("/")
                .maxAge(securityProperties.refreshTokenExpiration())
                .sameSite("Strict")
                .build();
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
        AuthenticationInnerResponse authenticationInnerResponse = authenticationService.login(loginRequest.fingerprint());
        ResponseCookie refreshTokenResponseCookie = getResponseCookie(authenticationInnerResponse.refreshToken());

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, refreshTokenResponseCookie.toString())
                .body(new AuthenticationApiResponse(authenticationInnerResponse.fingerprint(), authenticationInnerResponse.accessToken()));
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
        Cookie refreshTokenRequestCookie = Arrays.stream(request.getCookies())
                .filter(cookie -> cookie.getName().equals("refresh-token"))
                .findFirst()
                .orElseThrow(() -> new ApplicationAuthenticationException(ErrorCode.REFRESH_TOKEN_NOT_FOUND));

        AuthenticationInnerResponse authenticationInnerResponse = authenticationService.refreshToken(refreshTokenRequestCookie.getValue());

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, authenticationInnerResponse.refreshTokenReissued()
                        ? getResponseCookie(authenticationInnerResponse.refreshToken()).toString()
                        : null
                )
                .body(new AuthenticationApiResponse(authenticationInnerResponse.fingerprint(), authenticationInnerResponse.accessToken()));
    }
}
