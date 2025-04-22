import CenterPanel from "@/components/layouts/center-panel/index.jsx";
import { useState } from "react";
import { GitCommit, MessageCircle } from "lucide-react";
import SidePanel from "@/components/layouts/side-panel/index.jsx";
import { Outlet } from "react-router-dom";

export default function MainLayout() {
  /**
   * 게임 방 목록
   */
  const [roomList, setRoomList] = useState([
    { label: "ABCD123456", current: 2, capacity: 5 },
    { label: "WXYZ987654", current: 1, capacity: 4 },
    { label: "QWER112233", current: 5, capacity: 5 },
  ]);

  /**
   * 게임 방에 입장한 유저 목록
   */
  const [enteredUserList, setEnteredUserList] = useState([
    { label: "Alice", color: "#FF5733" },
    { label: "Bob", color: "#33A1FF" },
    { label: "Charlie", color: "#8D33FF" },
  ]);

  return (
    <div className="flex flex-col h-screen bg-gray-900 text-gray-200">
      <div className="flex flex-1 overflow-hidden">
        <SidePanel
          left
          title={{ label: "main", icon: <GitCommit size={20} className="text-gray-400" /> }}
        />

        <CenterPanel>
          <Outlet />
        </CenterPanel>

        <SidePanel
          right
          title={{ label: "chat", icon: <MessageCircle className="text-gray-400" /> }}
        />
      </div>

    </div>
  );
}
