import { useCallback, useEffect, useRef, useState } from "react";

export function useTimer() {
  const [remainingPercent, setRemainingPercent] = useState(100);
  const timerRef = useRef(null);
  const startTimeRef = useRef(null);
  const durationRef = useRef(null);

  const ready = useCallback((durationSec) => {
    durationRef.current = durationSec;
  }, []);

  const start = useCallback((expiration) => {
    // 기존 타이머 정리
    if (timerRef.current) {
      clearInterval(timerRef.current);
      timerRef.current = null;
    }

    // expiration을 기준으로 시작 시각 계산
    const expirationTime = new Date(expiration).getTime(); // ms
    const durationMs = durationRef.current * 1000;
    const startTime = expirationTime - durationMs;

    startTimeRef.current = startTime;
    // 초기화
    setRemainingPercent(100);

    timerRef.current = setInterval(() => {
      const now = Date.now();
      const elapsed = now - startTimeRef.current;
      const progress = elapsed / durationMs;
      const percent = Math.max(0, 100 - progress * 100);

      setRemainingPercent(percent);
      console.log(percent);

      if (percent <= 0 || now >= expirationTime) {
        clearInterval(timerRef.current);
        timerRef.current = null;
      }
    }, 100);
  }, []);

  const stop = useCallback(() => {
    if (timerRef.current) {
      clearInterval(timerRef.current);
      timerRef.current = null;
    }
  }, []);

  useEffect(() => {
    return () => {
      stop();
    };
  }, [stop]);

  return {
    remainingPercent,
    ready,
    start,
    stop,
  };
}
