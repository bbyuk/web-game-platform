export function LobbyPlaceholder({ className = String(), onMakeRoomRequest = () => {} }) {
  return (
    <div className={`flex flex-col items-center justify-center h-full text-gray-400 ${className}`}>
      <div className="text-6xl mb-4">🖌️</div>
      <div className="text-xl font-semibold mb-2">아직 참여 중인 게임이 없습니다.</div>
      <div className="text-sm mb-6">방을 생성하거나 대기중인 방에 입장하세요.</div>
      <button onClick={onMakeRoomRequest} className="px-4 py-2 bg-blue-600 hover:bg-blue-700 text-white rounded-lg shadow hover:cursor-pointer">
        방 만들기
      </button>
    </div>
  );
}
