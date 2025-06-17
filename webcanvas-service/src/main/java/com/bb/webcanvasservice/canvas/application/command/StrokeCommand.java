package com.bb.webcanvasservice.canvas.application.command;

import com.bb.webcanvasservice.canvas.domain.model.Coordinate;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Application layer 캔버스 스트로크 command Dto")
public record StrokeCommand(

        @Schema(description = "Stroke 요청 유저 ID")
        Long userId,

        @Schema(description = "스트로크 발생 게임 세션 ID")
        Long gameSessionId,

        @Schema(description = "스트로크의 색상")
        String color,

        @Schema(description = "선의 두께")
        int lineWidth,

        @Schema(description = "선을 구성하는 점들")
        List<Coordinate> points
) {
}
