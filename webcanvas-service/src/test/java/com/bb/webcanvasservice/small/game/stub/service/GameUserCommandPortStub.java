package com.bb.webcanvasservice.small.game.stub.service;

import com.bb.webcanvasservice.game.application.port.user.UserCommandPort;

import java.util.List;

public class GameUserCommandPortStub implements UserCommandPort {
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
}
