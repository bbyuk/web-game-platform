import { useEffect, useState, useRef } from "react";

/**
 * @param {number} durationSec - 총 지속 시간 (초)
 * @param {boolean} isRunning - 타이머 실행 여부
 * @param {() => void} onExpire - 시간 종료 시 실행되는 콜백
 */
export function useTimer({ durationSec, isRunning, onExpire }) {
  const [remainingTime, setRemainingTime] = useState(durationSec);
  const intervalRef = useRef(null);

  // 🔁 durationSec이 바뀌면 초기화 (단, isRunning 상태에서만)
  useEffect(() => {
    if (isRunning) {
      setRemainingTime(durationSec);
    }
  }, [durationSec, isRunning]);

  useEffect(() => {
    if (!isRunning) {
      clearInterval(intervalRef.current);
      return;
    }

    console.log("타이머 새로 시작");

    // 타이머 새로 시작
    clearInterval(intervalRef.current);
    intervalRef.current = setInterval(() => {
      setRemainingTime((prev) => {
        if (prev <= 1) {
          clearInterval(intervalRef.current);
          onExpire?.();
          return 0;
        }
        return prev - 1;
      });
    }, 1000);

    return () => clearInterval(intervalRef.current);
  }, [isRunning]);

  const percent = (remainingTime / durationSec) * 100;

  return {
    remainingTime,
    remainingPercent: percent,
    stop: () => clearInterval(intervalRef.current),
    reset: () => setRemainingTime(durationSec),
  };
}
