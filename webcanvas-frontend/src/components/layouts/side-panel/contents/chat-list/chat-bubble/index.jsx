/**
 * 말풍선 컴포넌트
 * @param sender
 * @param message
 * @returns {JSX.Element}
 * @constructor
 */
export default function ChatBubble({ sender, message }) {
  const isMe = sender === "me";

  return (
    <div className={`flex ${isMe ? "justify-end" : "justify-start"}`}>
      <div
        className={`max-w-[80%] px-3 py-2 rounded-2xl text-sm whitespace-pre-line
          ${isMe ? "bg-blue-600 text-white" : "bg-gray-700 text-gray-100"}`}
      >
        {message}
      </div>
    </div>
  );
}
