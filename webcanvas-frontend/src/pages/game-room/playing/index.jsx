import Canvas from '@/components/canvas/index.jsx';
import React, { useEffect, useState } from 'react';
import { useNavigate, useOutletContext, useParams } from 'react-router-dom';
import { game } from '@/api/index.js';
import { useApiLock } from '@/api/lock/index.jsx';
import { MessageCircle } from 'lucide-react';
import { getApiClient } from '@/client/http/index.jsx';
import { useAuthentication } from '@/contexts/authentication/index.jsx';
import { useLeftSideStore } from '@/stores/layout/leftSideStore.jsx';
import ItemList from '@/components/layouts/side-panel/contents/item-list/index.jsx';
import { pages } from '@/router/index.jsx';
import GameTurnTimer from '@/components/game-turn-timer/index.jsx';
import AnswerBoard from '@/components/answer-board/index.jsx';
import { useTimer } from '@/pages/game-room/playing/timer.jsx';
import { useClientStore } from '@/stores/client/clientStore.jsx';
import ChatList from '@/components/layouts/side-panel/contents/chat-list/index.jsx';
import SidePanelFooterInput from '@/components/layouts/side-panel/footer/input/index.jsx';
import { useRightSideStore } from '@/stores/layout/rightSideStore.jsx';
import CanvasToolbar from '@/components/canvas/toolbar/index.jsx';
import { useCountdown } from '@/contexts/countdown/index.jsx';
import { useToast } from '@/contexts/toast/index.jsx';

