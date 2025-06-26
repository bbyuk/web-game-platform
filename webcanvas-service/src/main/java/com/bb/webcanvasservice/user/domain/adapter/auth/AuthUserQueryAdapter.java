package com.bb.webcanvasservice.user.domain.adapter.auth;

import com.bb.webcanvasservice.auth.domain.port.AuthUserQueryPort;
import com.bb.webcanvasservice.user.domain.exception.UserNotFoundException;
import com.bb.webcanvasservice.user.domain.mapper.UserModelViewMapper;
import com.bb.webcanvasservice.user.domain.repository.UserRepository;
import com.bb.webcanvasservice.user.domain.view.UserInfo;

import java.util.Optional;

/**
 * auth 유저 조회 포트 어댑터
 */
public class AuthUserQueryAdapter implements AuthUserQueryPort {

    private final UserRepository userRepository;

    public AuthUserQueryAdapter(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Optional<UserInfo> findUserInfoWith(String fingerprint) {
        return userRepository.findByFingerprint(fingerprint).map(UserModelViewMapper::toView);
    }

    @Override
    public Optional<UserInfo> findUserInfoWith(Long userId) {
        return userRepository.findById(userId).map(UserModelViewMapper::toView);
    }
}
