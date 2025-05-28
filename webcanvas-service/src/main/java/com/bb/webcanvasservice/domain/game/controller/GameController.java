package com.bb.webcanvasservice.domain.game.controller;

import com.bb.webcanvasservice.common.security.Authenticated;
import com.bb.webcanvasservice.common.security.WebCanvasAuthentication;
import com.bb.webcanvasservice.domain.chat.service.ChatService;
import com.bb.webcanvasservice.domain.game.dto.request.GameStartRequest;
import com.bb.webcanvasservice.domain.game.dto.response.GameStartResponse;
import com.bb.webcanvasservice.domain.game.service.GameService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Game API", description = "게임 플레이 관련 API")
@RestController
@RequestMapping("game/canvas")
@RequiredArgsConstructor
public class GameController {

    private final GameService gameService;
    private final ChatService chatService;


    @PostMapping("session")
    @Operation(summary = "게임 시작", description = "게임을 시작한다. 게임 시작 서비스 메소드 실행 후 이벤트 리스너에 의해 서버 주도로 게임 세션이 진행된다.")
    public ResponseEntity<GameStartResponse> startGame(@RequestBody GameStartRequest request, @Authenticated WebCanvasAuthentication authentication) {
        return ResponseEntity.ok(new GameStartResponse(gameService.startGame(request, authentication.getUserId())));
    }

}
