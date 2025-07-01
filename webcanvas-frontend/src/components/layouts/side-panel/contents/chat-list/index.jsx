import ChatBubble from "@/components/layouts/side-panel/contents/chat-list/chat-bubble/index.jsx";
import { useEffect } from "react";

const MAX_CHAT_COUNT = 100;

export default function ChatList({ messages, removeOldChat }) {
  useEffect(() => {
    if (messages.length > MAX_CHAT_COUNT) {
      removeOldChat(MAX_CHAT_COUNT);
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
    </div>
  );
}
