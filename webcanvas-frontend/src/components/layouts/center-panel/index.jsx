import MainPanel from "@/components/layouts/center-panel/main-panel/index.jsx";

export default function CenterPanel({children}) {

  return (
    <div className="flex flex-col flex-1 bg-gray-800 border-r border-gray-700 overflow-hidden">
      <MainPanel>
        {children}
      </MainPanel>
    </div>
  );
}
