package com.bb.webcanvasservice.domain.game;

import com.bb.webcanvasservice.security.auth.Authenticated;
import com.bb.webcanvasservice.security.auth.WebCanvasAuthentication;
import com.bb.webcanvasservice.security.exception.NotAuthenticatedException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;


/**
 * 게임 진행, 게임 방 등 게임 처리 API 엔드포인트
 */
@RestController
@RequestMapping("game/canvas")
@RequiredArgsConstructor
public class GameController {

    private final GameRoomService gameRoomService;


    /**
     * 방 만들기
     * <p>
     * 방을 생성하고 입장한다.
     * @param authentication 유저 인증 정보
     * @return
     */
    @PostMapping("room")
    public ResponseEntity<GameRoomCreateResponse> createGameRoom(@Authenticated WebCanvasAuthentication authentication) {
        return ResponseEntity.ok(gameRoomService.createGameRoomAndEnter(authentication.getUserId()));
    }

}
