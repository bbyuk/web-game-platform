import React from 'react';
import ReactDOM from 'react-dom';
import { useGameSession } from '@/contexts/game-session/index.jsx';

export default function ScoreboardOverlay() {
  const { scoreboard } = useGameSession();
  if (!scoreboard || scoreboard.length === 0) return null;

  return ReactDOM.createPortal(
    <div className="fixed top-4 left-4 bg-black/60 backdrop-blur-sm p-3 rounded-lg z-50 pointer-events-none">
      <ul className="space-y-1 text-white">
        {scoreboard.map(player => (
          <li key={player.id} className="flex justify-between w-32">
            <span>{player.name}</span>
            <span>{player.score}</span>
          </li>
        ))}
      </ul>
    </div>,
    document.body
  );
}
