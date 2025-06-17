package com.bb.webcanvasservice.user.application.adapter.game;

import com.bb.webcanvasservice.game.application.port.UserCommandPort;
import com.bb.webcanvasservice.user.application.service.UserApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * game -> 유저 커맨드 포트 어댑터
 */
@Service
@RequiredArgsConstructor
public class GameUserCommandAdapter implements UserCommandPort {
    private final UserApplicationService userApplicationService;

    @Override
    public void moveUsersToGameSession(List<Long> userIds) {
        userApplicationService.moveUsersToGameSession(userIds);
    }

    @Override
    public void moveUsersToRoom(List<Long> userIds) {
        userApplicationService.moveUsersToRoom(userIds);
    }

    @Override
    public void moveUserToRoom(Long userId) {
        userApplicationService.moveUserToRoom(userId);
    }

    @Override
    public void moveUserToLobby(Long userId) {
        userApplicationService.moveUserToLobby(userId);
    }
}
