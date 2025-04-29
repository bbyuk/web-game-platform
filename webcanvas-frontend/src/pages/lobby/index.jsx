import { LobbyPlaceholder } from "@/components/lobby-placeholder/index.jsx";
import { useEffect, useState } from "react";
import { useApplicationContext } from "@/contexts/index.jsx";
import { game } from '@/api/index.js';
import { useApiLock } from '@/api/lock/index.jsx';

export default function LobbyPage() {
  const { leftSidebar, mock, api } = useApplicationContext();

  const { apiLock } = useApiLock();

  useEffect(() => {
    /**
     * 초기 api 호출
     */

    /**
     * 입장 가능한 방 목록 조회
     */
    api.get(game.getEnterableRooms)
      .then(response => {
        leftSidebar.setItems(response);
      })
      .catch(error => {
        console.log(error);
        console.log(error);
      })

  }, []);

  return (
    <>
      <LobbyPlaceholder className={"w-full h-full"} />
    </>
  );
}
