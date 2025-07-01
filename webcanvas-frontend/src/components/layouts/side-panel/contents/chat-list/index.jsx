import ChatBubble from "@/components/layouts/side-panel/contents/chat-list/chat-bubble/index.jsx";
import { useEffect, useRef } from "react";

const MAX_CHAT_COUNT = 100;

export default function ChatList({ messages, removeOldChat }) {
  const bottomRef = useRef(null);

  // 오래된 메시지 제거
  useEffect(() => {
    if (messages.length > MAX_CHAT_COUNT) {
      removeOldChat(MAX_CHAT_COUNT);
    }
  }, [messages]);

  // 메시지 추가 시 스크롤 아래로 부드럽게 이동
  useEffect(() => {
    if (bottomRef.current) {
      bottomRef.current.scrollIntoView({ behavior: "smooth" });
    }
  }, [messages]);

  return (
    <div className="flex flex-col h-full space-y-2 pr-1 overflow-y-auto custom-scrollbar">
      {messages.map((message, index) => (
        <ChatBubble
          key={`chat-message-${index}`}
          senderId={message.senderId}
          nickname={message.nickname}
          color={message.color}
          message={message.value}
        />
      ))}
      {/* 스크롤 트리거용 ref */}
      <div ref={bottomRef} />
    </div>
  );
}
