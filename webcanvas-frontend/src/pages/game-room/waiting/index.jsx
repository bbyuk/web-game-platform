import { useEffect } from "react";
import { GameRoomWaitingPlaceholder } from "@/components/placeholder/game-room/waiting/index.jsx";
import { useOutletContext } from "react-router-dom";
import { useLeftSideStore } from "@/stores/layout/leftSideStore.jsx";
import SidePanelFooterButton from "@/components/layouts/side-panel/footer-button/index.jsx";

export default function GameRoomWaitingPage() {
  // Outlet context
  const { enteredUsers, nickname, userColor, webSocketClientRef } = useOutletContext();
  const leftSideStore = useLeftSideStore();

  /**
   * =========================== 이벤트 핸들러 =============================
   */

  /**
   * 게임을 시작한다.
   */
  const startGame = () => {
    alert("게임 시작");
  };

  useEffect(() => {
    leftSideStore.setFooter({
      slot: SidePanelFooterButton,
      props: {
        label: "게임 시작",
        onClick: startGame,
        disabled: enteredUsers.length === 1,
      },
    });
  }, []);

  /**
   * =========================== 이벤트 핸들러 =============================
   */

  return <GameRoomWaitingPlaceholder />;
}
