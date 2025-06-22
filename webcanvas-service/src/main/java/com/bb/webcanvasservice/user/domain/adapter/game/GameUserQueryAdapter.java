package com.bb.webcanvasservice.user.domain.adapter.game;

import com.bb.webcanvasservice.game.domain.port.user.GameUserQueryPort;
import com.bb.webcanvasservice.user.domain.exception.UserNotFoundException;
import com.bb.webcanvasservice.user.domain.repository.UserRepository;

/**
 * game -> user 조회 포트 어댑터
 */
public class GameUserQueryAdapter implements GameUserQueryPort {

    private final UserRepository userRepository;

    public GameUserQueryAdapter(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public boolean userCanJoin(Long userId) {
        return userRepository
                .findById(userId).orElseThrow(UserNotFoundException::new)
                .canJoin();
    }
}
