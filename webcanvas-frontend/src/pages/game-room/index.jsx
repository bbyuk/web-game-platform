import Canvas from "@/components/canvas/index.jsx";
import { useEffect, useState } from "react";
import { useApplicationContext } from "@/contexts/index.jsx";
import { EMPTY_MESSAGES } from "@/constants/message.js";
import { useLocation, useNavigate } from "react-router-dom";
import { game } from "@/api/index.js";
import { useApiLock } from "@/api/lock/index.jsx";
import { pages } from "@/router/index.jsx";

export default function GameRoomPage() {
  // 현재 캔버스의 획 모음
  const [strokes, setStrokes] = useState([]);

  //캔버스 온디맨드 리렌더링 시그널
  const [reRenderingSignal, setReRenderingSignal] = useState(false);

  // 전역 컨텍스트
  const { api, topTabs, leftSidebar, currentGame } = useApplicationContext();

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
   * =========================== 이벤트 핸들러 =============================
   */
  useEffect(() => {
    leftSidebar.setEmptyPlaceholder(EMPTY_MESSAGES.ENTERED_USER_LIST);

    if (!(currentGame.gameRoomId && currentGame.gameRoomEntranceId)) {
      // 방 정보 조회
      api
        .get(game.getCurrentEnteredGameRoom)
        .then((response) => {
          currentGame.setEntranceInfo(response);
          navigate(`${pages.gameRoom}/${response.gameRoomId}`, { replace: true });
        })
        .catch((error) => {
          alert("테스트 alert => 에러발생");
          console.log(error);
        });
    } else {
      alert(`${currentGame.gameRoomId} 방에 입장했습니다.`);
    }

    /**
     * TODO canvas 팔레트 서비스 개발 및 조회 API 요청
     */
  }, [location.pathname]);

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
