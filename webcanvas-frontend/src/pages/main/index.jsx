import LeftSidebar from '@/components/layouts/left-sidebar/index.jsx';
import CenterBoard from '@/components/layouts/center-board/index.jsx';
import RightPanel from '@/components/layouts/right-panel/index.jsx';
import { useState } from 'react';

export default function Main() {
  /**
   * 게임 방 목록
   */
  const [roomList, setRoomList] = useState([{ label: "방1" }, { label: "방2" }, { label: "방3" }]);

  /**
   * 현재 게임 방에 입장해있는지 여부
   */
  const [gameRoomEntered, setGameRoomEntered] = useState(false);

  /**
   * 현재 캔버스의 획 모음
   */
  const [strokes, setStrokes] = useState([]);

  /**
   * 캔버스 온디맨드 리렌더링 시그널
   */
  const [reRenderingSignal, setReRenderingSignal] = useState(false);



  /**
   * =========================== 이벤트 핸들러 =============================
   */
  /**
   * 캔버스 컴포넌트 stroke 이벤트 핸들러
   * @param stroke
   */
  const onStrokeHandler = (stroke) => {
    if (stroke.length > 0) {
      setStrokes((prevItems) => [...prevItems, stroke]);
    }
  };

  /**
   * 캔버스 컴포넌트 리렌더링 이벤트 핸들러
   */
  const onReRenderingHandler = () => {
    setReRenderingSignal(false);
  };

  return (
    <div className="flex flex-col h-screen bg-gray-900 text-gray-200">
      {/* 메인 위쪽 */}
      <div className="flex flex-1 overflow-hidden">
        <LeftSidebar list={roomList} />

        <CenterBoard gameRoomEntered={gameRoomEntered}
                     strokes={strokes}
                     onStroke={onStrokeHandler}
                     reRenderingSignal={reRenderingSignal}
                     onReRendering={onReRenderingHandler}
                    />

        {/* 우측 빌드툴 영역 */}
        <RightPanel />
      </div>

      {/* 하단 터미널 (채팅) */}
      {/*<Terminal />*/}
    </div>
  );
}
