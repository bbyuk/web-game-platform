import { LobbyPlaceholder } from "@/components/placeholder/lobby/index.jsx";
import { useEffect } from "react";
import { game, user } from "@/api/index.js";
import { useApiLock } from "@/api/lock/index.jsx";
import { EMPTY_MESSAGES } from "@/constants/message.js";
import { useNavigate } from "react-router-dom";
import { pages } from "@/router/index.jsx";
import { GitCommit } from "lucide-react";
import { getApiClient } from "@/client/http/index.jsx";
import { useLeftSideStore } from "@/stores/layout/leftSideStore.jsx";
import ItemList from "@/components/layouts/side-panel/contents/item-list/index.jsx";
import { useUserStore } from "@/stores/user/userStore.jsx";

export default function LobbyPage() {
  // 전역 context
  const apiClient = getApiClient();
  // API 중복 요청을 block하기 위한 lock
  const { apiLock } = useApiLock();
  const navigate = useNavigate();

  const leftSideStore = useLeftSideStore();
  const { userState, setUserState } = useUserStore();
  /**
   * ============== 유저 정의 함수 ===============
   */
  const enterRoom = async (targetRoomId) => {
    const { gameRoomId, gameRoomEntranceId } = await apiLock(
      game.joinGameRoom(targetRoomId),
      async () => await apiClient.post(game.joinGameRoom(targetRoomId))
    );

    moveToGameRoom(gameRoomId);
  };

  const findEnterableGameRooms = async () => {
    const response = await apiLock(
      game.getJoinableRooms,
      async () =>
        await apiClient.get(game.getJoinableRooms).catch(async (error) => {
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
      const leftSideList = response.roomList.map(
        ({ joinCode, enterCount, capacity, gameRoomId }) => ({
          label: "입장 가능",
          current: enterCount,
          isButton: enterCount < capacity,
          capacity: capacity,
          gameRoomId: gameRoomId,
          onClick: () => enterRoom(gameRoomId),
        })
      );
      leftSideStore.setContents({
        slot: ItemList,
        props: {
          value: leftSideList,
          emptyPlaceholder: EMPTY_MESSAGES.ROOM_LIST,
        },
      });
    }
  };

  useEffect(() => {
    leftSideStore.setTitle({
      label: "main",
      icon: <GitCommit size={20} className="text-gray-400" />,
      button: true,
      onClick: findEnterableGameRooms,
    });

    findEnterableGameRooms();
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
    navigate(pages.gameRoom.url(gameRoomId), { replace: true });
  };

  return (
    <>
      <LobbyPlaceholder onMakeRoomRequest={makeRoom} className={"w-full h-full"} />
    </>
  );
}
