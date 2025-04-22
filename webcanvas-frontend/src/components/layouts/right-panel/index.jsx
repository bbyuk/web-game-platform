import { MessageCircle } from "lucide-react";

export default function RightPanel() {
  return (
    <div className="w-60 bg-gray-900 border-l border-gray-700 p-4">
      <div className="text-lg font-bold mb-4">
        <MessageCircle className="inline-block text-gray-400 mr-2" />
        chat
      </div>
      <div className="space-y-2">
        <button className="w-full bg-indigo-600 hover:bg-indigo-700 p-2 rounded">Build</button>
        <button className="w-full bg-green-600 hover:bg-green-700 p-2 rounded">Deploy</button>
      </div>
    </div>
  );
}
