import { useEffect } from "react";
import { GameRoomWaitingPlaceholder } from "@/components/placeholder/game-room/waiting/index.jsx";
import { useOutletContext } from "react-router-dom";
import { useLeftSideStore } from "@/stores/layout/leftSideStore.jsx";
import SidePanelFooterButton from "@/components/layouts/side-panel/footer-button/index.jsx";

export default function GameRoomWaitingPage() {
  // Outlet context
  const { enteredUsers, myInfo, webSocketClientRef } = useOutletContext();
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
    const label = myInfo.role === "HOST" ? "START" : myInfo.role === "GUEST" ? "READY" : "";

    const disabled = myInfo.role === "HOST" ? enteredUsers.length === 1 : myInfo.role !== "GUEST";

    leftSideStore.setFooter({
      slot: SidePanelFooterButton,
      props: {
        label: label,
        onClick: startGame,
        disabled: disabled,
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
