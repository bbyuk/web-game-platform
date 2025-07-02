package com.bb.webcanvasservice.infrastructure.security.http;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Principal을 WebCanvasAuthentication 객체로 형변환 시 인증 정보 체크 Argument Resolver 적용을 위한 어노테이션
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface Authenticated {
}
