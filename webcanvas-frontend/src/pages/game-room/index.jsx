import { Outlet, useLocation, useNavigate, useParams } from 'react-router-dom';
import React, { useEffect, useRef, useState } from 'react';
import { game } from '@/api/index.js';
import { getApiClient } from '@/client/http/index.jsx';
import { pages } from '@/router/index.jsx';
import { EMPTY_MESSAGES, REDIRECT_MESSAGES } from '@/constants/message.js';
import { ArrowLeft, Gamepad2 } from 'lucide-react';
import { getWebSocketClient } from '@/client/stomp/index.jsx';
import { useAuthentication } from '@/contexts/authentication/index.jsx';
import { useApiLock } from '@/api/lock/index.jsx';
import { useLeftSideStore } from '@/stores/layout/leftSideStore.jsx';
import ItemList from '@/components/layouts/side-panel/contents/item-list/index.jsx';
import { useRightSideStore } from '@/stores/layout/rightSideStore.jsx';
import { useClientStore } from '@/stores/client/clientStore.jsx';

export default function GameRoomPage() {
  // ===============================================================
  // 상태 정의
  // ===============================================================
  const { roomId } = useParams();
  const { authenticatedUserId } = useAuthentication();
  const { apiLock } = useApiLock();
  const apiClient = getApiClient();
  const navigate = useNavigate();
  const webSocketClientRef = useRef(null);
  const leftSideStore = useLeftSideStore();
  const rightSideStore = useRightSideStore();
  const { startLoading } = useClientStore();
  const { state } = useLocation();

  /**
   * 페이지 상태ㅁ
   */
  const [connected, setConnected] = useState(false);
  const [enteredUsers, setEnteredUsers] = useState([]);
  const [myInfo, setMyInfo] = useState({
    gameRoomParticipantId: null,
    nickname: null,
    color: null,
    role: null,
    ready: false,
  });
  const [gameRoomState, setGameRoomState] = useState(null);

  // ===============================================================
  // 유저 정의 함수
  // ===============================================================

  /**
   * 레디 상태를 변경한다.
   * @param ready
   */
  const changeReadyState = (ready) => {
    setMyInfo({ ...myInfo, ready });
  };

  /**
   * 현재 입장한 게임 방의 정보를 조회한다.
   * @returns {Promise<awaited Promise<Result<RootNode> | void> | Promise<Result<Root> | void> | Promise<any>>}
   */
  const findCurrentGameRoomInfo = async () => {
    // 방 정보 조회
    return await apiClient
      .get(game.getCurrentJoinedGameRoom)
      .then((response) => {
        // 상태 셋팅
        setEnteredUsers(response.enteredUsers);

        setMyInfo({
          nickname: response.requesterUserSummary.nickname,
          color: response.requesterUserSummary.color,
          role: response.requesterUserSummary.role,
          gameRoomParticipantId: response.gameRoomParticipantId,
          ready: response.requesterUserSummary.ready,
        });

        if (roomId !== "temp" && !connected) {
          setConnected(true);
        }

        setGameRoomState(response.gameRoomState);
        if (response.gameRoomState === "WAITING") {
          navigate(pages.gameRoom.waiting.url(response.gameRoomId), { replace: true });
          return null;
        } else if (response.gameRoomState === "PLAYING") {
          navigate(pages.gameRoom.playing.url(response.gameRoomId), { replace: true });
          return null;
        }

        setLeftSidebar(response);
        return response;
      })
      .catch((error) => {
        if (error.code === "R003" || error.code === "R000") {
          // 로비로 이동
          alert(REDIRECT_MESSAGES.TO_LOBBY);
          navigate(pages.lobby.url, { replace: true });
          return null;
        }
      });
  };

  /**
   * left sidebar set
   * @param response
   */
  const setLeftSidebar = (response) => {
    if (response.enteredUsers) {
      leftSideStore.setContents({
        slot: ItemList,
        props: {
          value: response.enteredUsers.map(({ nickname, ...rest }) => ({
            label: nickname,
            ...rest,
          })),
        },
      });
    } else {
      leftSideStore.setContents({
        slot: ItemList,
        props: {
          value: [],
          emptyPlaceholder: EMPTY_MESSAGES.ENTERED_USER_LIST,
        },
      });
    }
  };

  /**
   * 현재 게임 방에서 퇴장한다.
   * @returns {Promise<void>}
   */
  const exitGameRoom = async (gameRoomParticipantId) => {
    if (!confirm("방에서 나가시겠습니까?")) {
      return;
    }

    const response = await apiLock(
      game.exitFromGameRoom(gameRoomParticipantId),
      async () => await apiClient.delete(game.exitFromGameRoom(gameRoomParticipantId))
    );

    if (response.success) {
      navigate(pages.lobby.url, { replace: true });
    }
  };

  /**
   * 웹소켓 서버에 연결하고 현재 방에 해당하는 브로커를 구독한다.
   * @param gameRoomId
   */
  const connectToWebSocket = () => {
    if (webSocketClientRef.current) {
      // 이전 client 존재시 deactivate
      webSocketClientRef.current.deactivate();
    }
    const options = {
      onConnect: (frame) => {},
      onError: (frame) => {},
    };
    webSocketClientRef.current = getWebSocketClient(options);
  };

  /**
   * 웹소켓을 통해 구독할 토픽과 콜백을 정의하고 구독한다.
   */
  const subscribeTopics = () => {
    /**
     * 게임 방 메세지 브로커 핸들러
     * @param frame
     */
    const gameRoomEventHandler = (frame) => {
      if (!frame.event) {
        // 서버로부터 받은 이벤트가 없음
        return;
      }

      switch (frame.event) {
        case "ROOM/JOIN":
        case "ROOM/EXIT":
          if (authenticatedUserId !== frame.userId) {
            /**
             * 다른 사람 입장 OR 퇴장 이벤트 발생시
             */
            findCurrentGameRoomInfo().then((response) => {
              if (!response) {
                return;
              }

              setLeftSidebar(response);
            });
          }
          break;
        case "ROOM/USER_READY_CHANGED":
          /**
           * 다른 사람 ready 발생시
           */
          findCurrentGameRoomInfo().then((response) => {
            if (!response) {
              return;
            }

            setLeftSidebar(response);
          });
          break;
        case "ROOM/SESSION_STARTED":
          startLoading();
          navigate(pages.gameRoom.playing.url(roomId), { replace: true });
          break;
      }
    };


    const topics = [
      // 게임 방 공통 이벤트 broker
      {
        destination: `/room/${roomId}`,
        messageHandler: gameRoomEventHandler,
      }
    ];

    console.log("game-room/index.jsx = 구독");

    webSocketClientRef.current.subscribe("room", topics);
  };

  // ===============================================================
  // useEffect 훅
  // ===============================================================

  useEffect(() => {
    const title =
      gameRoomState === "WAITING"
        ? {
            label: "exit",
            icon: <ArrowLeft size={20} className="text-gray-400" />,
            button: true,
            onClick: () => {
              exitGameRoom(myInfo.gameRoomParticipantId);
            },
          }
        : {
            label: "playing",
            icon: <Gamepad2 className="w-6 h-6 text-green-500" />,
            button: false,
          };

    console.log(title);

    leftSideStore.setTitle(title);
  }, [gameRoomState]);

  /**
   * 페이지 컴포넌트 unmount 시 현 페이지에서 설정한 전역설정 clear
   *
   * 웹소켓 연결 해제
   */
  useEffect(() => {
    return () => {
      if (webSocketClientRef.current) {
        webSocketClientRef.current.unsubscribe("room");
        webSocketClientRef.current.deactivate();
      }
      leftSideStore.clear();
      rightSideStore.clear();
    };
  }, []);

  /**
   * roomId 서버 조회 및 validation
   */
  useEffect(() => {
    findCurrentGameRoomInfo().catch((error) => alert(error));
  }, [roomId]);

  /**
   * 게임 종료 후 대기방으로 리턴시 게임 방 입장 정보 재조회
   */
  useEffect(() => {
    if (state?.gameSessionEnd) {
      findCurrentGameRoomInfo().catch((error) => alert(error));
    }
  }, [state]);

  /**
   * leftbar 리스트 등록
   */
  useEffect(() => {
    const getLeftSideItemTheme = (role, ready) => {
      // if (role === "GUEST" && ready) {
      //   return "indigo";
      // }
      // else if (role === "HOST") {
      //   return "green";
      // }

      if (ready) {
        return "indigo";
      }
    };

    if (enteredUsers.length > 0) {
      leftSideStore.setContents({
        slot: ItemList,
        props: {
          value: enteredUsers.map(({ nickname, ready, role, ...rest }) => ({
            label: nickname,
            highlight: ready,
            theme: getLeftSideItemTheme(role, ready),
            ...rest,
          })),
        },
      });
    }
  }, [enteredUsers]);

  /**
   * 웹소켓 토픽 구독 및 메세지 핸들러 등록 useEffect
   *
   * 토픽 구독
   * 채팅 입력 핸들러 셋팅
   */
  useEffect(() => {
    if (!webSocketClientRef.current || !authenticatedUserId) {
      return;
    }

    subscribeTopics();
  }, [authenticatedUserId, webSocketClientRef.current]);

  useEffect(() => {
    if (connected) {
      connectToWebSocket();
    }
  }, [connected]);

  return (
    <Outlet
      context={{
        enteredUsers,
        myInfo,
        changeReadyState,
        webSocketClientRef,
      }}
    />
  );
}
