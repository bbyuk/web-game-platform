package com.bb.webcanvasservice.game.presentation.response;

import com.bb.webcanvasservice.game.domain.model.room.GameSessionState;
import com.bb.webcanvasservice.game.domain.model.room.GameTurnState;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "현재 게임 진행 정보 조회 API의 응답 DTO")
public record GameProgressResponse(
        @Schema(description = "게임 세션 ID", example = "213")
        Long gameSessionId,

        @Schema(description = "대상 게임 세션의 턴 수", example = "5")
        int turnCount,

        @Schema(description = "턴 당 시간 (s)", example = "90")
        int timePerTurn,

        @Schema(description = "게임 세션 상태", example = "PLAYING")
        GameSessionState gameSessionState,

        @Schema(description = "현재까지 진행되었거나 진행중인 턴들의 목록")
        List<GameTurnInfo> turns
) {

    @Schema(description = "게임 세션 내에서 진행되는 턴 정보")
    public record GameTurnInfo(
            @Schema(description = "해당 턴에 그림을 그릴 차례인 사람의 ID")
            Long drawerId,
            @Schema(description = "턴 종료 시간")
            LocalDateTime expiration,
            @Schema(description = "정답 - 해당 턴에 그림을 그릴 차례인 사람인 경우에만 값을 채워 리턴", nullable = true)
            String answer,
            @Schema(description = "정답자 ID")
            Long correctAnswererId,
            @Schema(description = "턴의 상태")
            GameTurnState gameTurnState
    ) {}
}
