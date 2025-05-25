export function GameRoomWaitingPlaceholder({ className = "", role, ready, allGuestsReady }) {
  let icon = "⏳";
  let title = "";
  let description = "";

  if (role === "HOST") {
    if (allGuestsReady) {
      icon = "🚀";
      title = "모든 플레이어가 준비되었습니다!";
      description = "이제 게임을 시작할 수 있어요. START 버튼을 눌러주세요.";
    } else {
      icon = "⏱️";
      title = "플레이어들을 기다리는 중...";
      description = "모든 플레이어가 준비할 때까지 기다려주세요.";
    }
  } else {
    if (ready) {
      icon = "🙌";
      title = "준비 완료!";
      description = "호스트가 게임을 시작할 때까지 기다려주세요.";
    } else {
      icon = "🕹️";
      title = "게임을 시작할 준비가 되셨나요?";
      description = "준비가 되면 READY 버튼을 눌러주세요.";
    }
  }

  return (
    <div className={`flex flex-col items-center justify-center h-full text-gray-400 ${className}`}>
      <div className="text-6xl mb-4">{icon}</div>
      <div className="text-xl font-semibold mb-2">{title}</div>
      <div className="text-sm mb-6 text-center">{description}</div>
      <div className="flex space-x-2">
        <div className="w-3 h-3 rounded-full bg-green-500 animate-bounce" />
        <div className="w-3 h-3 rounded-full bg-yellow-500 animate-bounce delay-150" />
        <div className="w-3 h-3 rounded-full bg-red-500 animate-bounce delay-300" />
      </div>
    </div>
  );
}
