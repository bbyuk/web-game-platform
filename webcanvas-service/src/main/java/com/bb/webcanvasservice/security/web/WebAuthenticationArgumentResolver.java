package com.bb.webcanvasservice.security.web;

import com.bb.webcanvasservice.security.auth.Authenticated;
import com.bb.webcanvasservice.security.auth.WebCanvasAuthentication;
import com.bb.webcanvasservice.security.exception.NotAuthenticatedException;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.security.Principal;

@Component
public class WebAuthenticationArgumentResolver implements HandlerMethodArgumentResolver {
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterAnnotation(Authenticated.class) != null &&
                WebCanvasAuthentication.class.isAssignableFrom(parameter.getParameterType());
    }

    @Override
    public Object resolveArgument(MethodParameter parameter,
                                  ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest,
                                  WebDataBinderFactory binderFactory) {
        Principal principal = webRequest.getUserPrincipal();
        if (principal instanceof WebCanvasAuthentication authentication) {
            return authentication;
        }
        throw new NotAuthenticatedException("유저 인증 정보를 찾을 수 없습니다.");
    }
}