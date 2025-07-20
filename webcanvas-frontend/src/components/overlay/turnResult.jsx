import React from 'react';
import ReactDOM from 'react-dom';
import { useGameSession } from '@/contexts/game-session/index.jsx';

export default function TurnResultOverlay() {
  const { turnResult } = useGameSession();
  if (!turnResult) return null;

  return ReactDOM.createPortal(
    <div className="
      fixed inset-0
      flex flex-col items-center justify-center
      bg-black/60 backdrop-blur-sm
      z-50 pointer-events-none
    ">
      <div className="bg-white/90 px-6 py-4 rounded-lg shadow-lg">
        <p className="text-2xl font-bold text-gray-800">
          {turnResult.message}
        </p>
      </div>
    </div>,
    document.body
  );
}
