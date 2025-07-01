import { useAuthentication } from "@/contexts/authentication/index.jsx";

/**
 * 채팅 말풍선
 * @param senderId
 * @param message
 * @param nickname
 * @param color
 * @returns {JSX.Element}
 */
export default function ChatBubble({ senderId, message, nickname, color }) {
  const { authenticatedUserId } = useAuthentication();
  const isMe = authenticatedUserId === senderId;

  return (
    <div className={`flex ${isMe ? "justify-end" : "justify-start"}`}>
      <div className="flex flex-col max-w-[80%]">
        {!isMe && (
          <span
            className="mb-1 ml-1 text-xs font-semibold"
            style={{ color }}
          >
            {nickname}
          </span>
        )}
        <div
          className={`px-3 py-2 rounded-2xl text-sm whitespace-pre-line break-words
            ${isMe ? "bg-blue-600 text-white self-end" : "bg-gray-700 text-gray-100 self-start"}`}
        >
          {message}
        </div>
      </div>
    </div>
  );
}
