package com.bb.webcanvasservice.domain.canvas;

import java.util.List;

public class Stroke {

    // 각 스트로크에 대한 고유 ID
    private Long strokeId;

    // 어떤 방에서 그려진 데이터인지
    private Long gameId;

    // 그림을 그린 유저 ID
    private Long userId;

    // 스트로크의 색상
    private String color;

    // 선의 두께
    private int lineWidth;

    // 선을 구성하는 점들
    private List<Point> points;
}
