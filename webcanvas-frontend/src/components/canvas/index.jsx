import { useEffect, useRef, useState } from "react";

export default function Canvas({
  strokes = [],
  onStroke = (stroke) => {},
  color = "black",
  reRenderingSignal = false,
  afterReRendering = () => {},
  className = String,
}) {
  const elementId = "canvas";
  /**
   * canvas ref
   * @type {React.RefObject<null>}
   */
  const canvasRef = useRef(null);

  /**
   * 현재 마우스를 누르고 그리고 있는 중인지 여부
   */
  const [painting, setPainting] = useState(false);
  /**
   * Stroke 이벤트마다 집계되는 stroke 획 state
   */
  const [currentStroke, setCurrentStroke] = useState([]);
  /**
   * 화면에 그려질 scaled strokes
   */
  const [scaledStrokes, setScaledStrokes] = useState(strokes);

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

    if (painting) {
      ctx.lineTo(offsetX, offsetY);
      ctx.stroke();
      setCurrentStroke((prevItems) => [
        ...prevItems,
        { x: Math.round(offsetX), y: Math.round(offsetY) },
      ]);
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
   * stroke scale 처리
   * @param originalStrokes
   * @param oldWidth
   * @param oldHeight
   * @param newWidth
   * @param newHeight
   * @returns {*}
   */
  const scaleStrokes = (originalStrokes, oldWidth, oldHeight, newWidth, newHeight) => {
    return originalStrokes.map((stroke) =>
      stroke.map((point) => {
        console.log(`width : ${oldWidth} => ${newWidth}`);
        console.log(`x : ${point.x} => ${point.x * (newWidth / oldWidth)}`);
        return {
          x: point.x * (newWidth / oldWidth),
          y: point.y * (newHeight / oldWidth),
        };
      })
    );
  };

  /**
   * canvas context 리턴
   */
  const getCanvasContext = () => {
    const canvas = canvasRef.current;
    return canvas.getContext("2d");
  };

  /**
   * 캔버스 rerendering
   */
  const reRendering = () => {
    const ctx = getCanvasContext();
    if (!ctx) {
      return;
    }
    ctx.clearRect(0, 0, canvasRef.current.width, canvasRef.current.height);

    scaledStrokes.forEach((stroke) => {
      if (stroke.length === 0) {
        return;
      }
      stroke.forEach((point, index) => {
        if (index > 0) {
          ctx.lineTo(point.x, point.y);
          ctx.stroke();
        }

        ctx.beginPath();
        ctx.moveTo(point.x, point.y);
      });
    });
  };

  /**
   * canvas 반응형 resize
   */
  const resize = () => {
    const canvas = canvasRef.current;
    const parent = canvas.parentElement;

    const ctx = getCanvasContext();

    const { clientWidth, clientHeight } = parent;

    const oldWidth = canvas.width;
    const oldHeight = canvas.height;

    // 실제 캔버스 내부 크기 설정 (픽셀 크기)
    canvas.width = clientWidth;
    canvas.height = clientHeight;

    // 보이는 크기는 CSS로 100% 고정
    canvas.style.width = "100%";
    canvas.style.height = "100%";

    ctx.lineWidth = 5;
    ctx.lineCap = "round";
    ctx.strokeStyle = color;

    setScaledStrokes(scaleStrokes(strokes, oldWidth, oldHeight, canvas.width, canvas.height));
    reRendering();
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

  useEffect(() => {
    resize();

    /**
     * 윈도우 리사이즈시 캔버스도 함께 리사이즈
     */
    window.addEventListener("resize", resize);

    return () => {
      window.removeEventListener("resize", resize);
    };
  }, []);
  useEffect(() => {
    if (!getCanvasContext()) {
      return;
    }

    if (reRenderingSignal) {
      reRendering();
      afterReRendering();
    }
  }, [reRenderingSignal, afterReRendering]);
  useEffect(() => {
    const ctx = getCanvasContext();
    if (ctx) {
      ctx.strokeStyle = color;
    }
  }, [color]);
  useEffect(() => {
    console.log("rerender");
    reRendering();
  }, [scaledStrokes]);

  return (
    <div className="relative w-full h-auto" style={{ aspectRatio: "4 / 3" }}>
      <canvas
        className={className}
        id={elementId}
        ref={canvasRef}
        onMouseMove={(e) => onMouseMove(e)}
        onMouseDown={startPainting}
        onMouseUp={stopPainting}
        onMouseLeave={stopPainting}
      />
    </div>
  );
}
