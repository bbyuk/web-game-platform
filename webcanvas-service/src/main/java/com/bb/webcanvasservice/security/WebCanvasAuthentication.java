package com.bb.webcanvasservice.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.List;

/**
 * webcanvas-service의 인증 처리를 담당하는 커스텀 Authentication 클래스
 */
public class WebCanvasAuthentication implements Authentication {

    private final Long userId;
    private boolean authenticated;

    /**
     * 기본적으로 객체 생성시 인증된 상태로 설정
     * @param userId
     */
    public WebCanvasAuthentication(Long userId) {
        this.userId = userId;
        this.authenticated = true;
    }

    /**
     * 현재 권한 처리는 하지 않음.
     * @return
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    /**
     * 별도로 credential 처리 하지 않음.
     * @return
     */
    @Override
    public Object getCredentials() {
        return null;
    }

    /**
     * 요청 정보
     * @return
     */
    @Override
    public Object getDetails() {
        return null;
    }

    /**
     * userId를 principal로 한다.
     * @return
     */
    @Override
    public Object getPrincipal() {
        return this.userId;
    }

    @Override
    public boolean isAuthenticated() {
        return this.authenticated;
    }

    @Override
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
        this.authenticated = isAuthenticated;
    }

    @Override
    public String getName() {
        return "";
    }
}
