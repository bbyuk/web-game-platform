export default function LNB() {
  return (
    <div className="w-64 bg-gray-900 flex flex-col border-r border-gray-700">
      <div className="h-12 flex items-center px-4 border-b border-gray-700">
        <span className="text-sm font-semibold">유저 닉네임 영역</span>
      </div>
      {/* 방 리스트 */}
      <div className="flex-1 overflow-y-auto p-2 space-y-2">
        {["방1", "방2", "방3", "방4"].map((room, idx) => (
          <div key={idx} className="flex items-center p-2 rounded hover:bg-gray-700 cursor-pointer">
            <span className="material-icons mr-2 text-gray-400 text-base">meeting_room</span>
            <span>{room}</span>
          </div>
        ))}
      </div>
    </div>
  );
}