export default function GameRoomPlayingPage() {
  // ===============================================================
  // 상태 정의
  // ===============================================================

  // 현재 캔버스의 획 모음
  const [strokes, setStrokes] = useState([]);
  //캔버스 온디맨드 리렌더링 시그널
  const [reRenderingSignal, setReRenderingSignal] = useState(false);
  // 유저ID
  const {authenticatedUserId} = useAuthentication();
  // apiLock
  const {apiLock} = useApiLock();
  const apiClient = getApiClient();
  const navigate = useNavigate();
  const {roomId} = useParams();
  const {webSocketClientRef, enteredUsers} = useOutletContext();
  const rightSideStore = useRightSideStore();
  const leftSideStore = useLeftSideStore();
  const {endLoading} = useClientStore();

  const [gameSessionId, setGameSessionId] = useState(null);
  const [currentDrawerId, setCurrentDrawerId] = useState(null);
  const [displayedAnswer, setDisplayedAnswer] = useState(null);
  const [chatMessages, setChatMessages] = useState([]);


  /**
   * ====== 캔버스 관련 state ======
   */
  const [selectedCanvasTool, setSelectedCanvasTool] = useState("pen");
  const [selectedCanvasToolSize, setSelectedCanvasToolSize] = useState(5);
  const [selectedCanvasPenColor, setSelectedCanvasPenColor] = useState("#000000");

  /**
   * ====== 캔버스 관련 state ======
   */

  /**
   * ====== 게임 플레이 세션 관련 state ======
   */
    // 게임 턴 별 시간 (s)
  const [timePerTurn, setTimePerTurn] = useState(0);
  // 현재 턴의 인덱스
  const [currentTurnIndex, setCurrentTurnIndex] = useState(0);
  // 전체 턴 수
  const [turnCount, setTurnCount] = useState(0);
  const [isTurnActive, setIsTurnActive] = useState(false);

  // countdown context
  const countdown = useCountdown();
  const toast = useToast();

  const handleAnswer = isCorrect => {
    if (isCorrect) {
      countdown(
        5,
        { message: '🎉 정답입니다!' },
        () => toast('다음 턴으로 이동합니다.', 3000)
      );
    } else {
      // 오답은 토스트만
      toast('다시 시도해보세요.', 3000);
    }
  };


  /**
   * ====== 게임 플레이 세션 관련 state ======
   */
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
      // setStrokes((prevItems) => [...prevItems, stroke]);
      webSocketClientRef.current.send(`/session/${gameSessionId}/canvas/stroke`, stroke);
    }
  };

  /**
   * 캔버스 컴포넌트를 클리어한다.
   */
  const clearCanvas = () => {
    // sender client clear
    setStrokes([]);
    setReRenderingSignal(true);
    // websocket clear message send
    webSocketClientRef.current.send(`/session/${gameSessionId}/canvas/clear`)
  };

  /**
   * 웹소켓을 통해 구독할 토픽과 콜백을 정의하고 구독한다.
   */
  const subscribeTopics = () => {
    console.log("게임 세션 이벤트브로커 구독");
    /**
     * 게임 세션 처리 브로커 핸들러
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
          countdown(
            frame.sessionStartCountDownDelaySeconds
          );
          break;
        case "SESSION/END":
          // TODO 게임 종료 이벤트 클라이언트 핸들링
          alert("게임이 종료되었습니다. 대기실로 이동합니다.");
          navigate(pages.gameRoom.waiting.url(roomId), {
            replace: true,
            state: {gameSessionEnd: true},
          });
          break;
      }
    };

    /**
     * 채팅 메세지 브로커 핸들러
     * @param frame
     */
    const gameSessionChatEventHandler = (frame) => {
      console.log(frame);

      const newMessage = {
        value: frame.value,
        senderId: frame.senderId,
        nickname: enteredUsers
          .filter(enteredUser => enteredUser.userId === frame.senderId)
          .map(enteredUser => enteredUser.nickname),
        color: enteredUsers
          .filter(enteredUser => enteredUser.userId === frame.senderId)
          .map(enteredUser => enteredUser.color)
      }

      console.log(enteredUsers);
      setChatMessages((prevItems) => [...prevItems, newMessage]);
    };

    /**
     * 캔버스 브로커 핸들러
     * @type {[{messageHandler: gameSessionEventHandler, destination: string},{messageHandler: gameSessionChatEventHandler, destination: string},{messageHandler: messageHandler, destination: string}]}
     */
    const canvasEventHandler = (frame) => {
      if (frame.event === "SESSION/CANVAS/STROKE") {
        setStrokes((prevItems) => [...prevItems, frame.stroke]);
        setReRenderingSignal(true);
      }
      else if (frame.event === "SESSION/CANVAS/CLEAR") {
        setStrokes([]);
        setReRenderingSignal(true);
      }
    };

    const topics = [
      // 게임 세션 진행 이벤트 broker
      {
        destination: `/session/${gameSessionId}`,
        messageHandler: gameSessionEventHandler,
      },
      {
        destination: `/session/${gameSessionId}/chat`,
        messageHandler: gameSessionChatEventHandler
      },
      {
        destination: `/session/${gameSessionId}/canvas`,
        messageHandler: canvasEventHandler
      }
    ];

    console.log("game-room/playing/index.jsx = 구독");

    webSocketClientRef.current.subscribe("room/playing", topics);
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
    rightSideStore.setTitle({
      label: "chat",
      icon: <MessageCircle size={20} className="text-gray-400"/>,
      button: false,
      onClick: () => {
      },
    });

    rightSideStore.setContents({
      slot: ChatList,
      props: {
        messages: chatMessages,
        removeOldChat: (maxChatCount) => {
          setChatMessages((prev) => prev.slice(-maxChatCount));
        },
      },
    });
    rightSideStore.setFooter({
      slot: SidePanelFooterInput,
    });

    findCurrentGameSessionInfo();

    return () => {
      if (webSocketClientRef.current) {
        webSocketClientRef.current.unsubscribe("room/playing");
      }
    };
  }, []);

  useEffect(() => {
    rightSideStore.setContents({
      slot: ChatList,
      props: {
        messages: chatMessages,
        removeOldChat: (maxChatCount) => {
          setChatMessages((prev) => prev.slice(-maxChatCount));
        },
      },
    });
  }, [chatMessages]);

  useEffect(() => {
    if (!gameSessionId || !webSocketClientRef.current) return;

    rightSideStore.setFooter({
      slot: SidePanelFooterInput,
      props: {
        onSubmit: (message) => {
          webSocketClientRef.current.send(`/session/${gameSessionId}/chat/send`, {
            value: message,
          });
        },
      },
    });

    /**
     * 게임 세션 ID를 상태에 저장한 후 세션 웹소켓을 구독한다.
     */
    subscribeTopics();
  }, [gameSessionId, webSocketClientRef.current]);

  useEffect(() => {
    leftSideStore.setContents({
      slot: ItemList,
      props: {
        value: enteredUsers.map(({userId, nickname, role, ...rest}) => ({
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
      <GameTurnTimer remainingPercent={timer.remainingPercent}/>
      {isDrawer &&
        <>
          <AnswerBoard answer={displayedAnswer}/>
          <CanvasToolbar
            tool={selectedCanvasTool}
            size={selectedCanvasToolSize}
            color={selectedCanvasPenColor}
            onChangeTool={(value) => (setSelectedCanvasTool(value))}
            onChangeSize={(value) => (setSelectedCanvasToolSize(value))}
            onChangeColor={(value) => (setSelectedCanvasPenColor(value))}
            onClear={clearCanvas}
          />
        </>}
      <Canvas
        className="flex-1"
        strokes={strokes}
        tool={selectedCanvasTool}
        color={selectedCanvasPenColor}
        lineWidth={selectedCanvasToolSize}
        onStroke={onStrokeHandler}
        drawable={authenticatedUserId === currentDrawerId}
        reRenderingSignal={reRenderingSignal}
        afterReRendering={onReRenderingHandler}
        // color={
        //   topTabs.items[topTabs.selectedIndex] ? topTabs.items[topTabs.selectedIndex].label : "black"
        // }
      />
    </>
  );
}
