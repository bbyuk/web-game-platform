import { LobbyPlaceholder } from "@/components/lobby-placeholder/index.jsx";
import { useEffect, useState } from "react";
import { useApplicationContext } from "@/contexts/index.jsx";
import { game } from "@/api/index.js";
import { useApiLock } from "@/api/lock/index.jsx";
import { EMPTY_MESSAGES } from "@/constants/message.js";
import { useNavigate } from "react-router-dom";
import { pages } from "@/router/index.jsx";

export default function LobbyPage() {
  // 전역 context
  const { leftSidebar, api, currentGame } = useApplicationContext();
  // API 중복 요청을 block하기 위한 lock
  const { apiLock } = useApiLock();
  const navigate = useNavigate();

  /**
   * ============== 유저 정의 함수 ===============
   */
  const enterRoom = async (targetRoomId) => {
    const { gameRoomId, gameRoomEntranceId } = await apiLock(
      game.enterGameRoom(targetRoomId),
      async () => await api.post(game.enterGameRoom(targetRoomId))
    );

    moveToGameRoom(gameRoomId);
  };

  useEffect(() => {
    if (currentGame.gameRoomId && currentGame.gameRoomEntranceId) {
      /**
       * 이미 방에 입장한 상태
       * 게임 방 page로 이동
       */
      moveToGameRoom(currentGame.gameRoomId);
    }

    /**
     * 초기 api 호출
     */

    /**
     * 입장 가능한 방 목록 조회
     */
    leftSidebar.setEmptyPlaceholder(EMPTY_MESSAGES.ROOM_LIST);
    api
      .get(game.getEnterableRooms)
      .then((response) => {
        leftSidebar.setItems(
          response.roomList
            ? response.roomList.map(({ joinCode, enterCount, capacity, gameRoomId }) => ({
                label: joinCode,
                current: enterCount,
                isButton: enterCount < capacity,
                capacity: capacity,
                gameRoomId: gameRoomId,
                onClick: () => enterRoom(gameRoomId),
              }))
            : []
        );
      })
      .catch(async (error) => {
        if (error.code === "U002") {
          /**
           * 이미 방에 입장한 상태
           * 게임 방 page로 이동
           */
          moveToGameRoom();
        }
      });
  }, []);

  /**
   * ========================= 유저 함수 ==========================
   */
  const makeRoom = async () => {
    const response = await apiLock(
      game.createGameRoom,
      async () => await api.post(game.createGameRoom)
    );

    currentGame.setEntranceInfo(response);
    moveToGameRoom(response.gameRoomId);
  };

  const moveToGameRoom = (gameRoomId = "temp") => {
    navigate(`${pages.gameRoom}/${gameRoomId}`, { replace: true });
  };

  return (
    <>
      <LobbyPlaceholder onMakeRoomRequest={makeRoom} className={"w-full h-full"} />
    </>
  );
}
