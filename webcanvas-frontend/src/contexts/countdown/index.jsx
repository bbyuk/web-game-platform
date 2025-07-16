// CountdownProvider.jsx
import React, { createContext, useState, useContext, useCallback, useRef } from 'react';
import ReactDOM from 'react-dom';

const CountdownContext = createContext(null);

export function CountdownProvider({ children }) {
  const [countdown, setCountdown] = useState(null);
  const intervalRef = useRef(null);

  const startCountdown = useCallback(
    (seconds, { message = '' } = {}, onFinish = () => {}) => {
      if (intervalRef.current) clearInterval(intervalRef.current);
      setCountdown({ timeLeft: seconds, message });
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
      {countdown &&
        ReactDOM.createPortal(
          // ↓ 여기 bg-black/50 과 backdrop-blur-sm을 추가
          <div className="
            fixed inset-0
            flex flex-col items-center justify-center
            bg-black/50 backdrop-blur-sm
            z-50 pointer-events-none
          ">
            {/* 정답 체크 애니메이션 */}
            {countdown.message && (
              <div className="relative mb-4">
                <svg
                  xmlns="http://www.w3.org/2000/svg"
                  className="absolute inset-0 w-20 h-20 text-green-400 opacity-75 animate-ping"
                  fill="none"
                  viewBox="0 0 24 24"
                  stroke="currentColor"
                >
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2}
                        d="M9 12l2 2l4 -4" />
                </svg>
                <svg
                  xmlns="http://www.w3.org/2000/svg"
                  className="relative w-20 h-20 text-green-500 animate-bounce"
                  fill="none"
                  viewBox="0 0 24 24"
                  stroke="currentColor"
                >
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2}
                        d="M9 12l2 2l4 -4" />
                </svg>
              </div>
            )}
            {/* 카운트다운 숫자 */}
            <div className="text-7xl text-white font-bold animate-pulse">
              {countdown.timeLeft}
            </div>
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
