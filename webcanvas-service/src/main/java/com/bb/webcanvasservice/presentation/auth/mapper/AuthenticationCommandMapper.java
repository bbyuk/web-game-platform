package com.bb.webcanvasservice.presentation.auth.mapper;

import com.bb.webcanvasservice.application.auth.command.LoginCommand;
import com.bb.webcanvasservice.presentation.auth.request.LoginRequest;

/**
 * presentation layer -> application layer command mapper
 * Auth
 */
public class AuthenticationCommandMapper {

    public static LoginCommand toLoginCommand(LoginRequest loginRequest) {
        return new LoginCommand(loginRequest.fingerprint());
    }
}
