import Canvas from "@/components/canvas/index.jsx";
import { useEffect, useState } from "react";
import { useApplicationContext } from "@/contexts/index.jsx";
import { EMPTY_MESSAGES, REDIRECT_MESSAGES } from "@/constants/message.js";
import { useLocation, useNavigate } from "react-router-dom";
import { game } from "@/api/index.js";
import { useApiLock } from "@/api/lock/index.jsx";
import { pages } from "@/router/index.jsx";
import { ArrowLeft } from "lucide-react";
import { useApiClient } from "@/hooks/api-client/index.jsx";

export default function GameRoomPage() {
  // 현재 캔버스의 획 모음
  const [strokes, setStrokes] = useState([]);

  //캔버스 온디맨드 리렌더링 시그널
  const [reRenderingSignal, setReRenderingSignal] = useState(false);

  // 전역 컨텍스트
  const { topTabs, leftSidebar, rightSidebar, currentGame, utils } = useApplicationContext();

  const apiClient = useApiClient();

  const { apiLock } = useApiLock();

  const location = useLocation();

  const navigate = useNavigate();

  /**
   * =========================== 이벤트 핸들러 =============================
   */

  /**
   * 캔버스 컴포넌트 stroke 이벤트 핸들러
   * @param stroke
   */
  const onStrokeHandler = (stroke) => {
    if (stroke.length > 0) {
      setStrokes((prevItems) => [...prevItems, stroke]);
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
        if (location.pathname !== `${pages.gameRoom}/${response.gameRoomId}`) {
          navigate(`${pages.gameRoom}/${response.gameRoomId}`, { replace: true });
        }
        return response;
      })
      .catch((error) => {
        if (error.code === "R003") {
          // 로비로 이동
          alert(REDIRECT_MESSAGES.TO_LOBBY);
          navigate(pages.lobby, { replace: true });
        }
      });
  };

  const setLeftbar = (response) => {
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
      navigate(pages.lobby, { replace: true });
    }
  };

  /**
   * =========================== 이벤트 핸들러 =============================
   */
  useEffect(() => {
    findCurrentGameRoomInfo()
      .then(setLeftbar)
      .catch((error) => alert(error));
  }, [location.pathname]);

  useEffect(() => {
    /*
     * TODO canvas 팔레트 서비스 개발 및 조회 API 요청
     */

    return () => {
      leftSidebar.clear();
      rightSidebar.clear();
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
