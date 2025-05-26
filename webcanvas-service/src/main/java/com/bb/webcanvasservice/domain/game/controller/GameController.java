package com.bb.webcanvasservice.domain.game.controller;

import com.bb.webcanvasservice.domain.game.service.GameService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Game API", description = "게임 플레이 관련 API")
@RestController
@RequestMapping("game/canvas")
@RequiredArgsConstructor
public class GameController {

    private final GameService gameService;

}
