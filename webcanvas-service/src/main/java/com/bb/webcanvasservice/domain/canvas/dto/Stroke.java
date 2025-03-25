package com.bb.webcanvasservice.domain.canvas.dto;

import lombok.Builder;

import java.util.List;

@Builder
public record Stroke(
        // 어떤 방에서 그려진 데이터인지
        Long gameRoomId,

        // 그림을 그린 유저 ID
        Long userId,

        // 스트로크의 색상
        String color,

        // 선의 두께
        int lineWidth,

        // 선을 구성하는 점들
        List<Point> points
) {

}
