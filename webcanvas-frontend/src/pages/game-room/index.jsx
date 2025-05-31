import { Outlet, useNavigate, useParams } from "react-router-dom";
import React, { useEffect, useRef, useState } from "react";
import { game } from "@/api/index.js";
import { getApiClient } from "@/client/http/index.jsx";
import { pages } from "@/router/index.jsx";
import { EMPTY_MESSAGES, REDIRECT_MESSAGES } from "@/constants/message.js";
import { ArrowLeft } from "lucide-react";
import { getWebSocketClient } from "@/client/stomp/index.jsx";
import { useAuthentication } from "@/contexts/authentication/index.jsx";
import { useApiLock } from "@/api/lock/index.jsx";
import { useLeftSideStore } from "@/stores/layout/leftSideStore.jsx";
import ItemList from "@/components/layouts/side-panel/item-list/index.jsx";

export default function GameRoomPage() {
  const { roomId } = useParams();
  const { authenticatedUserId } = useAuthentication();
  const { apiLock } = useApiLock();
  const apiClient = getApiClient();
  const navigate = useNavigate();
  const webSocketClientRef = useRef(null);
  const leftSideStore = useLeftSideStore();

  /**
   * 페이지 상태
   */
  const [connected, setConnected] = useState(false);
  const [enteredUsers, setEnteredUsers] = useState([]);
  const [myInfo, setMyInfo] = useState({
    gameRoomEntranceId: null,
    nickname: null,
    color: null,
    role: null,
    ready: false,
  });

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
      .get(game.getCurrentEnteredGameRoom)
      .then((response) => {
        // 상태 셋팅
        setEnteredUsers(response.enteredUsers);

        console.log(response);
        setMyInfo({
          nickname: response.requesterUserSummary.nickname,
          color: response.requesterUserSummary.color,
          role: response.requesterUserSummary.role,
          gameRoomEntranceId: response.gameRoomEntranceId,
          ready: response.requesterUserSummary.ready,
        });

        if (roomId !== "temp" && !connected) {
          setConnected(true);
        }

        if (response.gameRoomState === "WAITING") {
          navigate(pages.gameRoom.waiting.url(response.gameRoomId), { replace: true });
          return null;
        } else if (response.gameRoomState === "PLAYING") {
          navigate(pages.gameRoom.playing.url(response.gameRoomId), { replace: true });
          return null;
        }

        return response;
      })
      .catch((error) => {
        if (error.code === "R003") {
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

    leftSideStore.setTitle({
      label: "exit",
      icon: <ArrowLeft size={20} className="text-gray-400" />,
      button: true,
      onClick: () => {
        exitGameRoom(response.gameRoomEntranceId);
      },
    });
  };

  /**
   * 현재 게임 방에서 퇴장한다.
   * @returns {Promise<void>}
   */
  const exitGameRoom = async (gameRoomEntranceId) => {
    if (!confirm("방에서 나가시겠습니까?")) {
      return;
    }

    const response = await apiLock(
      game.exitFromGameRoom(gameRoomEntranceId),
      async () => await apiClient.delete(game.exitFromGameRoom(gameRoomEntranceId))
    );

    if (response.success) {
      navigate(pages.lobby.url, { replace: true });
    }
  };

  /**
   * 다른 유저의 입장 및 퇴장 이벤트 발생시
   * 현재 게임방 정보를 재조회해 leftbar setting
   */
  const findEnteredUsers = () => {
    findCurrentGameRoomInfo().then((response) => {
      if (!response) {
        return;
      }

      setLeftSidebar(response);
    });
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
        case "ROOM/ENTRANCE":
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
        case "SESSION/STARTED":
          console.log(frame);
          findCurrentGameRoomInfo().then((response) => {
            if (!response) {
              return;
            }

            setLeftSidebar(response);
          });
          break;
      }
    };

    /**
     * 채팅 메세지 브로커 핸들러
     * @param frame
     */
    const gameRoomChatHandler = (frame) => {
      console.log(frame);
    };

    const topics = [
      // 게임 방 공통 이벤트 broker
      {
        destination: `/session/${roomId}`,
        messageHandler: gameRoomEventHandler,
      },
      // 게임 방 내 채팅 broker
      {
        destination: `/session/${roomId}/chat`,
        messageHandler: gameRoomChatHandler,
      },
    ];
    webSocketClientRef.current.subscribe(topics);
  };

  /**
   * 페이지 컴포넌트 unmount 시 현 페이지에서 설정한 전역설정 clear
   *
   * 웹소켓 연결 해제
   */
  useEffect(() => {
    leftSideStore.setTitle({
      label: "exit",
      icon: <ArrowLeft size={20} className="text-gray-400" />,
      button: true,
      onClick: () => {
        exitGameRoom(myInfo.gameRoomEntranceId);
      },
    });

    return () => {
      webSocketClientRef.current.deactivate();
      leftSideStore.clear();
    };
  }, []);

  /**
   * roomId 서버 조회 및 validation
   */
  useEffect(() => {
    findCurrentGameRoomInfo().catch((error) => alert(error));
  }, [roomId]);

  /**
   * leftbar 리스트 등록
   */
  useEffect(() => {
    const getLeftSideItemTheme = (role, ready) => {
      if (role === "GUEST" && ready) {
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

  // 여기서 웹소켓 클라이언트 ref 내려주기
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
