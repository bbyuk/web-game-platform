import { useState } from "react";
import { SendHorizonal } from "lucide-react";

export default function SidePanelFooterInput({ onSubmit }) {
  const [message, setMessage] = useState("");

  const handleSubmit = () => {
    if (message.trim() === "") return;
    onSubmit?.(message);
    setMessage("");
  };

  return (
    <div className="flex items-center gap-2">
      <input
        type="text"
        placeholder="메시지를 입력하세요"
        className="flex-1 min-w-0 px-2 py-1.5 rounded-md bg-gray-800 text-gray-200 placeholder-gray-500 outline-none text-sm"
        value={message}
        onChange={(e) => setMessage(e.target.value)}
        onKeyUp={(e) => {
          if (e.key === "Enter" && !e.shiftKey) {
            e.preventDefault();
            handleSubmit();
          }
        }}
      />
      <button
        onClick={handleSubmit}
        className="p-2 rounded-md bg-blue-600 hover:bg-blue-700 text-white"
        title="전송"
      >
        <SendHorizonal size={16} />
      </button>
    </div>
  );
}
