package com.bb.webcanvasservice.domain.canvas.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class Point {
    private int x;
    private int y;
}
