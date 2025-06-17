package com.bb.webcanvasservice.user.application.service;

import com.bb.webcanvasservice.user.application.mapper.UserApplicationDtoMapper;
import com.bb.webcanvasservice.user.application.dto.UserDto;
import com.bb.webcanvasservice.user.application.dto.UserStateDto;
import com.bb.webcanvasservice.user.domain.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 게임 유저에 대한 비즈니스 로직을 처리하는 서비스 클래스
 */
@Service
@RequiredArgsConstructor
public class UserApplicationService {

    private final UserService userService;

    /**
     * 클라이언트 fingerprint로 등록된 유저를 조회 후 없을 시 유저 생성 후 리턴
     *
     * @param fingerprint
     * @return
     */
    @Transactional
    public UserDto findOrCreateUser(String fingerprint) {
        return UserApplicationDtoMapper.toUserDto(userService.findOrCreateUser(fingerprint));
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
        return UserApplicationDtoMapper.toUserDto(userService.findUser(userId));
    }

    /**
     * 생성된 fingerprint로 유저를 생성하고 저장 후 리턴한다.
     *
     * @param fingerprint 서버에서 생성된 유저 fingerprint
     * @return 유저 Application DTO
     */
    @Transactional
    public UserDto createUser(String fingerprint) {
        return UserApplicationDtoMapper.toUserDto(userService.createUser(fingerprint));
    }

    /**
     * 유저 ID로 유저 상태 정보를 조회한다.
     * @param userId 유저 ID
     * @return 유저 상태 Application DTO
     */
    @Transactional(readOnly = true)
    public UserStateDto findUserState(Long userId) {
        return UserApplicationDtoMapper.toUserStateDto(userService.findUserState(userId));
    }

}
