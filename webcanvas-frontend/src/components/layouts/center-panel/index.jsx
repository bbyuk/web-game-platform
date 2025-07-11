import MainPanel from "@/components/layouts/center-panel/main-panel/index.jsx";

export default function CenterPanel({ children, className = "" }) {
  return (
    <div
      className={
        `flex flex-col flex-1 h-full bg-gray-800 border-r border-gray-700 overflow-hidden ` +
        className
      }
    >
      {/* MainPanel을 flex-col overflow-auto로 감싸서 toolbar+canvas가 세로로 쌓이고 스크롤 됩니다. */}
      <MainPanel className="flex flex-col flex-1 overflow-auto">
        {children}
      </MainPanel>
    </div>
  );
}
