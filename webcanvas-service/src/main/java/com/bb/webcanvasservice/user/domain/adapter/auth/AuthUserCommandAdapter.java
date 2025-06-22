package com.bb.webcanvasservice.user.domain.adapter.auth;

import com.bb.webcanvasservice.auth.domain.port.AuthUserCommandPort;
import com.bb.webcanvasservice.user.domain.exception.AlreadyRegisteredUserException;
import com.bb.webcanvasservice.user.domain.exception.UserNotFoundException;
import com.bb.webcanvasservice.user.domain.mapper.UserModelViewMapper;
import com.bb.webcanvasservice.user.domain.model.User;
import com.bb.webcanvasservice.user.domain.repository.UserRepository;
import com.bb.webcanvasservice.user.domain.view.UserInfo;

/**
 * auth 유저 명령 포트 어댑터
 */
public class AuthUserCommandAdapter implements AuthUserCommandPort {

    private final UserRepository userRepository;

    public AuthUserCommandAdapter(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserInfo createUser(String fingerprint) {
        userRepository.findByFingerprint(fingerprint)
                .ifPresent(
                        user -> {
                            throw new AlreadyRegisteredUserException();
                        }
                );
        User newUser = userRepository.save(User.create(fingerprint));

        return UserModelViewMapper.toView(newUser);
    }

    @Override
    public void updateUserRefreshToken(Long userId, String refreshToken) {
        User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
        user.updateRefreshToken(refreshToken);

        userRepository.save(user);
    }
}
