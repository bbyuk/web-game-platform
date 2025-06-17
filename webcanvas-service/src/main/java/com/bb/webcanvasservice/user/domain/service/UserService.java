package com.bb.webcanvasservice.user.domain.service;

import com.bb.webcanvasservice.user.domain.exception.UserNotFoundException;
import com.bb.webcanvasservice.user.domain.model.User;
import com.bb.webcanvasservice.user.domain.model.UserState;
import com.bb.webcanvasservice.user.application.repository.UserRepository;

import java.util.List;

/**
 * 게임 유저에 대한 비즈니스 로직을 처리하는 서비스 클래스
 */
public class UserService {

    private final UserRepository userRepository;
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    /**
     * 유저 상태를 게임 방으로 옮긴다.
     * @param userId 유저 ID
     */
    public void moveUserToRoom(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);
        user.changeState(UserState.IN_ROOM);
        userRepository.save(user);
    }

    /**
     * 유저 상태를 로비로 옮긴다.
     * @param userId 유저 ID
     */
    public void moveUserToLobby(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);
        user.changeState(UserState.IN_LOBBY);
        userRepository.save(user);
    }

    /**
     * 유저 상태를 게임중으로 변경한다.
     * @param userId 유저 ID
     */
    public void moveUserToGameSession(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);
        user.changeState(UserState.IN_GAME);
        userRepository.save(user);
    }

    /**
     * 유저 상태를 조회한다.
     * @param userId
     * @return
     */
    public UserState findUserState(Long userId) {
        return userRepository.findUserState(userId);
    }

    /**
     * 대상 유저들의 상태를 게임 방 내로 변경한다.
     * @param userIds 대상 유저 ID 목록
     */
    public void moveUsersToRoom(List<Long> userIds) {
        userRepository.updateUsersStates(userIds, UserState.IN_ROOM);
    }

    /**
     * 대상 유저들의 상태를 게임 세션 내로 변경한다.
     * @param userIds 대상 유저 ID 목록
     */
    public void moveUsersToGameSession(List<Long> userIds) {
        userRepository.updateUsersStates(userIds, UserState.IN_GAME);
    }
}
