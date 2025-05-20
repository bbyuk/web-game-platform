export function GameRoomWaitingPlaceholder({ className = "" }) {
  return (
    <div className={`flex flex-col items-center justify-center h-full text-gray-400 ${className}`}>
      <div className="text-6xl mb-4">⏳</div>
      <div className="text-xl font-semibold mb-2">게임을 시작할 준비가 되었습니다.</div>
      <div className="text-sm mb-6">
        모든 참가자가 입장했는지 확인한 후, 방장이 게임을 시작하세요.
      </div>
      <div className="flex space-x-2">
        <div className="w-3 h-3 rounded-full bg-green-500 animate-bounce" />
        <div className="w-3 h-3 rounded-full bg-yellow-500 animate-bounce delay-150" />
        <div className="w-3 h-3 rounded-full bg-red-500 animate-bounce delay-300" />
      </div>
    </div>
  );
}
