import { useEffect } from "react";
import { GameRoomWaitingPlaceholder } from "@/components/placeholder/game-room/waiting/index.jsx";
import { useOutletContext } from "react-router-dom";
import { useLeftSideStore } from "@/stores/layout/leftSideStore.jsx";
import SidePanelFooterButton from "@/components/layouts/side-panel/footer-button/index.jsx";
import { useApiLock } from "@/api/lock/index.jsx";
import { getApiClient } from "@/client/http/index.jsx";
import { game } from "@/api/index.js";

export default function GameRoomWaitingPage() {
  // Outlet context
  const { enteredUsers, myInfo, changeReadyState, webSocketClientRef } = useOutletContext();
  const leftSideStore = useLeftSideStore();
  const { apiLock } = useApiLock();
  const apiClient = getApiClient();

  /**
   * =========================== 이벤트 핸들러 =============================
   */

  /**
   * 게임을 시작한다.
   */
  const startGame = () => {
    alert("게임 시작");
  };

  /**
   * 레디 버튼 토글
   */
  const toggleReady = () => {
    apiClient
      .patch(game.updateReady(myInfo.gameRoomEntranceId), {
        ready: !myInfo.ready,
      })
      .then((response) => {
        changeReadyState(response);
      });
  };

  useEffect(() => {
    const buttonOnClickHandler =
      myInfo.role === "HOST" ? startGame : myInfo.role === "GUEST" ? toggleReady : null;
    const status =
      myInfo.role === "HOST"
        ? "start"
        : myInfo.role === "GUEST" && myInfo.ready
          ? "ready"
          : myInfo.role === "GUEST" && !myInfo.ready
            ? "not-ready"
            : null;

    leftSideStore.setFooter({
      slot: SidePanelFooterButton,
      props: {
        status: status,
        onClick: buttonOnClickHandler,
      },
    });

    return () => {
      leftSideStore.clear();
    };
  }, [enteredUsers, myInfo]);

  /**
   * =========================== 이벤트 핸들러 =============================
   */

  return <GameRoomWaitingPlaceholder />;
}
