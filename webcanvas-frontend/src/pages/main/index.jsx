import LNB from "@/components/layouts/lnb/index.jsx";
import EditorArea from "@/components/layouts/editor-area/index.jsx";
import Terminal from "@/components/layouts/terminal/index.jsx";
import RightArea from "@/components/layouts/right-area/index.jsx";
import { useState } from "react";

export default function Main() {
  const [roomList, setRoomList] = useState([{ label: "방1" }, { label: "방2" }, { label: "방3" }]);

  return (
    <div className="flex flex-col h-screen bg-gray-900 text-gray-200">
      {/* 메인 위쪽 */}
      <div className="flex flex-1 overflow-hidden">
        <LNB list={roomList} />

        <EditorArea />

        {/* 우측 빌드툴 영역 */}
        <RightArea />
      </div>

      {/* 하단 터미널 (채팅) */}
      {/*<Terminal />*/}
    </div>
  );
}
