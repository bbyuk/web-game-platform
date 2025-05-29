import { useEffect, useState } from "react";
import { GameRoomWaitingPlaceholder } from "@/components/placeholder/game-room/waiting/index.jsx";
import { useNavigate, useOutletContext, useParams } from "react-router-dom";
import { useLeftSideStore } from "@/stores/layout/leftSideStore.jsx";
import SidePanelFooterButton from "@/components/layouts/side-panel/footer-button/index.jsx";
import { useApiLock } from "@/api/lock/index.jsx";
import { getApiClient } from "@/client/http/index.jsx";
import { game } from "@/api/index.js";
import { pages } from "@/router/index.jsx";

export default function GameRoomWaitingPage() {
  const { roomId } = useParams();

  // Outlet context
  const { enteredUsers, myInfo, changeReadyState, webSocketClientRef } = useOutletContext();
  const leftSideStore = useLeftSideStore();
  const { apiLock } = useApiLock();
  const apiClient = getApiClient();
  const navigate = useNavigate();

  /**
   * =========================== 이벤트 핸들러 =============================
   */

  /**
   * 게임을 시작한다.
   */
  const startGame = () => {
    /**
     * TODO 추후 옵션 제공
     */
    apiLock(
      game.startGame,
      async () =>
        await apiClient
          .post(game.startGame, {
            gameRoomId: roomId,
            turnCount: enteredUsers.length,
            timePerTurn: 90,
          })
          .then((response) => {
            navigate(pages.gameRoom.playing.url(response.gameSessionId, roomId), { replace: true });
          })
    );
  };

  /**
   * 레디 버튼 토글
   */
  const toggleReady = () => {
    apiLock(
      game.updateReady(myInfo.gameRoomEntranceId),
      async () =>
        await apiClient
          .patch(game.updateReady(myInfo.gameRoomEntranceId), {
            ready: !myInfo.ready,
          })
          .then((response) => {
            changeReadyState(response);
          })
    );
  };

  useEffect(() => {
    const buttonOnClickHandler =
      myInfo.role === "HOST" ? startGame : myInfo.role === "GUEST" ? toggleReady : null;

    let status;
    if (myInfo.role === "HOST") {
      if (enteredUsers.filter((user) => !user.ready).length > 0) {
        status = "not-all-ready";
      } else {
        status = "all-ready";
      }
    } else {
      if (myInfo.ready) {
        status = "ready";
      } else {
        status = "not-ready";
      }
    }

    leftSideStore.setFooter({
      slot: SidePanelFooterButton,
      props: {
        status: status,
        onClick: buttonOnClickHandler,
      },
    });
  }, [enteredUsers, myInfo]);

  useEffect(() => {
    return () => {
      leftSideStore.clear();
    };
  }, []);

  /**
   * =========================== 이벤트 핸들러 =============================
   */

  return (
    <GameRoomWaitingPlaceholder
      role={myInfo.role}
      ready={myInfo.ready}
      allGuestsReady={enteredUsers.filter((user) => !user.ready).length === 0}
    />
  );
}
