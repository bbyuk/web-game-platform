import Canvas from "components/canvas";
import {useState} from "react";

export default function CanvasTest() {
    const [strokes, setStrokes] = useState([]);
    const [reRenderingSignal, setReRenderingSignal] = useState(false);

    const onStrokeHandler = (stroke) => {
        if (stroke.length > 0) {
            setStrokes((prevItems) => [...prevItems, stroke]);
        }
    };

    const clear = () => {
        setStrokes([]);
        setReRenderingSignal(true);
    };

    const createGameRoom = () => {
        fetch(`${apiBaseUrl}/users`)
            .then(response => response.json())
            .then(data => console.log(data));
    };

    return <div >
        <Canvas
            strokes={strokes}
            onStroke={onStrokeHandler}
            reRenderingSignal={reRenderingSignal}
            afterReRendering={() => setReRenderingSignal(false)}
            color={"green"}
        />
        <div>
            <br/>
            <br/>
            <button onClick={e => alert("테스트 유저 만들기")}>테스트 유저 만들기</button>
            <button onClick={e => alert("테스트 방 만들기")}>테스트 방 만들기</button>
            <button onClick={e => console.log(strokes)}>현재까지의 데이터 로그 찍기</button>
            <button onClick={clear}>전체 지우기</button>
            <br/>
            <br/>
            <br/>
        </div>
    </div>;
}