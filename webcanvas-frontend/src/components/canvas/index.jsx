import {useEffect, useRef, useState} from "react";

export default function Canvas({
                                 strokes = [],
                                 onStroke = (stroke) => {
                                 },
                                 tool = "pen",
                                 color = "black",
                                 lineWidth = 5,
                                 reRenderingSignal = false,
                                 drawable = false,
                                 afterReRendering = () => {
                                 },
                                 className = String(),
                               }) {
  const initialStroke = {
    color: color,
    lineWidth: lineWidth,
    points: [],
  };

  const elementId = "canvas";
  /**
   * canvas ref
   * @type {React.RefObject<null>}
   */
  const canvasRef = useRef(null);
  const contextRef = useRef(null);

  /**
   * 현재 마우스를 누르고 그리고 있는 중인지 여부
   */
  const [dragging, setDragging] = useState(false);
  /**
   * Stroke 이벤트마다 집계되는 stroke 획 state
   */
  const [currentStroke, setCurrentStroke] = useState(initialStroke);

  /**
   * resize 이벤트 디바운스 타이머
   * @type {React.RefObject<null>}
   */
  const resizeTimer = useRef(null);

  /**
   * 현재 브라우저 윈도우가 resizing 중인지 여부
   */
  const [resizing, setResizing] = useState(false);

  /**
   * ====================================== 이벤트 핸들러 ======================================
   */
  /**
   * 마우스 이동 이벤트 핸들러
   * @param event
   */
  const onMouseMove = (event) => {
    const ctx = contextRef.current;
    if (!ctx) {
      return;
    }

    const rect = canvasRef.current.getBoundingClientRect();
    const offsetX = event.clientX - rect.left;
    const offsetY = event.clientY - rect.top;

    const scaledX = offsetX / canvasRef.current.width;
    const scaledY = offsetY / canvasRef.current.height;

    if (dragging) {
      ctx.lineTo(offsetX, offsetY);
      ctx.stroke();

      // setCurrentStroke((prevItems) => [...prevItems, { x: scaledX, y: scaledY, color: color }]);

      setCurrentStroke({
        tool: tool,
        color: color,
        lineWidth: lineWidth,
        points: [...currentStroke.points, {x: scaledX, y: scaledY}],
      });
    } else {
      ctx.beginPath();
      ctx.moveTo(offsetX, offsetY);

      onStroke(currentStroke);
      setCurrentStroke(initialStroke);
    }
  };

  const onResize = () => {
    if (resizeTimer.current) {
      clearTimeout(resizeTimer.current);
    }

    setResizing(true);

    // 200ms 동안 resize 이벤트가 더이상 발생하지 않으면 resize 작업 수행
    resizeTimer.current = setTimeout(() => {
      setResizing(false);
    }, 500);
  };
  /**
   * ====================================== 이벤트 핸들러 ======================================
   */

  /**
   * ====================================== 커스텀 메소드 ======================================
   */

  /**
   * 캔버스 reRendering
   */
  const reRendering = () => {
    const ctx = contextRef.current;
    if (!ctx) {
      return;
    }

    ctx.clearRect(0, 0, canvasRef.current.width, canvasRef.current.height);

    const canvasWidth = canvasRef.current.width;
    const canvasHeight = canvasRef.current.height;

    strokes.forEach((stroke) => {
      // 상태 저장
      ctx.save();

      // 지우개 모드인지 펜 모드인지 분기
      if (stroke.tool === 'eraser') {
        ctx.globalCompositeOperation = 'destination-out';
      } else {
        ctx.globalCompositeOperation = 'source-over';
        ctx.strokeStyle = stroke.color;
      }
      // 획 굵기
      ctx.lineWidth = stroke.lineWidth;

      ctx.beginPath();
      ctx.moveTo(stroke.points[0].x * canvasWidth, stroke.points[0].y * canvasHeight);

      stroke.points.forEach((point, index) => {
        if (index === 0) {
          return;
        }
        ctx.lineTo(point.x * canvasWidth, point.y * canvasHeight);
      });

      ctx.stroke();
      // 상태 복구
      ctx.restore();
    });
  };

  /**
   * 화면에 그림 그리기 시작 (마우스 클릭 시작)
   */
  const startPainting = () => {
    if (!drawable) {
      return;
    }
    setDragging(true);
  };
  /**
   * 화면에 그림 그리기 종료 (마우스 클릭 종료)
   */
  const stopPainting = () => {
    setDragging(false);
  };

  const initCanvas = () => {
    const canvas = canvasRef.current;
    const parent = canvas.parentElement;
    const ctx = canvas.getContext("2d");

    contextRef.current = ctx;

    const {clientWidth, clientHeight} = parent;

    // 실제 캔버스 내부 크기 설정 (픽셀 크기)
    canvas.style.width = "100%";
    canvas.style.height = "100%";

    canvas.width = clientWidth;
    canvas.height = clientHeight;

    ctx.lineWidth = lineWidth;
    ctx.lineCap = "round";
    ctx.strokeStyle = color;
  };

  /**
   * ====================================== 커스텀 메소드 ======================================
   */

  /**
   * onComponentLoad
   */
  useEffect(() => {
    window.addEventListener("resize", onResize);

    return () => {
      window.removeEventListener("resize", onResize);
    };
  }, []);

  /**
   * 부모 컴포넌트에서 온디맨드 리렌더링 처리
   */
  useEffect(() => {
    if (!contextRef.current) {
      return;
    }

    if (reRenderingSignal) {
      reRendering();
      afterReRendering();
    }
  }, [reRenderingSignal, afterReRendering]);

  /**
   * 컬러 변경
   */
  useEffect(() => {
    if (!contextRef.current) {
      return;
    }
    contextRef.current.strokeStyle = color;
  }, [color]);

  /**
   * lineWidth 변경
   */
  useEffect(() => {
    if (!contextRef.current) {
      return;
    }
    contextRef.current.lineWidth = lineWidth;
  }, [lineWidth]);

  /**
   * resizing handling
   */
  useEffect(() => {
    if (!resizing) {
      initCanvas();
      reRendering();
    }
  }, [resizing]);

  useEffect(() => {
    if (!contextRef.current) {
      return;
    }
    if (tool === "eraser") {
      contextRef.current.strokeStyle = "#FFFFFF";
    } else if (tool === "pen") {
      contextRef.current.strokeStyle = color;
    }
  }, [tool]);

  // 툴별 커서 URL 지정 (커서 파일은 public/cursors 폴더 등에 두고 가져온다고 가정)
  const cursorUrl = tool
    ? `url('/cursors/${tool}.cur') 0 0, auto`
    : "default"


  return (
    <div className="flex justify-center items-center flex-1 overflow-hidden bg-gray-800">
      <div className="relative aspect-[4/3] w-full max-w-[calc(100vh*4/3)] max-h-[80vh] bg-white rounded shadow-lg">
        <canvas
          id={elementId}
          ref={canvasRef}
          onMouseMove={onMouseMove}
          onMouseDown={startPainting}
          onMouseUp={stopPainting}
          onMouseLeave={stopPainting}
          className="w-full h-full"
          style={{ cursor: cursorUrl }}
        />
      </div>
    </div>
  );
}
