import CenterPanel from "@/components/layouts/center-panel/index.jsx";
import SidePanel from "@/components/layouts/side-panel/index.jsx";
import { Outlet } from "react-router-dom";

export default function MainLayout() {
  return (
    <div className="flex flex-col h-screen bg-gray-900 text-gray-200">
      <div className="flex flex-1 overflow-hidden">
        <SidePanel position={"left"} />

        <CenterPanel>
          <Outlet />
        </CenterPanel>

        <SidePanel position={"right"} />
      </div>
    </div>
  );
}
