import { useEffect, useRef, useState } from "react";

export default function Canvas({
  strokes = [],
  onStroke = (stroke) => {},
  color = "black",
  width = 1000,
  height = 700,
  reRenderingSignal = false,
  afterReRendering = () => {},
}) {
  const elementId = "canvas";
  const canvasRef = useRef(null);
  const [canvasContext, setCanvasContext] = useState(null);
  const [painting, setPainting] = useState(false);
  const [currentStroke, setCurrentStroke] = useState([]);

  const startPainting = () => {
    setPainting(true);
  };
  const stopPainting = () => {
    setPainting(false);
  };
  const onMouseMove = (event) => {
    if (!canvasContext) {
      return;
    }

    const rect = canvasRef.current.getBoundingClientRect();
    const offsetX = event.clientX - rect.left;
    const offsetY = event.clientY - rect.top;

    if (painting) {
      canvasContext.lineTo(offsetX, offsetY);
      canvasContext.stroke();
      setCurrentStroke((prevItems) => [
        ...prevItems,
        { x: Math.round(offsetX), y: Math.round(offsetY) },
      ]);
    } else {
      canvasContext.beginPath();
      canvasContext.moveTo(offsetX, offsetY);

      onStroke(currentStroke);
      setCurrentStroke([]);
    }
  };

  useEffect(() => {
    const canvas = canvasRef.current;
    const ctx = canvas.getContext("2d");

    // 부모 크기 측정 및 캔버스 resize
    const resizeCanvas = () => {
      const parent = canvas.parentElement;
      const { clientWidth, clientHeight } = parent;

      // 실제 캔버스 내부 크기 설정 (픽셀 크기)
      canvas.width = clientWidth;
      canvas.height = clientHeight;

      // 보이는 크기는 CSS로 100% 고정
      canvas.style.width = "100%";
      canvas.style.height = "100%";

      // if (canvasContext) {
      //   canvasContext.lineWidth = 5;
      //   canvasContext.lineCap = "round";
      // }
      // else {
      //   ctx.lineWidth = 5;
      //   ctx.lineCap = "round";
      // }
    };

    resizeCanvas();
    ctx.lineWidth = 5;
    ctx.lineCap = "round";

    /**
     * 윈도우 리사이즈시 캔ㅂ스도 함께 리사이즈
     */
    window.addEventListener("resize", resizeCanvas);

    setCanvasContext(ctx);

    return () => {
      window.removeEventListener("resize", resizeCanvas);
    };
  }, []);
  useEffect(() => {
    if (!canvasContext) {
      return;
    }

    const reRendering = () => {
      if (!canvasContext) {
        return;
      }
      canvasContext.clearRect(0, 0, canvasRef.current.width, canvasRef.current.height);

      strokes.forEach((stroke) => {
        if (stroke.length === 0) {
          return;
        }
        stroke.forEach((point, index) => {
          if (index > 0) {
            canvasContext.lineTo(point.x, point.y);
            canvasContext.stroke();
          }

          canvasContext.beginPath();
          canvasContext.moveTo(point.x, point.y);
        });
      });
    };

    if (reRenderingSignal) {
      reRendering();
      afterReRendering();
    }
  }, [canvasContext, reRenderingSignal, afterReRendering]);
  useEffect(() => {
    if (canvasContext) {
      canvasContext.strokeStyle = color;
    }
  }, [canvasContext, color]);

  return (
    <>
      <canvas
        id={elementId}
        ref={canvasRef}
        style={{ border: "solid 1px black" }}
        onMouseMove={(e) => onMouseMove(e)}
        onMouseDown={startPainting}
        onMouseUp={stopPainting}
        onMouseLeave={stopPainting}
      />
    </>
  );
}
