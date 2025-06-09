import ChatBubble from "@/components/layouts/side-panel/contents/chat-list/chat-bubble/index.jsx";
import { useEffect } from "react";

const MAX_CHAT_COUNT = 100;

export default function ChatList({ chats, removeOldChat }) {
  useEffect(() => {
    if (chats.length > MAX_CHAT_COUNT) {
      removeOldChat(MAX_CHAT_COUNT);
    }
  }, [chats]);

  return (
    <div className="flex flex-col h-full space-y-2 pr-1 overflow-y-auto custom-scrollbar">
      {chats.map((chat, index) => (
        <ChatBubble key={`chat-${index}`} senderId={chat.senderId} value={chat.value} />
      ))}
    </div>
  );
}
