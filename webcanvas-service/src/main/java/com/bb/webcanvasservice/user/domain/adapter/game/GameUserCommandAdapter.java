package com.bb.webcanvasservice.user.domain.adapter.game;

import com.bb.webcanvasservice.user.domain.exception.AlreadyJoinedRoomException;
import com.bb.webcanvasservice.game.domain.port.user.GameUserCommandPort;
import com.bb.webcanvasservice.user.domain.exception.UserNotFoundException;
import com.bb.webcanvasservice.user.domain.model.User;
import com.bb.webcanvasservice.user.domain.model.UserState;
import com.bb.webcanvasservice.user.domain.repository.UserRepository;

import java.util.List;

/**
 * game -> 유저 커맨드 포트 어댑터
 */
public class GameUserCommandAdapter implements GameUserCommandPort {

    private final UserRepository userRepository;

    public GameUserCommandAdapter(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void moveUsersToGameSession(List<Long> userIds) {
        userRepository.updateUsersStates(userIds, UserState.IN_GAME);
    }

    @Override
    public void moveUsersToRoom(List<Long> userIds) {
        userRepository.updateUsersStates(userIds, UserState.IN_ROOM);
    }

    @Override
    public void moveUserToRoom(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
        user.moveToRoom();
        userRepository.save(user);
    }

    @Override
    public void moveUserToLobby(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
        user.moveToLobby();
        userRepository.save(user);
    }

    @Override
    public void validateUserCanJoin(Long userId) {
        User user = userRepository
                .findById(userId).orElseThrow(UserNotFoundException::new);

        if (user.state() != UserState.IN_LOBBY) {
            throw new AlreadyJoinedRoomException();
        }
    }
}
