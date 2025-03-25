package com.bb.webcanvasservice.domain.game;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GameService {

    private final GameRoomRepository gameRoomRepository;


    public GameRoom findGameRoomIdByUserToken(String userToken) {
    }
}
