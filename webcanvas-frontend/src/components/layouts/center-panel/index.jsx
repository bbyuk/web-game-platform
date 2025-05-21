import MainPanel from "@/components/layouts/center-panel/main-panel/index.jsx";
import TopTabs from "@/components/layouts/center-panel/top-tabs/index.jsx";
// import { useApplicationContext } from "@/contexts/index.jsx";

export default function CenterPanel({ children }) {
  /**
   * canvas context
   */
  // const { topTabs } = useApplicationContext();

  const { topTabs } = {};

  return (
    <div className="flex flex-col flex-1 bg-gray-800 border-r border-gray-700 overflow-hidden">
      {/*<TopTabs*/}
      {/*  tabs={topTabs.items}*/}
      {/*  onSelected={topTabs.onSelected}*/}
      {/*  selectedIndex={topTabs.selectedIndex}*/}
      {/*/>*/}

      <MainPanel>{children}</MainPanel>
    </div>
  );
}
