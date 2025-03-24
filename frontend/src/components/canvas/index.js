import "index.css";
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
        const rect = canvasRef.current.getBoundingClientRect();
        const offsetX = event.clientX - rect.left;
        const offsetY = event.clientY - rect.top;

        if (painting) {
            canvasContext.lineTo(offsetX, offsetY);
            canvasContext.stroke();
            setCurrentStroke((prevItems) => [...prevItems, {x: offsetX, y: offsetY}]);
        } else {
            canvasContext.beginPath();
            canvasContext.moveTo(offsetX, offsetY);
            if (currentStroke.length > 0) {
                setStrokes((prevItems) => [...prevItems, currentStroke]);
                setCurrentStroke([]);
            }
        }
    }

    const clear = () => {
        canvasContext.clearRect(0, 0, canvasRef.current.width, canvasRef.current.height);
    }

    const recover = () => {
        if (strokes.length === 0) {
            alert("이전에 그려진 데이터가 없습니다.");
            return;
        }

        const rect = canvasRef.current.getBoundingClientRect();
        console.log(rect);

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

        <div>
            <br/>
            <br/>
            <button onClick={e => console.log(strokes)}>현재까지의 데이터 로그 찍기</button>
            <button onClick={clear}>전체 지우기</button>
            <button onClick={recover}>저장된 데이터로 그림 다시 그리기</button>
            <br/>
            <br/>
            <br/>
        </div>
    </>;
}