package com.bb.webcanvasservice.medium.game.application;

import com.bb.webcanvasservice.game.application.command.JoinGameRoomCommand;
import com.bb.webcanvasservice.game.application.command.StartGameCommand;
import com.bb.webcanvasservice.game.application.command.UpdateReadyCommand;
import com.bb.webcanvasservice.game.application.dto.GameRoomJoinDto;
import com.bb.webcanvasservice.game.domain.repository.GameRoomRepository;
import com.bb.webcanvasservice.game.application.service.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * 게임 서비스 테스트 중 멀티스레드 환경 테스트시 미리 데이터 로드를 위한 테스트 컴포넌트
 */
@Component
public class GameServiceTester {

    @Autowired
    private GameService gameService;

    @Autowired
    private GameRoomRepository gameRoomRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public GameStartTestData prepareGameSessionStartTestData(Long hostUserId, Long guestUserId) {
        GameRoomJoinDto gameRoomAndEnter = gameService.createGameRoomAndEnter(hostUserId);
        GameRoomJoinDto gameRoomJoinDto = gameService.joinGameRoom(new JoinGameRoomCommand(gameRoomAndEnter.gameRoomId(), guestUserId));

        gameService.updateReady(new UpdateReadyCommand(gameRoomJoinDto.gameRoomParticipantId(), guestUserId, true));

        Long gameSessionId = gameService.loadGameSession(new StartGameCommand(gameRoomJoinDto.gameRoomId(), 2, 20, hostUserId));

        return new GameStartTestData(gameRoomJoinDto.gameRoomId(), gameSessionId);
    }

    record GameStartTestData(
            Long gameRoomId,
            Long gameSessionId
    ) {}
}
