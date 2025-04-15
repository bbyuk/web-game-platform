package com.bb.webcanvasservice.domain.game;

import com.bb.webcanvasservice.domain.game.dto.response.GameRoomEntranceInfoResponse;
import com.bb.webcanvasservice.domain.game.dto.response.GameRoomEntranceResponse;
import com.bb.webcanvasservice.domain.game.dto.response.GameRoomListResponse;
import com.bb.webcanvasservice.security.auth.Authenticated;
import com.bb.webcanvasservice.security.auth.WebCanvasAuthentication;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


/**
 * 게임 진행, 게임 방 등 게임 처리 API 엔드포인트
 */
@Tag(name = "Game API", description = "게임 방 및 게임 세션 진행 관련 API")
@RestController
@RequestMapping("game/canvas")
@RequiredArgsConstructor
public class GameController {

    private final GameRoomService gameRoomService;


    /**
     * 게임 방 생성
     * <p>
     * 방을 생성하고 입장한다.
     * @param authentication 유저 인증 정보
     * @return
     */
    @PostMapping("room")
    @Operation(summary = "게임 방 생성", description = "게임 방을 생성하고 입장한다.")
    public ResponseEntity<GameRoomEntranceResponse> createGameRoom(@Authenticated WebCanvasAuthentication authentication) {
        return ResponseEntity.ok(gameRoomService.createGameRoomAndEnter(authentication.getUserId()));
    }

    /**
     * 입장 가능한 방 목록 조회
     * <p>
     * 요청을 보낸 유저가 입장할 수 있는 webcanvas-service 방의 목록을 조회한다.
     * @return
     */
    @GetMapping("room")
    @Operation(summary = "입장 가능한 방 목록 조회", description = "요청을 보낸 유저가 입장할 수 있는 webcanvas-service 방의 목록을 조회한다.")
    public ResponseEntity<GameRoomListResponse> getEnterableGameRooms(@Authenticated WebCanvasAuthentication authentication) {
        return ResponseEntity.ok(gameRoomService.findEnterableGameRooms(authentication.getUserId()));
    }

    /**
     * 현재 입장한 게임 방 조회
     *
     * 요청을 보낸 유저가 입장한 게임 방과 입장 정보를 조회한다.
     *
     */
    @Operation(summary = "현재 입장한 게임 방 조회", description = "요청을 보낸 유저가 입장한 게임 방과 입장 정보를 조회한다.")
    @GetMapping("room/entrance")
    public ResponseEntity<GameRoomEntranceInfoResponse> getEnteredGameRoom(@Authenticated WebCanvasAuthentication authentication) {
        return ResponseEntity.ok(gameRoomService.findEnteredGameRoomInfo(authentication.getUserId()));
    }


    /**
     * 게임 방 입장
     *
     * 요청을 보낸 유저를 대상 게임 방에 입장시킨다.
     *
     * @param gameRoomId
     * @param authentication
     * @return
     */
    @PostMapping("room/{gameRoomId}/entrance")
    @Operation(summary = "게임 방 입장", description = "입장 요청을 보낸 유저를 대상 게임 방에 입장시킨다.")
    public ResponseEntity<GameRoomEntranceResponse> enterGameRoom(@PathVariable("gameRoomId") Long gameRoomId, @Authenticated WebCanvasAuthentication authentication) {
        return ResponseEntity.ok(gameRoomService.enterGameRoom(gameRoomId, authentication.getUserId()));
    }

    /**
     * joinCode로 게임 방 입장
     *
     * 요청을 보낸 유저를 대상 게임 방에 입장시킨다.
     *
     * @param joinCode
     * @param authentication
     * @return
     */
    @PostMapping("room/{joinCode}/enterance")
    @Operation(summary = "Join Code로 게임 방 입장", description = "Join Code로 입장 요청읇 보낸 유저를 대상 게임 방에 입장시킨다.")
    public ResponseEntity<GameRoomEntranceResponse> enterGameRoomWithJoinCode(@PathVariable("joinCode") String joinCode,
                                                                              @Authenticated WebCanvasAuthentication authentication) {
        return ResponseEntity.ok(gameRoomService.enterGameRoomWithJoinCode(joinCode, authentication.getUserId()));
    }

}
