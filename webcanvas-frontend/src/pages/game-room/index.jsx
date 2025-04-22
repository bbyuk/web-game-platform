import Canvas from "@/components/canvas/index.jsx";
import TopTabs from "@/components/layouts/center-panel/top-tabs/index.jsx";
import {useState} from "react";

export default function GameRoomPage() {
  /**
   * gameRoomEntered => 캔버스 컬러 팔레트 역할
   */
  const [tabs, setTabs] = useState([
    { label: "black" },
    { label: "blue" },
    { label: "green" },
    { label: "red" },
    { label: "yellow" },
  ]);
  /**
   * 선택된 상단 팔레트 탭
   */
  const [selectedTopTabIndex, setSelectedTopTabIndex] = useState(0);

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


  return (<>
    <TopTabs
      tabs={tabs}
      selectedIndex={selectedTopTabIndex}
      onSelected={(index) => setSelectedTopTabIndex(index)}
    />

    <Canvas
      className="flex-1"
      strokes={strokes}
      onStroke={onStrokeHandler}
      reRenderingSignal={reRenderingSignal}
      afterReRendering={onReRenderingHandler}
      color={tabs[selectedTopTabIndex] ? tabs[selectedTopTabIndex].label : "black"}
    />
  </>);
}
