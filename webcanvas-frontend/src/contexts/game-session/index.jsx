import React, {
  createContext,
  useState,
  useContext,
  useCallback,
  useRef,
  useEffect
} from 'react';

const GameSessionContext = createContext(null);

export function GameSessionProvider({ children }) {
  // 1) Countdown state
  const [countdown, setCountdown] = useState(null);
  const countdownRef = useRef(null);

  const startCountdown = useCallback(
    (seconds, message = '', onFinish = () => {}) => {
      if (countdownRef.current) clearInterval(countdownRef.current);
      setCountdown({ timeLeft: seconds, message });

      countdownRef.current = setInterval(() => {
        setCountdown(prev => {
          if (!prev || prev.timeLeft <= 1) {
            clearInterval(countdownRef.current);
            countdownRef.current = null;
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

  // 2) Scoreboard state
  const [scoreboard, setScoreboard] = useState([]);
  const updateScoreboard = useCallback(newScores => {
    setScoreboard(newScores);
  }, []);
  const incrementScore = useCallback((playerId, delta = 1) => {
    setScoreboard(prev =>
      prev.map(p =>
        p.id === playerId ? { ...p, score: p.score + delta } : p
      )
    );
  }, []);


  // 3) TURN RESULT state
  const [turnResult, setTurnResult] = useState(null);
  const turnResultTimer = useRef(null);
  const startTurnResult = useCallback(
    (message, delaySeconds = 3, onFinish = () => {}) => {
      // 이전 결과 초기화
      if (turnResultTimer.current) clearTimeout(turnResultTimer.current);
      setTurnResult({ message });

      // delaySeconds 후 클리어 & 콜백
      turnResultTimer.current = setTimeout(() => {
        setTurnResult(null);
        onFinish();
      }, delaySeconds * 1000);
    },
    []
  );

  // cleanup on unmount
  useEffect(() => () => {
    if (countdownRef.current) clearInterval(countdownRef.current);
    if (turnResultTimer.current) clearTimeout(turnResultTimer.current);
  }, []);

  return (
    <GameSessionContext.Provider
      value={{
        countdown,
        startCountdown,
        turnResult,
        startTurnResult,
        scoreboard,
        updateScoreboard,
        incrementScore
      }}
    >
      {children}
    </GameSessionContext.Provider>
  );
}

export function useGameSession() {
  const ctx = useContext(GameSessionContext);
  if (!ctx) throw new Error('useGameSession must be used within GameSessionProvider');
  return ctx;
}
