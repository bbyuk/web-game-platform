package com.bb.webcanvasservice.security.web;

import com.bb.webcanvasservice.security.auth.Authenticated;
import com.bb.webcanvasservice.security.auth.WebCanvasAuthentication;
import com.bb.webcanvasservice.security.exception.ApplicationAuthenticationException;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.security.Principal;

import static com.bb.webcanvasservice.common.code.ErrorCode.AUTH_USER_NOT_FOUND;

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
        throw new ApplicationAuthenticationException(AUTH_USER_NOT_FOUND);
    }
}