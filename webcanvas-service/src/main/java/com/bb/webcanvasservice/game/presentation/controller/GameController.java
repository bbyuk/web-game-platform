package com.bb.webcanvasservice.game.presentation.controller;

import com.bb.webcanvasservice.infrastructure.security.http.Authenticated;
import com.bb.webcanvasservice.infrastructure.security.http.WebCanvasAuthentication;
import com.bb.webcanvasservice.game.application.service.GameService;
import com.bb.webcanvasservice.game.presentation.mapper.GameCommandMapper;
import com.bb.webcanvasservice.game.presentation.mapper.GamePresentationDtoMapper;
import com.bb.webcanvasservice.game.presentation.request.GameStartRequest;
import com.bb.webcanvasservice.game.presentation.response.GameTurnResponse;
import com.bb.webcanvasservice.game.presentation.response.GameSessionResponse;
import com.bb.webcanvasservice.game.presentation.response.GameStartResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Game API", description = "게임 플레이 관련 API")
@RestController
@RequestMapping("game/canvas")
@RequiredArgsConstructor
public class GameController {

    private final GameService gameService;
    @PostMapping("session")
    @Operation(summary = "게임 시작", description = "게임을 시작한다. 게임 시작 서비스 메소드 실행 후 이벤트 리스너에 의해 서버 주도로 게임 세션이 진행된다.")
    public ResponseEntity<GameStartResponse> startGame(@RequestBody GameStartRequest request, @Authenticated WebCanvasAuthentication authentication) {
        return ResponseEntity.ok(
                new GameStartResponse(
                        gameService.loadGameSession(
                                GameCommandMapper.toStartGameCommand(request.gameRoomId(), request.turnCount(), request.timePerTurn(), authentication.getUserId())
                        )
                )
        );
    }

    @GetMapping("session/{gameSessionId}/turn")
    @Operation(summary = "게임 턴 조회", description = "현재 진행중인 게임 턴을 조회한다.")
    public ResponseEntity<GameTurnResponse> findCurrentGameTurn(@PathVariable("gameSessionId") Long gameSessionId, @Authenticated WebCanvasAuthentication authentication) {
        return ResponseEntity.ok(
                GamePresentationDtoMapper.toGameTurnResponse(gameService.findCurrentGameTurn(gameSessionId, authentication.getUserId()))
        );
    }

    @GetMapping("room/{gameRoomId}/session")
    @Operation(summary = "게임 세션 조회", description = "현재 진행중인 게임 세션을 조회한다.")
    public ResponseEntity<GameSessionResponse> findCurrentGameSession(@PathVariable("gameRoomId") Long gameRoomId) {
        return ResponseEntity.ok(
                GamePresentationDtoMapper.toGameSessionResponse(gameService.findCurrentGameSession(gameRoomId))
        );
    }
}
