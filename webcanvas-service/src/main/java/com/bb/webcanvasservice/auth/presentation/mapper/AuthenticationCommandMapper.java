package com.bb.webcanvasservice.auth.presentation.mapper;

import com.bb.webcanvasservice.auth.application.command.LoginCommand;
import com.bb.webcanvasservice.auth.presentation.request.LoginRequest;

/**
 * presentation layer -> application layer command mapper
 * Auth
 */
public class AuthenticationCommandMapper {

    public static LoginCommand toLoginCommand(LoginRequest loginRequest) {
        return new LoginCommand(loginRequest.fingerprint());
    }
}
