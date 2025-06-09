export default function GameTurnTimer({ remainingPercent }) {
  return (
    <div className="absolute top-2 left-1/2 -translate-x-1/2 z-20">
      <div className="w-64 h-2 bg-gray-700 rounded-full overflow-hidden shadow-sm">
        <div
          className="h-full bg-blue-500 transition-all duration-300"
          style={{ width: `${remainingPercent}%` }}
        />
      </div>
    </div>
  );
}
