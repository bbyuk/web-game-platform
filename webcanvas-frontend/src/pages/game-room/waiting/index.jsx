import { useEffect } from "react";
import { useApplicationContext } from "@/contexts/index.jsx";
import SidePanelFooterButton from "@/components/layouts/side-panel/footer-button/index.jsx";
import { GameRoomWaitingPlaceholder } from "@/components/placeholder/game-room/waiting/index.jsx";
import { useOutletContext } from "react-router-dom";

export default function GameRoomWaitingPage() {
  // 전역 컨텍스트
  const { leftSidebar } = useApplicationContext();
  const { enteredUsers, nickname, userColor, webSocketClientRef } = useOutletContext();

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
    leftSidebar.setFooter(
      <SidePanelFooterButton
        label={"게임 시작"}
        onClick={startGame}
        disabled={enteredUsers.length === 1}
      />
    );
  }, []);

  /**
   * =========================== 이벤트 핸들러 =============================
   */

  return <GameRoomWaitingPlaceholder />;
}
