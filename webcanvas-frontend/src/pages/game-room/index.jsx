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
  const { authenticatedUserIdRef } = useAuthentication();
  const { apiLock } = useApiLock();
  const apiClient = getApiClient();
  const navigate = useNavigate();
  const webSocketClientRef = useRef(null);
  const leftSideStore = useLeftSideStore();

  /**
   * 페이지 상태
   */
  const [enteredUsers, setEnteredUsers] = useState([]);
  const [gameRoomEntranceId, setGameRoomEntranceId] = useState(null);
  const [nickname, setNickname] = useState(null);
  const [userColor, setUserColor] = useState(null);
  const [userRole, setUserRole] = useState(null);

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
        setGameRoomEntranceId(response.gameRoomEntranceId);
        setEnteredUsers(response.enteredUsers);

        const { nickname, color, role } = response.requesterUserSummary;

        setNickname(nickname);
        setUserColor(color);
        setUserRole(role);

        // stomp 연결
        if (roomId !== "temp") {
          connectToWebSocket(response.gameRoomId);
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
  const onOtherUserStateChange = () => {
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
  const connectToWebSocket = (gameRoomId) => {
    if (webSocketClientRef.current) {
      // 이전 client 존재시 deactivate
      webSocketClientRef.current.deactivate();
    }
    const options = {
      onConnect: (frame) => {
        console.log(frame);
      },
      onError: (frame) => {
        console.log(frame);
      },
    };
    webSocketClientRef.current = getWebSocketClient(options);
  };

  /**
   * 페이지 컴포넌트 unmount 시 현 페이지에서 설정한 전역설정 clear
   *
   * 웹소켓 연결 해제
   */
  useEffect(() => {
    return () => {
      webSocketClientRef.current.deactivate();
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
    if (enteredUsers.length > 0) {
      leftSideStore.setContents({
        slot: ItemList,
        props: {
          value: enteredUsers.map(({ nickname, ...rest }) => ({ label: nickname, ...rest })),
        },
      });
    }
  }, [enteredUsers]);

  /**
   * leftbar title 등록
   */
  useEffect(() => {
    leftSideStore.setTitle({
      label: "exit",
      icon: <ArrowLeft size={20} className="text-gray-400" />,
      button: true,
      onClick: () => {
        exitGameRoom(gameRoomEntranceId);
      },
    });
  }, [gameRoomEntranceId]);

  /**
   * 웹소켓 토픽 구독 및 메세지 핸들러 등록 useEffect
   */
  useEffect(() => {
    if (!webSocketClientRef.current || !authenticatedUserIdRef.current) {
      return;
    }

    /**
     * 게임 방 메세지 브로커 핸들러
     * @param frame
     */
    const gameRoomEventHandler = (frame) => {
      if (!frame.event) {
        // 서버로부터 받은 이벤트가 없음
        return;
      }
      const { event, userId } = frame;
      if (
        authenticatedUserIdRef.current !== userId &&
        (event === "ROOM/ENTRANCE" || event === "ROOM/EXIT")
      ) {
        /**
         * 다른 사람 입장 OR 퇴장 이벤트 발생시
         */
        onOtherUserStateChange();
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
  }, [webSocketClientRef.current, authenticatedUserIdRef.current]);

  // 여기서 웹소켓 클라이언트 ref 내려주기
  return (
    <Outlet
      context={{
        enteredUsers,
        nickname,
        userColor,
        webSocketClientRef,
        gameRoomEntranceId,
      }}
    />
  );
}
