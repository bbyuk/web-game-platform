import { useEffect, useState } from 'react';
import Canvas from '@/components/canvas/index.jsx';
import { LobbyPlaceholder } from '@/components/lobby-placeholder/index.jsx';
import MainPanel from '@/components/layouts/center-board/main-panel/index.jsx';
import TopTabs from '@/components/layouts/center-board/top-tabs/index.jsx';

export default function CenterBoard({
                                      gameRoomEntered = Boolean(),
                                      strokes = [],
                                      onStroke = (stroke) => {
                                      },
                                      reRenderingSignal = false,
                                      onReRendering = () => {
                                      }
                                    }) {
  const [tabs, setTabs] = useState([]);
  const [selectedTopTabIndex, setSelectedTopTabIndex] = useState(0);

  useEffect(() => {
    if (gameRoomEntered) {
      /**
       * gameRoomEntered => 캔버스 컬러 팔레트 역할
       */
      setTabs([{ label: 'black' }, { label: 'blue' }, { label: 'green' }, { label: 'red' }, { label: 'yellow' }]);
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
            onStroke={onStroke}
            reRenderingSignal={reRenderingSignal}
            afterReRendering={onReRendering}
            color={tabs[selectedTopTabIndex] ? tabs[selectedTopTabIndex].label : "black"}
          />
        ) : (
          <LobbyPlaceholder className={'w-full h-full'} />
        )}
      </MainPanel>
    </div>
  );
}
