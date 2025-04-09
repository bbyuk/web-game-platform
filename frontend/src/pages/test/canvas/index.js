import Canvas from "components/canvas";
import {useState} from "react";

export default function CanvasTest() {
    const [strokes, setStrokes] = useState([]);
    const [reRenderingSignal, setReRenderingSignal] = useState(false);
    const [userCreating, setUserCreating] = useState(false);

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

    };

    const onCreateUserButtonClickHandler = async (e) => {
        const created = await createUser();

        if (created) {
            alert(`${localStorage.getItem("userId")} user created!`);
        }
    }
    const createUser = async () => {
        if (userCreating) {
            alert("이미 요청을 처리중입니다");
            return false;
        }
        try {
            setUserCreating(true);
            const response = await fetch(`${process.env.REACT_APP_API_BASE_URL}/user`, {
                method: "POST",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify({
                    clientFingerprint: "testuser1234"
                })
            });

            if (!response.ok) {
                throw new Error(response.status);
            }

            const {userId, fingerprint} = await response.json();

            localStorage.setItem("fingerprint", fingerprint);
            localStorage.setItem("userId", userId);
        } catch (error) {
            alert("API 요청 실패 " + error);
            console.log(error);
            return false;
        }
        finally {
            setUserCreating(false);
        }

        return true;
    };

    return <div>
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
            <button onClick={onCreateUserButtonClickHandler}>테스트 유저 만들기</button>
            <button onClick={e => alert("테스트 방 만들기")}>테스트 방 만들기</button>
            <button onClick={e => console.log(strokes)}>현재까지의 데이터 로그 찍기</button>
            <button onClick={clear}>전체 지우기</button>
            <br/>
            <br/>
            <br/>
        </div>
    </div>;
}