package com.bb.webcanvasservice.small.game.stub.service;

import com.bb.webcanvasservice.game.domain.port.user.GameUserQueryPort;

public class GameUserQueryPortStub implements GameUserQueryPort {

    /**
     * 입장 가능한 상태로
     * @param userId
     * @return
     */
    @Override
    public boolean userCanJoin(Long userId) {
        return true;
    }
}
