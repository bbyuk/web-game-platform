package com.bb.webcanvasservice.domain.game.service;

import com.bb.webcanvasservice.domain.dictionary.enums.Language;
import com.bb.webcanvasservice.domain.dictionary.enums.PartOfSpeech;
import com.bb.webcanvasservice.domain.dictionary.service.DictionaryService;
import com.bb.webcanvasservice.domain.game.entity.GameRoomEntrance;
import com.bb.webcanvasservice.domain.game.entity.GameSession;
import com.bb.webcanvasservice.domain.game.entity.GameTurn;
import com.bb.webcanvasservice.domain.game.enums.GameRoomEntranceState;
import com.bb.webcanvasservice.domain.game.exception.GameSessionIsOverException;
import com.bb.webcanvasservice.domain.game.exception.GameSessionNotFoundException;
import com.bb.webcanvasservice.domain.game.exception.NextDrawerNotFoundException;
import com.bb.webcanvasservice.domain.game.repository.GameSessionRepository;
import com.bb.webcanvasservice.domain.game.repository.GameTurnRepository;
import com.bb.webcanvasservice.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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


    /**
     * 다음 차례로 그림을 그릴 유저 ID를 찾는다.
     *
     * @param gameSessionId
     * @return
     */
    @Transactional(readOnly = true)
    public Long findNextDrawerId(Long gameSessionId) {
        GameSession gameSession = gameSessionRepository.findById(gameSessionId)
                .orElseThrow(GameSessionNotFoundException::new);

        if (gameSession.getState() != PLAYING) {
            log.debug("게임 세션이 PLAYING 상태가 아닙니다.");
            throw new GameSessionIsOverException();
        }

        List<GameTurn> gameTurns = gameSession.getGameTurns();
        if (gameSession.getTurnCount() <= gameTurns.size()) {
            log.debug("현재 세션에 준비된 턴이 모두 끝났습니다.");
            log.debug("현재 사용된 턴 = {}", gameTurns.size());
            log.debug("현재 세션에 준비된 턴 = {}", gameSession.getTurnCount());
            throw new GameSessionIsOverException();
        }

        /**
         * 현재 게임중인 유저 목록
         */
        List<GameRoomEntrance> gameRoomEntrances = gameRoomFacade.findGameRoomEntrancesByGameRoomIdAndState(gameSession.getGameRoom().getId(), GameRoomEntranceState.PLAYING);

        // 유저별 턴 수 집계
        Map<Long, Integer> drawerCountMap = gameTurns.stream()
                .collect(Collectors.toMap(
                        gt -> gt.getDrawer().getId(),
                        gt -> 1,
                        Integer::sum
                ));

        int minCount = Integer.MAX_VALUE;
        List<Long> candidates = new ArrayList<>();

        for (GameRoomEntrance entrance : gameRoomEntrances) {
            Long userId = entrance.getUser().getId();
            int count = drawerCountMap.getOrDefault(userId, 0);

            if (count < minCount) {
                candidates.clear();
                candidates.add(userId);
                minCount = count;
            } else if (count == minCount) {
                candidates.add(userId);
            }
        }

        if (candidates.isEmpty()) {
            log.debug("후보자를 찾지 못했습니다.");
            throw new NextDrawerNotFoundException();
        }

        /**
         * 후보 ID들 중 랜덤 Index를 뽑아 리턴한다.
         */
        RandomGenerator randomGenerator = RandomGenerator.getDefault();
        int randomIndex = randomGenerator.nextInt(candidates.size());

        return candidates.get(randomIndex);
    }


    /**
     * 다음 턴으로 진행한다.
     * 해당 메소드는 startGame 등과 같은 메소드 이후 시점에 실행되어야 하나
     * 별도의 컨텍스트에서 싫랭되어야 하므로 트랜잭션읇 분리한다.
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void processToNextTurn(Long gameSessionId) {
        GameSession gameSession = gameSessionRepository.findById(gameSessionId)
                .orElseThrow(GameSessionNotFoundException::new);

        if (gameSession.getState() != PLAYING) {
            log.debug("진행중인 게임 세션이 아닙니다. ====== {} : {}", gameSession, gameSession.getState());
            throw new GameSessionIsOverException();
        }

        if (gameSession.getGameTurns().size() >= gameSession.getTurnCount()) {
            log.debug("모든 턴이 진행되었습니다.");
            log.debug("게임 세션 종료");

            /**
             * TODO 게임 종료 처리 메세지 push or 이벤트 pub
             */
//            endGame(gameSessionId);
            return;
        }


        /**
         * @param GameSession gameSession
         * @param User drawer
         * @Param String answer
         */
        GameTurn gameTurn = gameTurnRepository.save(
                new GameTurn(
                        gameSession,
                        userService.findUser(findNextDrawerId(gameSessionId)),
                        dictionaryService.drawRandomWordValue(Language.KOREAN, PartOfSpeech.NOUN)
                ));

        // TODO 턴 진행 이벤트 pub
    }

}
