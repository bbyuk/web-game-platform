import ChatBubble from "@/components/layouts/side-panel/contents/chat-list/chat-bubble/index.jsx";

export default function ChatList({ chats }) {
  return (
    <div className="flex flex-col h-full space-y-2 pr-1 overflow-y-auto custom-scrollbar">
      {chats.map((chat) => (
        <ChatBubble sender={chat.sender} message={chat.message} />
      ))}
    </div>
  );
}
