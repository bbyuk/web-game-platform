package com.bb.webcanvasservice.security;

import com.bb.webcanvasservice.security.exception.NotAuthenticatedException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.security.Principal;

/**
 * Security 처리에 관한 공통 로직을 관리하는 유틸 클래스
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SecurityUtils {


    public static Long getUserIdFromPrincipal(Principal principal) {
        if (principal instanceof WebCanvasAuthentication authentication) {
            return authentication.getUserId();
        }

        throw new NotAuthenticatedException("인증 정보를 찾을 수 없습니다.");
    }

}
