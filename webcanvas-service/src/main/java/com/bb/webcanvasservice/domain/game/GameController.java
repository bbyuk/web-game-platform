package com.bb.webcanvasservice.domain.game;

import com.bb.webcanvasservice.domain.game.dto.request.GameRoomEntranceRequest;
import com.bb.webcanvasservice.domain.game.dto.response.GameRoomCreateResponse;
import com.bb.webcanvasservice.domain.game.dto.response.GameRoomEntranceResponse;
import com.bb.webcanvasservice.domain.game.dto.response.GameRoomListResponse;
import com.bb.webcanvasservice.security.auth.Authenticated;
import com.bb.webcanvasservice.security.auth.WebCanvasAuthentication;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


/**
 * 게임 진행, 게임 방 등 게임 처리 API 엔드포인트
 */
@Slf4j
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

    /**
     * 입장 가능한 방 목록 조회
     * <p>
     * 요청을 보낸 유저가 입장할 수 있는 webcanvas-service 방의 목록을 조회한다.
     * @return
     */
    @GetMapping("room")
    public ResponseEntity<GameRoomListResponse> getEnterableGameRooms(@Authenticated WebCanvasAuthentication authentication) {
        return ResponseEntity.ok(gameRoomService.findEnterableGameRooms(authentication.getUserId()));
    }

    /**
     * 게임 방 입장
     *
     * 요청을 보낸 유저를 대상 게임 방에 입장시킨다.
     * @param entranceRequest
     * @param authentication
     * @return
     */
    @PostMapping("room/enterance")
    public ResponseEntity<GameRoomEntranceResponse> enterGameRoom(@RequestBody GameRoomEntranceRequest entranceRequest,
                                                                  @Authenticated WebCanvasAuthentication authentication) {
        return ResponseEntity.ok(new GameRoomEntranceResponse(null, null, null));
    }

}
