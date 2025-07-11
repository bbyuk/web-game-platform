package com.bb.webcanvasservice.canvas.presentation.request;

import com.bb.webcanvasservice.canvas.domain.model.Coordinate;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "캔버스 스트로크 draw 요청 DTO")
public record StrokeRequest(

        @Schema(description = "스트로크 tool")
        String tool,

        @Schema(description = "선의 색")
        String color,

        @Schema(description = "라인 폭")
        int lineWidth,

        @Schema(description = "스트로크를 구성하는 점들")
        List<Coordinate> points
) {
}
