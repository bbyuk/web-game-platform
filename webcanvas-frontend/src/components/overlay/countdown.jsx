import React from 'react';
import ReactDOM from 'react-dom';
import { useGameSession } from '@/contexts/game-session/index.jsx';

export default function CountdownOverlay() {
  const { countdown } = useGameSession();
  if (!countdown) return null;

  return ReactDOM.createPortal(
    <div className="fixed inset-0 flex flex-col items-center justify-center bg-black/50 backdrop-blur-sm z-50 pointer-events-none">
      {countdown.message && (
        <div className="text-3xl text-white font-bold mb-2">
          {countdown.message}
        </div>
      )}
      <div className="text-7xl text-white font-extrabold animate-pulse">
        {countdown.timeLeft}
      </div>
    </div>,
    document.body
  );
}
