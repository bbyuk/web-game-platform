import "index.css";
import {useEffect, useRef, useState} from "react";

export default function Canvas({
                                   strokes = [],
                                   onStroke = (stroke) => {
                                   },
                                   color = "black",
                                   width = 1000,
                                   height = 700,
                                   reRenderingSignal = false,
                                   afterReRendering = () => {
                                   }
                               }) {
    const elementId = "canvas";
    const canvasRef = useRef(null);
    const [canvasContext, setCanvasContext] = useState(null);
    const [painting, setPainting] = useState(false);
    const [currentStroke, setCurrentStroke] = useState([]);


    const startPainting = () => {
        setPainting(true);
    }
    const stopPainting = () => {
        setPainting(false);
    }
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
            setCurrentStroke((prevItems) => [...prevItems, {x: Math.round(offsetX), y: Math.round(offsetY)}]);
        } else {
            canvasContext.beginPath();
            canvasContext.moveTo(offsetX, offsetY);

            onStroke(currentStroke);
            setCurrentStroke([]);
        }
    }

    useEffect(() => {
        const canvas = canvasRef.current;
        const ctx = canvas.getContext("2d");

        ctx.lineWidth = 5;
        ctx.lineCap = "round";

        setCanvasContext(ctx);
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

            strokes.forEach(stroke => {
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
        }

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

    return <>
        <canvas
            id={elementId}
            ref={canvasRef}
            style={{border: "solid 1px black"}}
            width={width}
            height={height}
            onMouseMove={e => onMouseMove(e)}
            onMouseDown={startPainting}
            onMouseUp={stopPainting}
            onMouseLeave={stopPainting}
        />
    </>;
}