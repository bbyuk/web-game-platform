import CenterPanel from "@/components/layouts/center-panel/index.jsx";
import SidePanel from "@/components/layouts/side-panel/index.jsx";
import { Outlet } from "react-router-dom";
import { useClientStore } from '@/stores/client/clientStore.jsx';
import LoadingOverlay from '@/components/loading/index.jsx';

export default function MainLayout() {
  const { isLoading, startLoading, endLoading } = useClientStore();

  return (
    <div className="flex flex-col h-screen bg-gray-900 text-gray-200">
      { isLoading && <LoadingOverlay />}

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
