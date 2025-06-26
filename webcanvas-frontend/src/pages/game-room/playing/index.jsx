import Canvas from "@/components/canvas/index.jsx";
import React, { useEffect, useState } from "react";
import { useNavigate, useOutletContext, useParams } from "react-router-dom";
import { game } from "@/api/index.js";
import { useApiLock } from "@/api/lock/index.jsx";
import { Gamepad2 } from "lucide-react";
import { getApiClient } from "@/client/http/index.jsx";
import { useAuthentication } from "@/contexts/authentication/index.jsx";
import { useLeftSideStore } from "@/stores/layout/leftSideStore.jsx";
import ItemList from "@/components/layouts/side-panel/contents/item-list/index.jsx";
import { pages } from "@/router/index.jsx";
import GameTurnTimer from "@/components/game-turn-timer/index.jsx";
import AnswerBoard from "@/components/answer-board/index.jsx";
import { useTimer } from "@/pages/game-room/playing/timer.jsx";
import { useClientStore } from "@/stores/client/clientStore.jsx";

export default function GameRoomPlayingPage() {
  // ===============================================================
  // 상태 정의
  // ===============================================================

  // 현재 캔버스의 획 모음
  const [strokes, setStrokes] = useState([]);
  //캔버스 온디맨드 리렌더링 시그널
  const [reRenderingSignal, setReRenderingSignal] = useState(false);
  // 유저ID
  const { authenticatedUserId } = useAuthentication();
  // apiLock
  const { apiLock } = useApiLock();
  const apiClient = getApiClient();
  const navigate = useNavigate();
  const { roomId } = useParams();
  const { webSocketClientRef, enteredUsers } = useOutletContext();
  const { setContents } = useLeftSideStore();
  const { endLoading } = useClientStore();

  const [gameSessionId, setGameSessionId] = useState(null);
  const [currentDrawerId, setCurrentDrawerId] = useState(null);
  const [displayedAnswer, setDisplayedAnswer] = useState(null);

  // 게임 턴 별 시간 (s)
  const [timePerTurn, setTimePerTurn] = useState(0);
  // 현재 턴의 인덱스
  const [currentTurnIndex, setCurrentTurnIndex] = useState(0);
  // 전체 턴 수
  const [turnCount, setTurnCount] = useState(0);
  const [isTurnActive, setIsTurnActive] = useState(false);
  const timer = useTimer();

  // ===============================================================
  // 유저 정의 함수
  // ===============================================================

  /**
   * 캔버스 컴포넌트 stroke 이벤트 핸들러
   * @param stroke
   */
  const onStrokeHandler = (stroke) => {
    if (stroke.points.length > 0) {
      setStrokes((prevItems) => [...prevItems, stroke]);
      webSocketClientRef.current.publish({
        destination: `/session/${gameSessionId}/canvas/stroke`,
        body: JSON.stringify(stroke),
      });
    }
  };

  /**
   * 웹소켓을 통해 구독할 토픽과 콜백을 정의하고 구독한다.
   */
  const subscribeTopics = () => {
    console.log("게임 세션 이벤트브로커 구독");
    /**
     * 게임 방 메세지 브로커 핸들러
     * @param frame
     */
    const gameSessionEventHandler = (frame) => {
      if (!frame.event) {
        // 서버로부터 받은 이벤트가 없음
        return;
      }
      switch (frame.event) {
        case "SESSION/TURN_PROGRESSED":
          findCurrentGameTurnInfo(gameSessionId);

          break;
        case "SESSION/ALL_USER_LOADED":
          endLoading();
          break;
        case "SESSION/END":
          // TODO 게임 종료 이벤트 클라이언트 핸들링
          alert("게임이 종료되었습니다. 대기실로 이동합니다.");
          navigate(pages.gameRoom.waiting.url(roomId), {
            replace: true,
            state: { gameSessionEnd: true },
          });
          break;
      }
    };
    const topics = [
      // 게임 세션 진행 이벤트 broker
      {
        destination: `/session/${gameSessionId}`,
        messageHandler: gameSessionEventHandler,
      },
    ];

    console.log("game-room/playing/index.jsx = 구독");

    webSocketClientRef.current.subscribe(topics);
  };

  /**
   * 캔버스 컴포넌트 리렌더링 이벤트 핸들러
   */
  const onReRenderingHandler = () => {
    setReRenderingSignal(false);
  };

  /**
   * 턴 타이머 종료시 이벤트 핸들러
   */
  const onTurnTimerExpiredHandler = () => {
    console.log("턴 타이머 종료");
  };

  /**
   * 게임 세션 정보를 조회한다.
   */
  const findCurrentGameSessionInfo = () => {
    apiLock(
      game.getCurrentGameSession(roomId),
      async () => await apiClient.get(game.getCurrentGameSession(roomId))
    ).then((response) => {
      setGameSessionId(response.gameSessionId);
      setTimePerTurn(response.timePerTurn);
      setCurrentTurnIndex(response.currentTurnIndex);
      setTurnCount(response.turnCount);
      timer.ready(response.timePerTurn);

      if (response.state === "PLAYING") {
        /**
         * 새로고침 시 PLAING 중일 경우 현재 턴 정보 조회
         */
        findCurrentGameTurnInfo(response.gameSessionId);
      }
    });
  };

  const findCurrentGameTurnInfo = (gameSessionId) => {
    apiClient.get(game.getCurrentGameTurn(gameSessionId)).then((response) => {
      setCurrentDrawerId(response.drawerId);
      setDisplayedAnswer(response.answer);

      timer.start(response.expiration);
    });
  };

  // ===============================================================
  // useEffect 훅
  // ===============================================================

  useEffect(() => {
    findCurrentGameSessionInfo();
  }, []);

  useEffect(() => {
    if (!gameSessionId || !webSocketClientRef.current) return;

    /**
     * 게임 세션 ID를 상태에 저장한 후 세션 웹소켓을 구독한다.
     */
    subscribeTopics();
  }, [gameSessionId, webSocketClientRef.current]);

  useEffect(() => {
    setContents({
      slot: ItemList,
      props: {
        value: enteredUsers.map(({ userId, nickname, role, ...rest }) => ({
          label: nickname,
          highlight: userId === currentDrawerId,
          theme: "indigo",
          ...rest,
        })),
      },
    });
  }, [authenticatedUserId, currentDrawerId]);

  const isDrawer = authenticatedUserId === currentDrawerId;

  return (
    <>
      <GameTurnTimer remainingPercent={timer.remainingPercent} />
      {isDrawer && <AnswerBoard answer={displayedAnswer} />}
      <Canvas
        className="flex-1"
        strokes={strokes}
        onStroke={onStrokeHandler}
        reRenderingSignal={reRenderingSignal}
        afterReRendering={onReRenderingHandler}
        // color={
        //   topTabs.items[topTabs.selectedIndex] ? topTabs.items[topTabs.selectedIndex].label : "black"
        // }
      />
    </>
  );
}
