import Canvas from "@/components/canvas/index.jsx";
import React, { useEffect, useState } from "react";
import { useLocation, useNavigate, useOutletContext, useParams } from "react-router-dom";
import { game } from "@/api/index.js";
import { useApiLock } from "@/api/lock/index.jsx";
import { Gamepad2 } from "lucide-react";
import { getApiClient } from "@/client/http/index.jsx";
import { useAuthentication } from "@/contexts/authentication/index.jsx";
import { useLeftSideStore } from "@/stores/layout/leftSideStore.jsx";
import ItemList from "@/components/layouts/side-panel/item-list/index.jsx";
import { pages } from "@/router/index.jsx";

export default function GameRoomPlayingPage() {
  // 현재 캔버스의 획 모음
  const [strokes, setStrokes] = useState([]);

  //캔버스 온디맨드 리렌더링 시그널
  const [reRenderingSignal, setReRenderingSignal] = useState(false);

  const { authenticatedUserId } = useAuthentication();

  const { apiLock } = useApiLock();

  const apiClient = getApiClient();

  const location = useLocation();

  const navigate = useNavigate();

  const { roomId } = useParams();

  const { webSocketClientRef } = useOutletContext();

  const { enteredUsers } = useOutletContext();
  const { setTitle, setContents } = useLeftSideStore();

  const [currentDrawerId, setCurrentDrawerId] = useState(null);
  const [gameSessionId, setGameSessionId] = useState(null);

  /**
   * =========================== 이벤트 핸들러 =============================
   */

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
          // TODO 턴 진행 이벤트 클라이언트 핸들링
          apiClient.get(game.getCurrentGameTurn(gameSessionId)).then((response) => {
            setCurrentDrawerId(response.drawerId);
          });

          console.log(frame);
          break;
        case "SESSION/END":
          // TODO 게임 종료 이벤트 클라이언트 핸들링
          alert("게임이 종료되었습니다. 대기실로 이동합니다.");
          navigate(pages.gameRoom.waiting.url(roomId), { replace: true });
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
   * =========================== 이벤트 핸들러 =============================
   */

  useEffect(() => {
    setTitle({
      label: "playing",
      icon: <Gamepad2 className="w-6 h-6 text-green-500" />,
      button: false,
    });

    /**
     * 게임 세션 정보를 조회한다.
     */
    console.log(roomId);
    apiLock(
      game.getCurrentGameSession(roomId),
      async () => await apiClient.get(game.getCurrentGameSession(roomId))
    ).then((response) => {
      console.log(response);
      setGameSessionId(response.gameSessionId);
    });

  }, []);

  useEffect(() => {
    console.log("여긴 타나??");

    console.log("gameSessionId = ", gameSessionId);
    console.log("webSocketClientRef", webSocketClientRef);
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

  return (
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
  );
}
