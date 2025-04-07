package com.bb.webcanvasservice.security.auth;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 로그인 및 인증 처리 API의 엔드포인트
 */
@Tag(name = "Authentication API", description = "로그인 및 인증 처리 관련 API")
@RestController
@RequestMapping("auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;


    /**
     * 로그인
     *
     * 로그인 처리 후 발급된 토큰을 리턴한다.
     * @param loginRequest
     * @return
     */
    @Operation(summary = "로그인", description = "로그인 처리 후 발급된 토큰을 리턴한다.")
    @PostMapping("login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
        return ResponseEntity.ok(authenticationService.login(loginRequest.fingerprint()));
    }
}
