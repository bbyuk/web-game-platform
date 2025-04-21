import { useEffect, useState } from "react";
import Canvas from "@/components/canvas/index.jsx";
import { LobbyPlaceholder } from "@/components/lobby-placeholder/index.jsx";

const TopTabsContainer = ({ children }) => {
  return <div className="flex border-b border-gray-700 bg-gray-800">{children}</div>;
};

const TopTab = ({ name, selected = false, onClick }) => {
  return (
    <div
      onClick={onClick}
      className={
        selected
          ? "px-4 py-2 bg-gray-900 text-white border-r border-gray-700"
          : "px-4 py-2 bg-gray-800 text-gray-400 hover:text-white hover:bg-gray-700 border-r border-gray-700 cursor-pointer"
      }
    >
      {name}
    </div>
  );
};

const MainPanel = ({ children }) => {
  return (
    <div className="flex-1 justify-center items-center bg-gray-800 relative">
      <div className="p-4 flex justify-center items-center w-full h-full">{children}</div>
    </div>
  );
};

export default function CenterBoard() {
  const [tabs, setTabs] = useState([]);
  const [strokes, setStrokes] = useState([]);
  const [reRenderingSignal, setReRenderingSignal] = useState(false);
  const [selectedFileTabIndex, setSelectedFileTabIndex] = useState(0);
  const [enteredRoom, setEnteredRoom] = useState(true);

  /**
   * 캔버스 컴포넌트 stroke 이벤트 핸들러
   * @param stroke
   */
  const onStrokeHandler = (stroke) => {
    if (stroke.length > 0) {
      setStrokes((prevItems) => [...prevItems, stroke]);
    }
  };

  useEffect(() => {
    setTabs([{ name: "canvas.js" }, { name: "App.jsx" }, { name: "style.css" }]);
  }, []);

  return (
    <div className="flex flex-col flex-1 bg-gray-800 border-r border-gray-700 overflow-hidden">
      {/* 상단 탭 */}
      <TopTabsContainer>
        {tabs.map((tab, index) => (
          <TopTab
            key={`file-tab-${index}`}
            description={tab.description}
            name={tab.name}
            selected={index === selectedFileTabIndex}
            onClick={() => setSelectedFileTabIndex(index)}
          />
        ))}
      </TopTabsContainer>
      <MainPanel>
        {enteredRoom ? (
          <Canvas
            className="flex-1"
            strokes={strokes}
            onStroke={onStrokeHandler}
            reRenderingSignal={reRenderingSignal}
            afterReRendering={() => setReRenderingSignal(false)}
            color={"green"}
          />
        ) : (
          <LobbyPlaceholder className={"w-full h-full"} />
        )}
      </MainPanel>
    </div>
  );
}
