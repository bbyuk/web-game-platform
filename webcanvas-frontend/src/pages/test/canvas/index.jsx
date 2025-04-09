import Canvas from "@components/canvas";
import {useEffect, useState} from "react";
import GameRoomList from "@components/game-room-list";

export default function CanvasTest() {
    const [strokes, setStrokes] = useState([]);
    const [reRenderingSignal, setReRenderingSignal] = useState(false);
    const [loginProcessing, setLoginProcessing] = useState(false);
    const [gameRoomCreating, setGameRoomCreating] = useState(false);
    const [loggedIn, setLoggedIn] = useState(false);
    const apiUrl = import.meta.env.VITE_WEB_CANVAS_SERVICE;
    const ongoingMessage = "이미 요청을 처리중입니다.";


    /**
     * ======================== 화면 컴포넌트 메소드 ========================
     */

    /**
     * 캔버스를 clear하고 그려진 stroke들을 제거한 후, 캔버스 컴포넌트에 rerendering signal을 준다.
     */
    const clear = () => {
        setStrokes([]);
        setReRenderingSignal(true);
    };

    /**
     * ========================    이벤트 핸들러    ========================
     */

    /**
     * 로그인 버튼 클릭 이벤트 핸들러
     * @param e
     * @returns {Promise<void>}
     */
    const onLoginButtonClickHandler = async (e) => {
        if (loginProcessing) {
            alert(ongoingMessage);
            return;
        }
        const loggedIn = await login();
    }
    /**
     * 게임 방 생성 버튼 클릭 이벤트 핸들러
     * @param e
     * @returns {Promise<void>}
     */
    const onCreateGameRoomButtonClickHandler = async (e) => {
        if (gameRoomCreating) {
            alert(ongoingMessage);
            return;
        }

        const created = await createGameRoom();

    }

    /**
     * 캔버스 컴포넌트 stroke 이벤트 핸들러
     * @param stroke
     */
    const onStrokeHandler = (stroke) => {
        if (stroke.length > 0) {
            setStrokes((prevItems) => [...prevItems, stroke]);
        }
    };

    /**
     * ========================    서버 API 요청   ========================
     */

    /**
     * 로그인
     * @returns {Promise<boolean>}
     */
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

    /**
     * 게임 방 생성
     * @returns {Promise<boolean>}
     */
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

    /**
     *
     */

    /**
     * useEffect hook
     */
    useEffect(() => {
        const accessToken = localStorage.getItem("accessToken");

        if (accessToken) {
            setLoggedIn(true);
        }
    }, []);

    return <div>
        <Canvas
            strokes={strokes}
            onStroke={onStrokeHandler}
            reRenderingSignal={reRenderingSignal}
            afterReRendering={() => setReRenderingSignal(false)}
            color={"green"}
        />

        { loggedIn ? <GameRoomList /> : null }

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