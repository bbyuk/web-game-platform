package com.bb.webcanvasservice.small.game.stub.service;

import com.bb.webcanvasservice.game.domain.port.user.GameUserCommandPort;

import java.util.List;

public class GameUserCommandPortStub implements GameUserCommandPort {
    @Override
    public void moveUsersToGameSession(List<Long> userIds) {

    }

    @Override
    public void moveUsersToRoom(List<Long> userIds) {

    }

    @Override
    public void moveUserToRoom(Long userId) {

    }

    @Override
    public void moveUserToLobby(Long userId) {

    }

    @Override
    public void validateUserCanJoin(Long userId) {

    }
}
