import { useEffect, useRef, useState } from "react";

export default function Canvas({
  strokes = [],
  onStroke = (stroke) => {},
  color = "black",
  reRenderingSignal = false,
  afterReRendering = () => {},
  className = new String(),
}) {
  const elementId = "canvas";
  /**
   * canvas ref
   * @type {React.RefObject<null>}
   */
  const canvasRef = useRef(null);

  /**
   * resize timer
   * @type {React.RefObject<null>}
   */
  const resizeTimer = useRef(null);

  /**
   * 현재 마우스를 누르고 그리고 있는 중인지 여부
   */
  const [painting, setPainting] = useState(false);
  /**
   * Stroke 이벤트마다 집계되는 stroke 획 state
   */
  const [currentStroke, setCurrentStroke] = useState([]);

  /**
   * ====================================== 이벤트 핸들러 ======================================
   */
  /**
   * 마우스 이동 이벤트 핸들러
   * @param event
   */
  const onMouseMove = (event) => {
    const ctx = getCanvasContext();
    if (!ctx) {
      return;
    }

    const rect = canvasRef.current.getBoundingClientRect();
    const offsetX = event.clientX - rect.left;
    const offsetY = event.clientY - rect.top;

    const scaledX = offsetX / canvasRef.current.width;
    const scaledY = offsetY / canvasRef.current.height;

    if (painting) {
      ctx.lineTo(offsetX, offsetY);
      ctx.stroke();
      setCurrentStroke((prevItems) => [...prevItems, { x: scaledX, y: scaledY }]);
    } else {
      ctx.beginPath();
      ctx.moveTo(offsetX, offsetY);

      onStroke(currentStroke);
      setCurrentStroke([]);
    }
  };
  /**
   * ====================================== 이벤트 핸들러 ======================================
   */

  /**
   * ====================================== 커스텀 메소드 ======================================
   */

  /**
   * canvas context 리턴
   */
  const getCanvasContext = () => {
    const canvas = canvasRef.current;
    return canvas.getContext("2d");
  };

  /**
   * 캔버스 reRendering
   */
  const reRendering = () => {
    const ctx = getCanvasContext();
    if (!ctx) {
      return;
    }

    ctx.clearRect(0, 0, canvasRef.current.width, canvasRef.current.height);

    const canvasWidth = canvasRef.current.width;
    const canvasHeight = canvasRef.current.height;

    strokes.forEach((stroke) => {
      ctx.beginPath();
      ctx.moveTo(stroke[0].x * canvasWidth, stroke[0].y * canvasHeight);

      stroke.forEach((point, index) => {
        if (index === 0) {
          return;
        }
        ctx.lineTo(point.x * canvasWidth, point.y * canvasHeight);
      });

      ctx.stroke();
    });
  };

  /**
   * canvas resize
   */
  const resize = () => {
    if (resizeTimer.current) {
      clearTimeout(resizeTimer.current);
    }
    resizeTimer.current = setTimeout(() => {
      console.log(strokes);
      reRendering();
    }, 200); // 200ms 후에 한번만 리렌더
  };

  /**
   * 화면에 그림 그리기 시작 (마우스 클릭 시작)
   */
  const startPainting = () => {
    setPainting(true);
  };
  /**
   * 화면에 그림 그리기 종료 (마우스 클릭 종료)
   */
  const stopPainting = () => {
    setPainting(false);
  };

  /**
   * ====================================== 커스텀 메소드 ======================================
   */

  /**
   * onComponentLoad
   */
  useEffect(() => {
    const canvas = canvasRef.current;
    const parent = canvas.parentElement;

    const ctx = getCanvasContext();

    const { clientWidth, clientHeight } = parent;

    // 실제 캔버스 내부 크기 설정 (픽셀 크기)
    canvas.width = clientWidth;
    canvas.height = clientHeight;

    // 보이는 크기는 CSS로 100% 고정
    canvas.style.width = "100%";
    canvas.style.height = "100%";

    ctx.lineWidth = 5;
    ctx.lineCap = "round";
    ctx.strokeStyle = color;
  }, []);

  /**
   * 부모 컴포넌트에서 온디맨드 리렌더링 처리
   */
  useEffect(() => {
    if (!getCanvasContext()) {
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
    const ctx = getCanvasContext();
    if (ctx) {
      ctx.strokeStyle = color;
    }
  }, [color]);

  return (
    <div className="relative w-full h-auto" style={{ aspectRatio: "4 / 3" }}>
      <canvas
        className={className}
        id={elementId}
        ref={canvasRef}
        width={800}
        height={600}
        onMouseMove={(e) => onMouseMove(e)}
        onMouseDown={startPainting}
        onMouseUp={stopPainting}
        onMouseLeave={stopPainting}
      />
    </div>
  );
}
