import Canvas from "components/canvas";
import {useState} from "react";

export default function CanvasTest() {
    const [strokes, setStrokes] = useState([]);
    const [reRenderingSignal, setReRenderingSignal] = useState(false);
    const [userCreating, setUserCreating] = useState(false);
    const [gameRoomCreating, setGameRoomCreating] = useState(false);
    const ongoingMessage = "이미 요청을 처리중입니다.";


    const onStrokeHandler = (stroke) => {
        if (stroke.length > 0) {
            setStrokes((prevItems) => [...prevItems, stroke]);
        }
    };

    const clear = () => {
        setStrokes([]);
        setReRenderingSignal(true);
    };


    const onCreateUserButtonClickHandler = async (e) => {
        if (userCreating) {
            alert(ongoingMessage);
            return;
        }

        const created = await createUser();
        if (created) {
            alert(`${localStorage.getItem("userId")} user created!`);
        }
    }
    const createUser = async () => {
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


    const onCreateGameRoomButtonClickHandler = async (e) => {
        if (gameRoomCreating) {
            alert(ongoingMessage);
            return;
        }

        const created = await createGameRoom();

    }

    const createGameRoom = async () => {
        try {
            const response = await fetch(`${process.env.REACT_APP_API_BASE_URL}/game/canvas/room`, {
                method: "POST",
                headers: {
                    "Content-Type": "application/json"
                },
            });
        }
        catch(error) {
            alert("API 요청 실패 " + error);
            console.log(error);
            return false;
        }
        finally {
            setGameRoomCreating(false);
        }
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
            <button onClick={onCreateGameRoomButtonClickHandler}>테스트 방 만들기</button>
            <button onClick={e => console.log(strokes)}>현재까지의 데이터 로그 찍기</button>
            <button onClick={clear}>전체 지우기</button>
            <br/>
            <br/>
            <br/>
        </div>
    </div>;
}