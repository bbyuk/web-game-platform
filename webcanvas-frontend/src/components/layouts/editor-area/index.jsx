import { useEffect, useState } from "react";
import Canvas from "@/components/canvas/index.jsx";

const FileTabContainer = ({ children }) => {
  return (
    <div className="h-10 flex items-center space-x-2 px-4 border-b border-gray-700 bg-gray-900">
      {children}
    </div>
  );
};

const FileTab = ({ name, selected = false, onClick }) => {
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

const CanvasContainer = ({ children }) => {
  return (
    // <div className="flex-1 p-4 overflow-auto">
    //   <div className="w-full max-h-full bg-gray-100 rounded-lg flex justify-center items-center">{children}</div>
    // </div>
    <div className="flex justify-center items-center flex-1 bg-gray-800 relative">
      <div
        className="bg-white rounded shadow-lg"
        style={{
          width: "800px", // 고정 크기
          height: "600px", // 고정 크기 (4:3 비율)
        }}
      >
        {children}
      </div>
    </div>
  );
};

export default function EditorArea() {
  const [tabs, setTabs] = useState([]);
  const [strokes, setStrokes] = useState([]);
  const [reRenderingSignal, setReRenderingSignal] = useState(false);
  const [selectedFileTabIndex, setSelectedFileTabIndex] = useState(0);
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
      <div className="flex border-b border-gray-700 bg-gray-800">
        {tabs.map((tab, index) => (
          <FileTab
            key={`file-tab-${index}`}
            description={tab.description}
            name={tab.name}
            selected={index === selectedFileTabIndex}
            onClick={() => setSelectedFileTabIndex(index)}
          />
        ))}
        {/*<div className="px-4 py-2 bg-gray-900 text-white border-r border-gray-700">*/}
        {/*  index.js*/}
        {/*</div>*/}
        {/*<div className="px-4 py-2 bg-gray-800 text-gray-400 hover:text-white hover:bg-gray-700 border-r border-gray-700 cursor-pointer">*/}
        {/*  App.jsx*/}
        {/*</div>*/}
        {/*<div className="px-4 py-2 bg-gray-800 text-gray-400 hover:text-white hover:bg-gray-700 cursor-pointer">*/}
        {/*  styles.css*/}
        {/*</div>*/}
      </div>
      {/* 캔버스 영역 (고정 사이즈) */}
      <CanvasContainer>
        <Canvas className="w-full h-full" />
      </CanvasContainer>
    </div>
  );
}
