import CenterPanel from '@/components/layouts/center-panel/index.jsx';
import { useEffect } from 'react';
import SidePanel from '@/components/layouts/side-panel/index.jsx';
import { Outlet, useLocation } from 'react-router-dom';
import { useApplicationContext } from '@/contexts/index.jsx';
import ItemList from '@/components/layouts/side-panel/item-list/index.jsx';

export default function MainLayout() {
  const location = useLocation();
  const { leftSidebar, topTabs, rightSidebar } = useApplicationContext();

  useEffect(() => {
    return () => {
      leftSidebar.clear();
      topTabs.clear();
    };
  }, [location]);

  return (
    <div className="flex flex-col h-screen bg-gray-900 text-gray-200">
      <div className="flex flex-1 overflow-hidden">
        <SidePanel left title={leftSidebar.title} footer={leftSidebar.footer}>
          <ItemList value={leftSidebar.items} emptyPlaceholder={leftSidebar.emptyPlaceholder} />
        </SidePanel>

        <CenterPanel>
          <Outlet />
        </CenterPanel>

        <SidePanel right title={rightSidebar.title} />
      </div>
    </div>
  );
}
