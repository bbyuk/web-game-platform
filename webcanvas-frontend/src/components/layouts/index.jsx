import CenterPanel from "@/components/layouts/center-panel/index.jsx";
import { useState } from "react";
import { GitCommit, MessageCircle } from "lucide-react";
import SidePanel from "@/components/layouts/side-panel/index.jsx";
import { Outlet } from "react-router-dom";

export default function MainLayout() {
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
          title={{ label: "chat", icon: <MessageCircle size={20} className="text-gray-400" /> }}
        />
      </div>
    </div>
  );
}
