import "index.css";
import {createCanvas} from "canvas";
import {useEffect, useRef, useState} from "react";

export default function Canvas() {
    const canvasRef = useRef(null);
    const elementId = "canvas";
    const [painting, setPainting] = useState(false);
    const [canvasContext, setCanvasContext] = useState(null);
    const [currentStroke, setCurrentStroke] = useState([]);
    const [strokes, setStrokes] = useState([]);


    const startPainting = () => {
        setPainting(true);
    }
    const stopPainting = () => {
        setPainting(false);
    }
    const onMouseMove = (event) => {
        const rect = event.currentTarget.getBoundingClientRect();
        const offsetX = event.clientX - rect.left;
        const offsetY = event.clientY - rect.top;

        if (painting) {
            canvasContext.lineTo(offsetX, offsetY);
            canvasContext.stroke();
            setCurrentStroke((prevItems) => [...prevItems, {x: offsetX, y: offsetY}] );
        }
        else {
            canvasContext.beginPath();
            canvasContext.moveTo(offsetX, offsetY);
            setStrokes((prevItems) => [...prevItems, currentStroke]);
        }
    }


    useEffect(() => {
        const canvas = canvasRef.current;
        const ctx = canvas.getContext("2d");

        ctx.lineWidth = 10;
        ctx.lineCap = "round";
        ctx.strokeStyle = "black";

        setCanvasContext(ctx);
    }, []);

    return <>
        <canvas
            id={elementId}
            ref={canvasRef}
            width={1000}
            height={700}
            onMouseMove={e => onMouseMove(e)}
            onMouseDown={startPainting}
            onMouseUp={stopPainting}
            onMouseLeave={stopPainting}
        />
    </>;
}