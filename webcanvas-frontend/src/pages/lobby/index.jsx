import { LobbyPlaceholder } from "@/components/lobby-placeholder/index.jsx";
import { useEffect, useState } from "react";
import { useApplicationContext } from "@/contexts/index.jsx";
import { game } from "@/api/index.js";
import { useApiLock } from "@/api/lock/index.jsx";
import { EMPTY_MESSAGES } from "@/constants/message.js";
import { useNavigate } from "react-router-dom";
import { pages } from "@/router/index.jsx";
import { GitCommit } from "lucide-react";
import { useApiClient } from "@/contexts/api-client/index.jsx";

export default function LobbyPage() {
  // 전역 context
  const { leftSidebar } = useApplicationContext();
  const { apiClient } = useApiClient();
  // API 중복 요청을 block하기 위한 lock
  const { apiLock } = useApiLock();
  const navigate = useNavigate();

  /**
   * ============== 유저 정의 함수 ===============
   */
  const enterRoom = async (targetRoomId) => {
    const { gameRoomId, gameRoomEntranceId } = await apiLock(
      game.enterGameRoom(targetRoomId),
      async () => await apiClient.post(game.enterGameRoom(targetRoomId))
    );

    moveToGameRoom(gameRoomId);
  };

  const findEnterableGameRooms = async () => {
    const response = await apiLock(
      game.getEnterableRooms,
      async () =>
        await apiClient.get(game.getEnterableRooms).catch(async (error) => {
          if (error.code === "U002") {
            /**
             * 이미 방에 입장한 상태
             * 게임 방 page로 이동
             */
            moveToGameRoom();
          }
        })
    );

    if (response) {
      leftSidebar.setItems(
        response.roomList
          ? response.roomList.map(({ joinCode, enterCount, capacity, gameRoomId }) => ({
              label: "입장 가능",
              current: enterCount,
              isButton: enterCount < capacity,
              capacity: capacity,
              gameRoomId: gameRoomId,
              onClick: () => enterRoom(gameRoomId),
            }))
          : []
      );
    }
  };

  useEffect(() => {
    /**
     * 입장 가능한 방 목록 조회
     */
    findEnterableGameRooms().finally(() => {
      leftSidebar.setTitle({
        label: "main",
        icon: <GitCommit size={20} className="text-gray-400" />,
        button: true,
        onClick: findEnterableGameRooms,
      });
      leftSidebar.setEmptyPlaceholder(EMPTY_MESSAGES.ROOM_LIST);
    });
  }, []);

  /**
   * ========================= 유저 함수 ==========================
   */
  const makeRoom = async () => {
    const response = await apiLock(
      game.createGameRoom,
      async () => await apiClient.post(game.createGameRoom)
    );

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
