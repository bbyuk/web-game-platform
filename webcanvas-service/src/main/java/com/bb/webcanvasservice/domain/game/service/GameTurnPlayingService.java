package com.bb.webcanvasservice.domain.game.service;

import com.bb.webcanvasservice.domain.dictionary.enums.Language;
import com.bb.webcanvasservice.domain.dictionary.enums.PartOfSpeech;
import com.bb.webcanvasservice.domain.dictionary.service.DictionaryService;
import com.bb.webcanvasservice.domain.game.entity.GameRoomEntrance;
import com.bb.webcanvasservice.domain.game.entity.GameSession;
import com.bb.webcanvasservice.domain.game.entity.GameTurn;
import com.bb.webcanvasservice.domain.game.enums.GameRoomEntranceState;
import com.bb.webcanvasservice.domain.game.event.GameTurnProgressedEvent;
import com.bb.webcanvasservice.domain.game.exception.GameSessionIsOverException;
import com.bb.webcanvasservice.domain.game.exception.GameSessionNotFoundException;
import com.bb.webcanvasservice.domain.game.exception.NextDrawerNotFoundException;
import com.bb.webcanvasservice.domain.game.repository.GameSessionRepository;
import com.bb.webcanvasservice.domain.game.repository.GameTurnRepository;
import com.bb.webcanvasservice.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.random.RandomGenerator;
import java.util.stream.Collectors;

import static com.bb.webcanvasservice.domain.game.enums.GameSessionState.PLAYING;

/**
 * 게임 진행 중 턴과 관려된 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GameTurnPlayingService {

    private final GameTurnRepository gameTurnRepository;
    private final GameSessionRepository gameSessionRepository;

    private final DictionaryService dictionaryService;
    private final UserService userService;
    private final GameRoomFacade gameRoomFacade;

    private final ApplicationEventPublisher applicationEventPublisher;



}
