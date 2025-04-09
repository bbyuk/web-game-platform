import Canvas from "@components/canvas/index.jsx";
import {useEffect, useState} from "react";

export default function CanvasTest() {
    const [strokes, setStrokes] = useState([]);
    const [reRenderingSignal, setReRenderingSignal] = useState(false);
    const [loginProcessing, setLoginProcessing] = useState(false);
    const [gameRoomCreating, setGameRoomCreating] = useState(false);
    const [loggedIn, setLoggedIn] = useState(false);
    const apiUrl = import.meta.env.VITE_WEB_CANVAS_SERVICE;

    const [gameRooms, setGameRooms] = useState([
        { id: 1, name: "Canvas Room A"},
        { id: 2, name: "초록 방"}
    ]);
    const onEnterRoom = (roomId) => {
        alert(`${roomId}번 방으로 입장 시도`);
    }

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


    const onLoginButtonClickHandler = async (e) => {
        if (loginProcessing) {
            alert(ongoingMessage);
            return;
        }
        const loggedIn = await login();
    }

    const login = async () => {
        try {


            /**
             * 로그인 로직
             */
            const response = await fetch(`${apiUrl}/auth/login`, {
                method: "POST",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify({
                    fingerprint: "testuser1234"
                })
            });

            if (!response.ok) {
                throw new Error(`error status : ${response.status}`);
            }

            const {accessToken, refreshToken} = await response.json();
            localStorage.setItem("accessToken", accessToken);
            localStorage.setItem("refreshToken", refreshToken);

            setLoggedIn(true);
            alert("로그인 성공!");
        }
        catch(error) {
            alert(`API 요청 실패 ${error}`);
            return false;
        }
        finally {
            setLoginProcessing(false);
        }
    }


    const onCreateGameRoomButtonClickHandler = async (e) => {
        if (gameRoomCreating) {
            alert(ongoingMessage);
            return;
        }

        const created = await createGameRoom();

    }
    const createGameRoom = async () => {
        try {
            const response = await fetch(`${apiUrl}/game/canvas/room`, {
                method: "POST",
                headers: {
                    "Content-Type": "application/json"
                },
            });
        } catch (error) {
            alert("API 요청 실패 " + error);
            console.log(error);
            return false;
        } finally {
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
            <h2>게임 방 목록</h2>
            <ul>
                {gameRooms.map((room) => (
                    <li key={room.id} style={{ marginBottom: "10px" }}>
                        <span>방 이름: {room.name}</span>
                        <button onClick={() => onEnterRoom(room.id)}>입장</button>
                    </li>
                ))}
            </ul>
        </div>

        <div>
            <br/>
            <br/>
            <button onClick={onLoginButtonClickHandler}>로그인</button>
            <button onClick={onCreateGameRoomButtonClickHandler}>테스트 방 만들기</button>
            <button onClick={e => console.log(strokes)}>현재까지의 데이터 로그 찍기</button>
            <button onClick={clear}>전체 지우기</button>
            <br/>
            <br/>
            <br/>
        </div>
    </div>;
}