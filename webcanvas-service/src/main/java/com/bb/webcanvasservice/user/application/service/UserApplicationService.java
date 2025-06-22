package com.bb.webcanvasservice.user.application.service;

import com.bb.webcanvasservice.user.application.dto.UserDto;
import com.bb.webcanvasservice.user.application.dto.UserStateDto;
import com.bb.webcanvasservice.user.application.mapper.UserApplicationDtoMapper;
import com.bb.webcanvasservice.user.domain.repository.UserRepository;
import com.bb.webcanvasservice.user.domain.exception.AlreadyRegisteredUserException;
import com.bb.webcanvasservice.user.domain.exception.UserNotFoundException;
import com.bb.webcanvasservice.user.domain.model.User;
import com.bb.webcanvasservice.user.domain.model.UserState;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 게임 유저에 대한 비즈니스 로직을 처리하는 서비스 클래스
 */
@Service
@RequiredArgsConstructor
public class UserApplicationService {

    private final UserRepository userRepository;

    /**
     * 유저의 리프레쉬 토큰을 변경한다.
     * @param userId 대상 유저 ID
     * @param refreshToken 리프레쉬 토큰
     */
    @Transactional
    public void updateRefreshToken(Long userId, String refreshToken) {
        User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
        user.updateRefreshToken(refreshToken);
        userRepository.save(user);
    }

    /**
     * 유저 ID로 유저를 조회해 리턴한다.
     * 찾지 못할 시 UserNotFoundException throw
     *
     * @param userId 유저 ID
     * @return 유저 Application DTO
     */
    @Transactional(readOnly = true)
    public UserDto findUser(Long userId) {
        return UserApplicationDtoMapper.toUserDto(
                userRepository.findById(userId).orElseThrow(UserNotFoundException::new)
        );
    }

    /**
     * 생성된 fingerprint로 유저를 생성하고 저장 후 리턴한다.
     *
     * @param fingerprint 서버에서 생성된 유저 fingerprint
     * @return 유저 Application DTO
     */
    @Transactional
    public UserDto createUser(String fingerprint) {
        userRepository.findByFingerprint(fingerprint)
                .ifPresent(
                        user -> {
                            throw new AlreadyRegisteredUserException();
                        }
                );

        return UserApplicationDtoMapper.toUserDto(userRepository.save(User.create(fingerprint)));
    }

    /**
     * 유저 ID로 유저 상태 정보를 조회한다.
     *
     * @param userId 유저 ID
     * @return 유저 상태 Application DTO
     */
    @Transactional(readOnly = true)
    public UserStateDto findUserState(Long userId) {
        return UserApplicationDtoMapper.toUserStateDto(
                userRepository.findUserState(userId)
        );
    }
}
