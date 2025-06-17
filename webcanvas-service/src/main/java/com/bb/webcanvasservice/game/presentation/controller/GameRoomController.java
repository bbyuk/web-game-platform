package com.bb.webcanvasservice.game.presentation.controller;

import com.bb.webcanvasservice.game.application.service.GameRoomApplicationService;
import com.bb.webcanvasservice.common.security.Authenticated;
import com.bb.webcanvasservice.common.security.WebCanvasAuthentication;
import com.bb.webcanvasservice.game.presentation.mapper.GameCommandMapper;
import com.bb.webcanvasservice.game.presentation.mapper.GamePresentationDtoMapper;
import com.bb.webcanvasservice.game.presentation.response.GameRoomEntranceDetailInfoResponse;
import com.bb.webcanvasservice.game.presentation.response.GameRoomEntranceResponse;
import com.bb.webcanvasservice.game.presentation.request.GameRoomReadyUpdateRequest;
import com.bb.webcanvasservice.game.presentation.response.GameRoomExitResponse;
import com.bb.webcanvasservice.game.presentation.response.GameRoomListResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@Tag(name = "Game Room API", description = "게임 방 생성 및 입장 등 게임 방 관련 API")
@RestController
@RequestMapping("game/canvas/room")
@RequiredArgsConstructor
public class GameRoomController {

    private final GameRoomApplicationService gameRoomApplicationService;

    @PostMapping
    @Operation(summary = "게임 방 생성", description = "게임 방을 생성하고 입장한다.")
    public ResponseEntity<GameRoomEntranceResponse> createGameRoom(@Authenticated WebCanvasAuthentication authentication) {
        return ResponseEntity.ok(
                GamePresentationDtoMapper.toGameRoomEntranceResponse(
                        gameRoomApplicationService.createGameRoomAndEnter(authentication.getUserId())
                )
        );
    }

    @GetMapping
    @Operation(summary = "입장 가능한 방 목록 조회", description = "요청을 보낸 유저가 입장할 수 있는 webcanvas-service 방의 목록을 조회한다.")
    public ResponseEntity<GameRoomListResponse> getEnterableGameRooms(@Authenticated WebCanvasAuthentication authentication) {
        return ResponseEntity.ok(
                GamePresentationDtoMapper.toGameRoomListResponse(
                    gameRoomApplicationService.findEnterableGameRooms(authentication.getUserId())
                )
        );
    }

    @Operation(summary = "현재 입장한 게임 방 조회", description = "요청을 보낸 유저가 입장한 게임 방과 입장 정보를 조회한다.")
    @GetMapping("entrance")
    public ResponseEntity<GameRoomEntranceDetailInfoResponse> getEnteredGameRoom(@Authenticated WebCanvasAuthentication authentication) {
        return ResponseEntity.ok(
                GamePresentationDtoMapper.toGameRoomEntranceDetailInfoResponse(
                        gameRoomApplicationService.findEnteredGameRoomInfo(authentication.getUserId())
                )
        );
    }


    @Operation(summary = "현재 입장한 게임 방에서 퇴장", description = "요청을 보낸 유저가 입장한 게임 방에서 퇴장한다.")
    @DeleteMapping("entrance/{gameRoomEntranceId}")
    public ResponseEntity<GameRoomExitResponse> exitGameRoom(@Authenticated WebCanvasAuthentication authentication, @PathVariable("gameRoomEntranceId") Long gameRoomEntranceId) {
        gameRoomApplicationService.exitFromRoom(gameRoomEntranceId, authentication.getUserId());
        return ResponseEntity.ok(new GameRoomExitResponse(true));
    }


    @PostMapping("{gameRoomId}/entrance")
    @Operation(summary = "게임 방 입장", description = "입장 요청을 보낸 유저를 대상 게임 방에 입장시킨다.")
    public ResponseEntity<GameRoomEntranceResponse> enterGameRoom(@PathVariable("gameRoomId") Long gameRoomId, @Authenticated WebCanvasAuthentication authentication) {
        return ResponseEntity.ok(
                GamePresentationDtoMapper.toGameRoomEntranceResponse(
                        gameRoomApplicationService.enterGameRoom(GameCommandMapper.toEnterGameRoomCommand(gameRoomId, authentication.getUserId()))
                )
        );
    }

    @PostMapping("{joinCode}/enterance")
    @Operation(summary = "Join Code로 게임 방 입장", description = "Join Code로 입장 요청읇 보낸 유저를 대상 게임 방에 입장시킨다.")
    public ResponseEntity<GameRoomEntranceResponse> enterGameRoomWithJoinCode(@PathVariable("joinCode") String joinCode,
                                                                              @Authenticated WebCanvasAuthentication authentication) {
        return ResponseEntity.ok(
                GamePresentationDtoMapper.toGameRoomEntranceResponse(
                        gameRoomApplicationService.enterGameRoomWithJoinCode(joinCode, authentication.getUserId())
                )
        );
    }

    @PatchMapping("entrance/{gameRoomEntranceId}/ready")
    @Operation(summary = "레디 상태 변경", description = "게임 방에 입장한 유저의 레디 상태를 업데이트한다.")
    public ResponseEntity<Boolean> updateReady(
            @PathVariable("gameRoomEntranceId") Long gameRoomEntranceId,
            @RequestBody GameRoomReadyUpdateRequest request,
            @Authenticated WebCanvasAuthentication authentication) {
        return ResponseEntity.ok(gameRoomApplicationService.updateReady(gameRoomEntranceId, authentication.getUserId(), request.ready()));
    }
}
