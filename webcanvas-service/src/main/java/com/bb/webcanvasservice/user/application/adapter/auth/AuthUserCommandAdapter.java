package com.bb.webcanvasservice.user.application.adapter.auth;

import com.bb.webcanvasservice.auth.application.port.UserCommandPort;
import com.bb.webcanvasservice.user.application.dto.UserDto;
import com.bb.webcanvasservice.user.application.service.UserApplicationService;
import com.bb.webcanvasservice.user.domain.exception.UserNotFoundException;
import com.bb.webcanvasservice.user.domain.mapper.UserModelViewMapper;
import com.bb.webcanvasservice.user.application.repository.UserRepository;
import com.bb.webcanvasservice.user.domain.view.UserInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * auth 유저 명령 포트 어댑터
 */
@Service
@RequiredArgsConstructor
public class AuthUserCommandAdapter implements UserCommandPort {

    private final UserApplicationService userApplicationService;
    private final UserRepository userRepository;

    @Override
    public UserInfo createUser(String fingerprint) {
        UserDto userDto = userApplicationService.createUser(fingerprint);
        return UserModelViewMapper.toView(
                userRepository.findById(userDto.userId()).orElseThrow(UserNotFoundException::new)
        );
    }

    @Override
    public void updateUserRefreshToken(Long userId, String refreshToken) {
        userApplicationService.updateRefreshToken(userId, refreshToken);
    }
}
