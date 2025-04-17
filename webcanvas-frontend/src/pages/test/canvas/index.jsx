import Canvas from "@/components/canvas";
import { useEffect, useState } from "react";
import GameRoomList from "@/components/game-room-list";
import { useApiLock } from "@/api/lock";
import { useApplicationContext } from "@/contexts/application/index.jsx";
import { auth, game } from "@/api/index.js";

export default function CanvasTest() {
  const { api } = useApplicationContext();

  /**
   * API 중복 호출을 막는 락 커스텀 hook
   */
  const { apiLock } = useApiLock();
  const [reRenderingSignal, setReRenderingSignal] = useState(false);

  const [strokes, setStrokes] = useState([]);
  const [loggedIn, setLoggedIn] = useState(false);

  /**
   * 게임방 생성 및 입장 API 응답값
   */
  const [enteredGameRoomId, setEnteredGameRoomId] = useState(null);
  const [gameRoomEntranceId, setGameRoomEntranceId] = useState(null);

  /**
   * 입장가능한 게임 방 목록 조회 API 응답값
   */
  const [enterableGameRoomList, setEnterableGameRoomList] = useState([]);

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
    const loggedIn = await apiLock("login", login);
  };
  /**
   * 게임 방 생성 버튼 클릭 이벤트 핸들러
   * @param e
   * @returns {Promise<void>}
   */
  const onCreateGameRoomButtonClickHandler = async (e) => {
    const created = await apiLock("create-game-room", createGameRoom);
  };

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
   * 게임 방 입장 버튼 클릭 이벤트 핸들러
   * @param gameRoomId
   */
  const enterButtonClickHandler = async (gameRoomId) => {
    await apiLock("enter-game-room", () => enterGameRoom(gameRoomId));
  };

  /**
   * ========================    서버 API 요청   ========================
   */

  /**
   * 로그인
   * @returns {Promise<boolean>}
   */
  const login = async () => {
    const savedFingerprint = localStorage.getItem("fingerprint");
    const requestData = {
      fingerprint: savedFingerprint ? savedFingerprint : "",
    };

    const { fingerprint, accessToken, refreshToken } = await api.post(auth.login, requestData, {
      credentials: "include",
    });

    localStorage.setItem("accessToken", accessToken);
    localStorage.setItem("refreshToken", refreshToken);
    localStorage.setItem("fingerprint", fingerprint);

    setLoggedIn(true);
    alert("로그인 성공!");
  };

  /**
   * 게임 방 입장
   * @param targetGameRoomId
   * @returns {Promise<void>}
   */
  const enterGameRoom = async (targetGameRoomId) => {
    const { gameRoomId, gameRoomEntranceId } = await api.post(game.enterGameRoom(targetGameRoomId));

    alert("게임 방 입장 성공!");
    // 게임 방 캔버스 웹 소켓 구독처리
  };

  /**
   * 게임 방 생성
   * @returns {Promise<boolean>}
   */
  const createGameRoom = async () => {
    const { gameRoomId, gameRoomEntranceId } = await api.post(game.createGameRoom);

    setGameRoomEntranceId(gameRoomEntranceId);
    setEnteredGameRoomId(gameRoomId);

    alert(`게임 방 생성 및 입장 성공! 게임 방 ID : ${gameRoomId}`);
  };

  /**
   * 입장 가능한 게임 방 목록 조회
   * @returns {Promise<void>}
   */
  const getEnterableGameRooms = async () => {
    const { roomList } = await api.get(game.getEnterableRooms);

    setEnterableGameRoomList(roomList);
  };

  /**
   * 로그인 되었을 시 처리
   */
  useEffect(() => {
    apiLock("get-enterable-game-room", getEnterableGameRooms).then((response) => {
      console.log(response);
    });
  }, []);

  return (
    <div>
      <Canvas
        strokes={strokes}
        onStroke={onStrokeHandler}
        reRenderingSignal={reRenderingSignal}
        afterReRendering={() => setReRenderingSignal(false)}
        color={"green"}
      />

      <div>
        <br />
        <br />
        <button onClick={onLoginButtonClickHandler}>로그인</button>
        <button onClick={onCreateGameRoomButtonClickHandler}>테스트 방 만들기</button>
        <button onClick={(e) => console.log(strokes)}>현재까지의 데이터 로그 찍기</button>
        <button onClick={clear}>전체 지우기</button>
        <br />
        <br />
        <br />
      </div>

      {loggedIn ? (
        <GameRoomList rooms={enterableGameRoomList} onEnterButtonClick={enterButtonClickHandler} />
      ) : null}
    </div>
  );
}
