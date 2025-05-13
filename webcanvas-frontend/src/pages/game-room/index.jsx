import Canvas from "@/components/canvas/index.jsx";
import { useEffect, useState } from "react";
import { useApplicationContext } from "@/contexts/index.jsx";
import { EMPTY_MESSAGES, REDIRECT_MESSAGES } from "@/constants/message.js";
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
  const { api, topTabs, leftSidebar, currentGame, utils } = useApplicationContext();

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

  const findCurrentGameInfo = async () => {
    // 방 정보 조회
    return await api
      .get(game.getCurrentEnteredGameRoom)
      .then((response) => {
        currentGame.setEntranceInfo(response);
        if (location.pathname !== `${pages.gameRoom}/${response.gameRoomId}`) {
          utils.redirectTo(`${pages.gameRoom}/${response.gameRoomId}`);
        }
        return response;
      })
      .catch((error) => {
        if (error.code === "R003") {
          // 로비로 이동
          alert(REDIRECT_MESSAGES.TO_LOBBY);
          utils.redirectTo(pages.lobby);
        }
      });
  };

  /**
   * =========================== 이벤트 핸들러 =============================
   */
  useEffect(() => {
    // redirect에 의한 set
    if (currentGame.gameRoomId && currentGame.gameRoomEntranceId) {
      if (currentGame.enteredUsers) {
        leftSidebar.setItems(
          currentGame.enteredUsers.map(({ nickname, ...rest }) => ({ label: nickname, ...rest }))
        );
      } else {
        leftSidebar.setItems(EMPTY_MESSAGES.ENTERED_USER_LIST);
      }
    }
    console.log(currentGame);
  }, [location.pathname]);

  useEffect(() => {
    findCurrentGameInfo().then((response) => {
      if (response.enteredUsers) {
        leftSidebar.setItems(
          response.enteredUsers.map(({ nickname, ...rest }) => ({ label: nickname, ...rest }))
        );
      } else {
        leftSidebar.setItems(EMPTY_MESSAGES.ENTERED_USER_LIST);
      }
    });

    /*
     * TODO canvas 팔레트 서비스 개발 및 조회 API 요청
     */
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
