import LeftSidebar from "@/components/layouts/left-sidebar/index.jsx";
import CenterBoard from "@/components/layouts/center-board/index.jsx";
import Terminal from "@/components/layouts/terminal/index.jsx";
import RightPanel from "@/components/layouts/right-panel/index.jsx";
import { useState } from "react";
import { LobbyPlaceholder } from "@/components/lobby-placeholder/index.jsx";

export default function Main() {
  const [roomList, setRoomList] = useState([{ label: "방1" }, { label: "방2" }, { label: "방3" }]);
  const [gameRoomEntered, setGameRoomEntered] = useState(false);

  return (
    <div className="flex flex-col h-screen bg-gray-900 text-gray-200">
      {/* 메인 위쪽 */}
      <div className="flex flex-1 overflow-hidden">
        <LeftSidebar list={roomList} />

        <CenterBoard />

        {/* 우측 빌드툴 영역 */}
        <RightPanel />
      </div>

      {/* 하단 터미널 (채팅) */}
      {/*<Terminal />*/}
    </div>
  );
}
