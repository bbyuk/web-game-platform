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
      <div className="w-full max-h-full bg-gray-100 rounded-lg flex justify-center items-center">{children}</div>
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
    <div className="flex flex-col flex-1 bg-gray-800 border-r border-gray-700 overflow-hidden">
      {/* 상단 탭 */}
      <div className="flex border-b border-gray-700 bg-gray-800">
        <div className="px-4 py-2 bg-gray-900 text-white border-r border-gray-700">
          index.js
        </div>
        <div className="px-4 py-2 bg-gray-800 text-gray-400 hover:text-white hover:bg-gray-700 border-r border-gray-700 cursor-pointer">
          App.jsx
        </div>
        <div className="px-4 py-2 bg-gray-800 text-gray-400 hover:text-white hover:bg-gray-700 cursor-pointer">
          styles.css
        </div>
      </div>

      {/* 캔버스 영역 (고정 사이즈) */}
      <div className="flex justify-center items-center flex-1 bg-gray-800 relative">
        <div
          className="bg-white rounded shadow-lg"
          style={{
            width: '800px',  // 고정 크기
            height: '600px', // 고정 크기 (4:3 비율)
          }}
        >
          <canvas id="main-canvas" className="w-full h-full"></canvas>
        </div>
      </div>
    </div>
  );
}
