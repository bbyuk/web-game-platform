import { useEffect, useState } from "react";
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
