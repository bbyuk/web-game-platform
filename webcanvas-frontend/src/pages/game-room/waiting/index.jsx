import React, { useEffect, useRef, useState } from 'react';
import { GameRoomWaitingPlaceholder } from "@/components/placeholder/game-room/waiting/index.jsx";
import { useNavigate, useOutletContext, useParams } from "react-router-dom";
import { useLeftSideStore } from "@/stores/layout/leftSideStore.jsx";
import SidePanelFooterButton from "@/components/layouts/side-panel/footer/button/index.jsx";
import { useApiLock } from "@/api/lock/index.jsx";
import { getApiClient } from "@/client/http/index.jsx";
import { game } from "@/api/index.js";
import { pages } from "@/router/index.jsx";
import ChatList from '@/components/layouts/side-panel/contents/chat-list/index.jsx';
import { MessageCircle } from 'lucide-react';
import SidePanelFooterInput from '@/components/layouts/side-panel/footer/input/index.jsx';
import { useRightSideStore } from '@/stores/layout/rightSideStore.jsx';
import { useAuthentication } from '@/contexts/authentication/index.jsx';

export default function GameRoomWaitingPage() {
  const { roomId } = useParams();

  // Outlet context
  const { enteredUsers, myInfo, changeReadyState, timePerTurn, webSocketClientRef } = useOutletContext();
  const { authenticatedUserId } = useAuthentication();
  const leftSideStore = useLeftSideStore();
  const rightSideStore = useRightSideStore();

  const { apiLock } = useApiLock();
  const apiClient = getApiClient();
  const navigate = useNavigate();

  const [readyStatus, setReadyStatus] = useState(null);
  const [chatMessages, setChatMessages] = useState([]);



  /**
   * =========================== 이벤트 핸들러 =============================
   */

  /**
   * 웹소켓을 통해 구독할 토픽과 콜백을 정의하고 구독한다.
   */
  const subscribeTopics = () => {
    console.log("게임 대기 방 이벤트 브로커 구독");

    /**
     * 채팅 메세지 브로커 핸들러
     * @param frame
     */
    const gameRoomChatHandler = (frame) => {

      const newMessage = {
        value: frame.value,
        senderId: frame.senderId,
        nickname: enteredUsers
          .filter(enteredUser => enteredUser.userId === frame.senderId)
          .map(enteredUser => enteredUser.nickname),
        color: enteredUsers
          .filter(enteredUser => enteredUser.userId === frame.senderId)
          .map(enteredUser => enteredUser.color)
      }

      setChatMessages((prevItems) => [...prevItems, newMessage]);
    };

    const topics = [
      // 게임 방 내 대기 채팅 broker
      {
        destination: `/room/${roomId}/chat`,
        messageHandler: gameRoomChatHandler,
      },
    ];

    console.log("game-room/waiting/index.jsx = 구독");
    webSocketClientRef.current.subscribe("room/waiting", topics);
  };

  /**
   * 게임을 시작한다.
   */
  const startGame = () => {
    /**
     * TODO 추후 옵션 제공
     */
    apiLock(
      game.startGame,
      async () =>
        await apiClient
          .post(game.startGame, {
            gameRoomId: roomId,
            turnCount: enteredUsers.length,
            timePerTurn: 60,
          })
          .then((response) => {
            navigate(pages.gameRoom.playing.url(roomId), { replace: true });
          })
    );
  };

  /**
   * 레디 버튼 토글
   */
  const toggleReady = () => {
    apiLock(
      game.updateReady(myInfo.gameRoomParticipantId),
      async () =>
        await apiClient
          .patch(game.updateReady(myInfo.gameRoomParticipantId), {
            ready: !myInfo.ready,
          })
          .then((response) => {
            changeReadyState(response);
          })
    );
  };

  useEffect(() => {
    if (myInfo.role === "HOST") {
      if (enteredUsers.length === 1 || enteredUsers.filter((user) => !user.ready).length > 0) {
        setReadyStatus("not-all-ready");
      } else {
        setReadyStatus("all-ready");
      }
    } else {
      if (myInfo.ready) {
        setReadyStatus("ready");
      } else {
        setReadyStatus("not-ready");
      }
    }
  }, [enteredUsers, myInfo]);

  useEffect(() => {
    const buttonOnClickHandler =
      myInfo.role === "HOST" ? startGame : myInfo.role === "GUEST" ? toggleReady : null;
    leftSideStore.setFooter({
      slot: SidePanelFooterButton,
      props: {
        status: readyStatus,
        onClick: buttonOnClickHandler,
      },
    });
    return () => {
      leftSideStore.clearFooter();
    };
  }, [readyStatus, myInfo]);

  useEffect(() => {
    rightSideStore.setContents({
      slot: ChatList,
      props: {
        messages: chatMessages,
        removeOldChat: (maxChatCount) => {
          setChatMessages((prev) => prev.slice(-maxChatCount));
        },
      },
    });
  }, [chatMessages]);

  useEffect(() => {
    rightSideStore.setTitle({
      label: "chat",
      icon: <MessageCircle size={20} className="text-gray-400" />,
      button: false,
      onClick: () => {},
    });

    rightSideStore.setContents({
      slot: ChatList,
      props: {
        messages: chatMessages,
        removeOldChat: (maxChatCount) => {
          setChatMessages((prev) => prev.slice(-maxChatCount));
        },
      },
    });
    rightSideStore.setFooter({
      slot: SidePanelFooterInput,
    });

    return () => {
      if (webSocketClientRef.current) {
        webSocketClientRef.current.unsubscribe("room/waiting");
      }
    };
  }, []);

  useEffect(() => {
    if (!webSocketClientRef.current || !authenticatedUserId) {
      return;
    }

    rightSideStore.setFooter({
      slot: SidePanelFooterInput,
      props: {
        onSubmit: (message) => {
          webSocketClientRef.current.send(`/room/${roomId}/chat/send`, {
            value: message,
          });
        },
      },
    });

    subscribeTopics();

  }, [authenticatedUserId, webSocketClientRef.current]);

  /**
   * =========================== 이벤트 핸들러 =============================
   */

  return <GameRoomWaitingPlaceholder readyStatus={readyStatus} />;
}
