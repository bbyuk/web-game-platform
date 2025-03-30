package com.bb.webcanvasservice.domain.canvas.dto;

import lombok.*;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Stroke {

    // 스트로크의 색상
    private String color;

    // 선의 두께
    private int lineWidth;

    // 선을 구성하는 점들
    private List<Point> points;
}
