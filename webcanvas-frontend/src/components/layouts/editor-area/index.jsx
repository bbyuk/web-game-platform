import { useEffect, useState } from "react";
import Canvas from "@/components/canvas/index.jsx";

const FileTabContainer = ({ children }) => {
  return (
    <div className="h-10 flex items-center space-x-2 px-4 border-b border-gray-700 bg-gray-900">
      <div className="flex items-center bg-gray-800 rounded-t px-3 py-1 shadow-sm">{children}</div>
    </div>
  );
};

const FileTab = ({ description, name }) => {
  return (
    <>
      <span className="material-icons mr-2 text-gray-400 text-sm">{description}</span>
      <span className="text-sm">{name}</span>
    </>
  );
};

const CanvasContainer = ({ children }) => {
  return (
    <div className="flex-1 p-4 overflow-auto">
      <div className="w-full h-full bg-gray-100 rounded-lg">{children}</div>
    </div>
  );
};

export default function EditorArea() {
  const [tabs, setTabs] = useState([]);
  const [strokes, setStrokes] = useState([]);
  const [reRenderingSignal, setReRenderingSignal] = useState(false);

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
    setTabs([{ description: "description", name: "canvas.js" }]);
  }, []);

  return (
    <div className="flex-1 flex flex-col bg-gray-800">
      {/* 파일 탭 */}
      <FileTabContainer>
        {tabs.map((tab, index) => (
          <FileTab key={`tab-${index}`} description={tab.description} name={tab.name} />
        ))}
      </FileTabContainer>
      {/* 코드 캔버스 */}
      <CanvasContainer>
        <Canvas
          strokes={strokes}
          onStroke={onStrokeHandler}
          reRenderingSignal={reRenderingSignal}
          afterReRendering={() => setReRenderingSignal(false)}
          color={"green"}
        />
      </CanvasContainer>
    </div>
  );
}
