import { useEffect, useState } from 'react';
import Canvas from '@/components/canvas/index.jsx';
import { LobbyPlaceholder } from '@/components/lobby-placeholder/index.jsx';
import MainPanel from '@/components/layouts/center-board/main-panel/index.jsx';
import TopTabs from '@/components/layouts/center-board/top-tabs/index.jsx';

const TopTabsContainer = ({ children }) => {
  return <div className="flex border-b border-gray-700 bg-gray-800">{children}</div>;
};

export default function CenterBoard({ gameRoomEntered = Boolean() }) {
  const [tabs, setTabs] = useState([]);
  const [strokes, setStrokes] = useState([]);
  const [reRenderingSignal, setReRenderingSignal] = useState(false);
  const [selectedTopTabIndex, setSelectedTopTabIndex] = useState(0);

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
    if (gameRoomEntered) {
      /**
       * gameRoomEntered => 캔버스 컬러 팔레트 역할
       */
      setTabs([{ name: "canvas.js" }, { name: "App.jsx" }, { name: "style.css" }]);
    }
  }, []);

  return (
    <div className="flex flex-col flex-1 bg-gray-800 border-r border-gray-700 overflow-hidden">
      <TopTabs
        tabs={tabs}
        selectedIndex={selectedTopTabIndex}
        onSelected={(index) => setSelectedTopTabIndex(index)}
      />

      <MainPanel>
        {gameRoomEntered ? (
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
