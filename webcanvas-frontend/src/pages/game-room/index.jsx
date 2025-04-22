import Canvas from "@/components/canvas/index.jsx";
import TopTabs from "@/components/layouts/center-panel/top-tabs/index.jsx";
import { useEffect, useState } from 'react';
import { useApplicationContext } from '@/contexts/application/index.jsx';

export default function GameRoomPage() {
  /**
   * 현재 캔버스의 획 모음
   */
  const [strokes, setStrokes] = useState([]);

  /**
   * 캔버스 온디맨드 리렌더링 시그널
   */
  const [reRenderingSignal, setReRenderingSignal] = useState(false);

  /**
   * 상단 탭 전역 컨텍스트
   */
  const { topTabs } = useApplicationContext();


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


  /**
   * =========================== 이벤트 핸들러 =============================
   */
  useEffect(() => {
    topTabs.setValue([{label: "black"}, {label: "blue"}, {label: "green"}, {label: "red"}, {label: "yellow"}]);

    return () => {
      topTabs.clear();
    };
  }, []);

  return (
    <Canvas
      className="flex-1"
      strokes={strokes}
      onStroke={onStrokeHandler}
      reRenderingSignal={reRenderingSignal}
      afterReRendering={onReRenderingHandler}
      color={topTabs.items[topTabs.selectedIndex] ? topTabs.items[topTabs.selectedIndex].label : "black"}
    />
  );
}
