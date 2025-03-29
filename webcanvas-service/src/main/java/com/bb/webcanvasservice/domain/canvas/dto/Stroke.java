package com.bb.webcanvasservice.domain.canvas.dto;

import lombok.*;

import java.util.List;

@Getter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Stroke {
    // 어떤 방에서 그려진 데이터인지
    private Long gameRoomId;

    // 그림을 그린 유저 ID
    private Long userId;

    // 스트로크의 색상
    private String color;

    // 선의 두께
    private int lineWidth;

    // 선을 구성하는 점들
    private List<Point> points;
}
