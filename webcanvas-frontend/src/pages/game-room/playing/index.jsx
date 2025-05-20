import Canvas from "@/components/canvas/index.jsx";
import { useEffect, useRef, useState } from "react";
import { useApplicationContext } from "@/contexts/index.jsx";
import { EMPTY_MESSAGES, REDIRECT_MESSAGES } from "@/constants/message.js";
import { useLocation, useNavigate } from "react-router-dom";
import { game } from "@/api/index.js";
import { useApiLock } from "@/api/lock/index.jsx";
import { pages } from "@/router/index.jsx";
import { ArrowLeft } from "lucide-react";
import { getApiClient } from "@/client/http/index.jsx";
import { getWebSocketClient } from "@/client/stomp/index.jsx";
import { useAuthentication } from "@/contexts/authentication/index.jsx";
import SidePanelFooterButton from "@/components/layouts/side-panel/footer-button/index.jsx";

export default function GameRoomPlayingPage() {
  // 현재 캔버스의 획 모음
  const [strokes, setStrokes] = useState([]);

  //캔버스 온디맨드 리렌더링 시그널
  const [reRenderingSignal, setReRenderingSignal] = useState(false);

  // 전역 컨텍스트
  const { topTabs, leftSidebar, rightSidebar } = useApplicationContext();

  const { authenticatedUserIdRef } = useAuthentication();

  const { apiLock } = useApiLock();

  const apiClient = getApiClient();

  const location = useLocation();

  const navigate = useNavigate();

  const [gameRoomId, setGameRoomId] = useState(null);

  const webSocketClientRef = useRef(null);

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
        destination: `/session/${gameRoomId}/canvas/stroke`,
        body: JSON.stringify(stroke),
      });
    }
  };

  /**
   * 캔버스 컴포넌트 리렌더링 이벤트 핸들러
   */
  const onReRenderingHandler = () => {
    setReRenderingSignal(false);
  };

  /**
   * 현재 입장한 게임 방의 정보를 조회한다.
   * @returns {Promise<awaited Promise<Result<RootNode> | void> | Promise<Result<Root> | void> | Promise<any>>}
   */
  const findCurrentGameRoomInfo = async () => {
    // 방 정보 조회
    return await apiClient
      .get(game.getCurrentEnteredGameRoom)
      .then((response) => {
        if (location.pathname !== pages.gameRoom.waiting.url(response.gameRoomId)) {
          navigate(pages.gameRoom.waiting.url(response.gameRoomId), { replace: true });
          return null;
        }
        return response;
      })
      .catch((error) => {
        if (error.code === "R003") {
          // 로비로 이동
          alert(REDIRECT_MESSAGES.TO_LOBBY);
          navigate(pages.lobby.url, { replace: true });
          return null;
        }
      });
  };

  /**
   * 게임을 시작한다.
   */
  const startGame = () => {
    alert("게임 시작");
  };

  /**
   * left sidebar set
   * @param response
   */
  const setLeftSidebar = (response) => {
    if (response.enteredUsers) {
      leftSidebar.setItems(
        response.enteredUsers.map(({ nickname, ...rest }) => ({ label: nickname, ...rest }))
      );
    } else {
      leftSidebar.setItems(EMPTY_MESSAGES.ENTERED_USER_LIST);
    }

    leftSidebar.setTitle({
      label: "exit",
      icon: <ArrowLeft size={20} className="text-gray-400" />,
      button: true,
      onClick: () => {
        exitGameRoom(response.gameRoomEntranceId);
      },
    });

    leftSidebar.setFooter(
      <SidePanelFooterButton
        label={"게임 시작"}
        onClick={startGame}
        disabled={response.enteredUsers.length === 1}
      />
    );
  };

  /**
   * 현재 게임 방에서 퇴장한다.
   * @returns {Promise<void>}
   */
  const exitGameRoom = async (gameRoomEntranceId) => {
    if (!confirm("방에서 나가시겠습니까?")) {
      return;
    }

    const response = await apiLock(
      game.exitFromGameRoom(gameRoomEntranceId),
      async () => await apiClient.delete(game.exitFromGameRoom(gameRoomEntranceId))
    );

    if (response.success) {
      navigate(pages.lobby.url, { replace: true });
    }
  };

  /**
   * 웹소켓 서버에 연결하고 현재 방에 해당하는 브로커를 구독한다.
   * @param gameRoomId
   */
  const connectToWebSocket = (gameRoomId) => {
    if (webSocketClientRef.current) {
      // 이전 client 존재시 deactivate
      webSocketClientRef.current.deactivate();
    }

    /**
     * 게임 방 메세지 브로커 핸들러
     * @param frame
     */
    const gameRoomEventHandler = (frame) => {
      if (!frame.event) {
        // 서버로부터 받은 이벤트가 없음
        return;
      }
      const { event, userId } = frame;
      if (
        (event === "ROOM/ENTRANCE" || event === "ROOM/EXIT") &&
        authenticatedUserIdRef.current !== userId
      ) {
        /**
         * 다른 사람 입장 OR 퇴장 이벤트 발생시
         */
        onOtherUserStateChange();
      }
    };

    /**
     * 채팅 메세지 브로커 핸들러
     * @param frame
     */
    const gameRoomChatHandler = (frame) => {
      console.log(frame);
    };

    /**
     * 캔버스 메세지 브로커 핸들러
     * @param stroke Stroke {color: String, lineWidth: Integer, points: [{x: double, y: double}]}
     *
     */
    const gameRoomCanvasHandler = (stroke) => {
      setStrokes((prevItems) => [...prevItems, stroke]);
      setReRenderingSignal(true);
    };

    const subscribeTopics = [
      // 게임 방 공통 이벤트 broker
      {
        destination: `/session/${gameRoomId}`,
        messageHandler: gameRoomEventHandler,
      },
      // 게임 방 내 채팅 broker
      {
        destination: `/session/${gameRoomId}/chat`,
        messageHandler: gameRoomChatHandler,
      },
      // 게임 방 내 캔버스 stroke broker
      {
        destination: `/session/${gameRoomId}/canvas`,
        messageHandler: gameRoomCanvasHandler,
      },
    ];

    const options = {
      onConnect: (frame) => {
        console.log(`connected At GameRoom ${frame}`);
      },
      onError: (frame) => {
        console.log(frame);
      },
      topics: subscribeTopics,
    };

    webSocketClientRef.current = getWebSocketClient(options);
  };

  /**
   * 다른 유저의 입장 및 퇴장 이벤트 발생시
   * 현재 게임방 정보를 재조회해 leftbar setting
   */
  const onOtherUserStateChange = () => {
    findCurrentGameRoomInfo().then((response) => {
      if (!response) {
        return;
      }

      setLeftSidebar(response);
    });
  };

  /**
   * =========================== 이벤트 핸들러 =============================
   */
  useEffect(() => {
    findCurrentGameRoomInfo()
      .then((response) => {
        if (!response) {
          return;
        }
        // 정상적인 방 경로로 입장.
        setLeftSidebar(response);
        setGameRoomId(response.gameRoomId);

        // stomp 연결
        connectToWebSocket(response.gameRoomId);
      })
      .catch((error) => alert(error))
      .finally(() => {
        /**
         * TODO 캔버스 팔레트 서비스 개발 및 조회 API로 변경
         */
        topTabs.setItems([
          { label: "black" },
          { label: "green" },
          { label: "yellow" },
          { label: "red" },
          { label: "blue" },
        ]);
      });
  }, [location.pathname]);

  useEffect(() => {
    return () => {
      leftSidebar.clear();
      rightSidebar.clear();
      webSocketClientRef.current.deactivate();
    };
  }, []);

  return (
    <Canvas
      className="flex-1"
      strokes={strokes}
      onStroke={onStrokeHandler}
      reRenderingSignal={reRenderingSignal}
      afterReRendering={onReRenderingHandler}
      color={
        topTabs.items[topTabs.selectedIndex] ? topTabs.items[topTabs.selectedIndex].label : "black"
      }
    />
  );
}
