// CountdownProvider.jsx
import React, { createContext, useState, useContext, useCallback, useRef } from 'react';
import ReactDOM from 'react-dom';

const CountdownContext = createContext(null);

export function CountdownProvider({ children }) {
  const [countdown, setCountdown] = useState(null);
  const intervalRef = useRef(null);

  const startCountdown = useCallback(
    (seconds, { message = '', status = 'success' } = {}, onFinish = () => {}) => {
      if (intervalRef.current) clearInterval(intervalRef.current);

      setCountdown({ timeLeft: seconds, message, status });

      intervalRef.current = setInterval(() => {
        setCountdown(prev => {
          if (!prev || prev.timeLeft <= 1) {
            clearInterval(intervalRef.current);
            intervalRef.current = null;
            setTimeout(() => setCountdown(null), 500);
            onFinish();
            return null;
          }
          return { ...prev, timeLeft: prev.timeLeft - 1 };
        });
      }, 1000);
    },
    []
  );

  return (
    <CountdownContext.Provider value={startCountdown}>
      {children}
      {countdown && ReactDOM.createPortal(
        <div className="fixed inset-0 flex flex-col items-center justify-center bg-black bg-opacity-50 z-40 pointer-events-none">
          {countdown.message && (
            <div className={`text-2xl font-bold mb-4 ${
              countdown.status === 'success' ? 'text-green-500' : ''
            }`}
            >
              {countdown.message}
            </div>
          )}
          <div className="text-6xl text-white animate-pulse">{countdown.timeLeft}</div>
        </div>,
        document.body
      )}
    </CountdownContext.Provider>
  );
}

export function useCountdown() {
  const ctx = useContext(CountdownContext);
  if (!ctx) throw new Error('useCountdown must be used within CountdownProvider');
  return ctx;
}
