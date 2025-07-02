package com.bb.webcanvasservice.game.presentation.controller;

import com.bb.webcanvasservice.infrastructure.security.http.Authenticated;
import com.bb.webcanvasservice.infrastructure.security.http.WebCanvasAuthentication;
import com.bb.webcanvasservice.game.application.service.GameService;
import com.bb.webcanvasservice.game.presentation.mapper.GameCommandMapper;
import com.bb.webcanvasservice.game.presentation.mapper.GamePresentationDtoMapper;
import com.bb.webcanvasservice.game.presentation.request.GameRoomReadyUpdateRequest;
import com.bb.webcanvasservice.game.presentation.response.GameRoomExitResponse;
import com.bb.webcanvasservice.game.presentation.response.GameRoomJoinDetailInfoResponse;
import com.bb.webcanvasservice.game.presentation.response.GameRoomJoinResponse;
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

    private final GameService gameService;

    @PostMapping
    @Operation(summary = "게임 방 생성", description = "게임 방을 생성하고 입장한다.")
    public ResponseEntity<GameRoomJoinResponse> createGameRoom(@Authenticated WebCanvasAuthentication authentication) {
        return ResponseEntity.ok(
                GamePresentationDtoMapper.toGameRoomJoinResponse(
                        gameService.createGameRoomAndEnter(authentication.getUserId())
                )
        );
    }

    @GetMapping
    @Operation(summary = "입장 가능한 방 목록 조회", description = "요청을 보낸 유저가 입장할 수 있는 webcanvas-service 방의 목록을 조회한다.")
    public ResponseEntity<GameRoomListResponse> getJoinableGameRooms(@Authenticated WebCanvasAuthentication authentication) {
        return ResponseEntity.ok(
                GamePresentationDtoMapper.toGameRoomListResponse(
                        gameService.findJoinableGameRooms(authentication.getUserId())
                )
        );
    }

    @Operation(summary = "현재 입장한 게임 방 조회", description = "요청을 보낸 유저가 입장한 게임 방과 입장 정보를 조회한다.")
    @GetMapping("detail")
    public ResponseEntity<GameRoomJoinDetailInfoResponse> getJoinedGameRoom(@Authenticated WebCanvasAuthentication authentication) {
        return ResponseEntity.ok(
                GamePresentationDtoMapper.toGameRoomJoinDetailInfoResponse(
                        gameService.findJoinedGameRoomInfo(authentication.getUserId())
                )
        );
    }


    @Operation(summary = "현재 입장한 게임 방에서 퇴장", description = "요청을 보낸 유저가 입장한 게임 방에서 퇴장한다.")
    @DeleteMapping("participant/{gameRoomParticipantId}")
    public ResponseEntity<GameRoomExitResponse> exitGameRoom(@Authenticated WebCanvasAuthentication authentication, @PathVariable("gameRoomParticipantId") Long gameRoomParticipantId) {
        gameService.exitFromRoom(
                GameCommandMapper.toExitGameRoomCommand(gameRoomParticipantId, authentication.getUserId())
        );
        return ResponseEntity.ok(new GameRoomExitResponse(true));
    }


    @PostMapping("id/{gameRoomId}/participant")
    @Operation(summary = "게임 방 입장", description = "입장 요청을 보낸 유저를 대상 게임 방에 입장시킨다.")
    public ResponseEntity<GameRoomJoinResponse> joinGameRoom(@PathVariable("gameRoomId") Long gameRoomId,
                                                             @Authenticated WebCanvasAuthentication authentication) {
        return ResponseEntity.ok(
                GamePresentationDtoMapper.toGameRoomJoinResponse(
                        gameService.joinGameRoom(GameCommandMapper.toEnterGameRoomCommand(gameRoomId, authentication.getUserId()))
                )
        );
    }

    @PostMapping("code/{joinCode}/participant")
    @Operation(summary = "Join Code로 게임 방 입장", description = "Join Code로 입장 요청읇 보낸 유저를 대상 게임 방에 입장시킨다.")
    public ResponseEntity<GameRoomJoinResponse> joinGameRoomWithJoinCode(@PathVariable("joinCode") String joinCode,
                                                                         @Authenticated WebCanvasAuthentication authentication) {
        return ResponseEntity.ok(
                GamePresentationDtoMapper.toGameRoomJoinResponse(
                        gameService.joinGameRoomWithJoinCode(joinCode, authentication.getUserId())
                )
        );
    }

    @PatchMapping("participant/{gameRoomParticipantId}/ready")
    @Operation(summary = "레디 상태 변경", description = "게임 방에 입장한 유저의 레디 상태를 업데이트한다.")
    public ResponseEntity<Boolean> updateReady(
            @PathVariable("gameRoomParticipantId") Long gameRoomParticipantId,
            @RequestBody GameRoomReadyUpdateRequest request,
            @Authenticated WebCanvasAuthentication authentication) {
        return ResponseEntity.ok(
                gameService.updateReady(
                        GameCommandMapper.toUpdateReadyCommand(
                                gameRoomParticipantId, authentication.getUserId(), request.ready()
                        )
                )
        );
    }
}
