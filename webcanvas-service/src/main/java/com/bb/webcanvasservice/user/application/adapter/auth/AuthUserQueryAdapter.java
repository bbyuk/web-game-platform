package com.bb.webcanvasservice.user.application.adapter.auth;

import com.bb.webcanvasservice.auth.application.port.UserQueryPort;
import com.bb.webcanvasservice.user.domain.exception.UserNotFoundException;
import com.bb.webcanvasservice.user.domain.mapper.UserModelViewMapper;
import com.bb.webcanvasservice.user.application.repository.UserRepository;
import com.bb.webcanvasservice.user.domain.view.UserInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * auth 유저 조회 포트 어댑터
 */
@Service
@RequiredArgsConstructor
public class AuthUserQueryAdapter implements UserQueryPort {

    private final UserRepository userRepository;

    @Override
    public UserInfo findUserInfoWith(String fingerprint) {
        return userRepository.findByFingerprint(fingerprint).map(UserModelViewMapper::toView).orElseGet(null);
    }

    @Override
    public UserInfo findUserInfoWith(Long userId) {
        return UserModelViewMapper.toView(userRepository.findById(userId).orElseThrow(UserNotFoundException::new));
    }
}
