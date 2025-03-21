import "index.css";
import {createCanvas} from "canvas";
import {useEffect, useRef} from "react";

export default function Canvas() {
    const canvasRef = useRef(null);
    const elementId = "canvas";

    useEffect(() => {
        const canvas = canvasRef.current;
        const ctx = canvas.getContext("2d");

        // 배경색 설정
        ctx.fillStyle = '#f0f0f0';
        ctx.fillRect(0, 0, canvas.width, canvas.height);

        // 원 그리기
        ctx.fillStyle = 'green';
        ctx.beginPath();
        ctx.arc(100, 100, 50, 0, Math.PI * 2);
        ctx.fill();

        // 텍스트 그리기
        ctx.fillStyle = 'black';
        ctx.font = '20px Arial';
        ctx.fillText('Hello, Canvas!', 50, 200);
    }, []);


    return <>
        <canvas
            id={elementId}
            ref={canvasRef}
            width={400}
            height={400}
            style={{border: '1px solid black'}}
        />
    </>;
}