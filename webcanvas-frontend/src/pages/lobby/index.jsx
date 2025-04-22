import { LobbyPlaceholder } from "@/components/lobby-placeholder/index.jsx";
import { useEffect, useState } from 'react';
import { useApplicationContext } from '@/contexts/application/index.jsx';

export default function LobbyPage() {
  const { leftSidebar } = useApplicationContext();

  /**
   * 게임 방 목록
   */
  const [roomList, setRoomList] = useState([
    { label: "ABCD123456", current: 2, capacity: 5, isButton: true },
    { label: "WXYZ987654", current: 1, capacity: 4, isButton: true },
    { label: "QWER112233", current: 5, capacity: 5, isButton: false },
  ]);

  useEffect(() => {
    leftSidebar.setItems(roomList);
  }, []);

  return (
    <>
      <LobbyPlaceholder className={"w-full h-full"} />
    </>
  );
}
