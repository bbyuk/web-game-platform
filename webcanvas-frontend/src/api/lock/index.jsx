import { useRef } from "react";

/**
 * API 중복 호출을 막기위한 apiLock를 제공하는 hook
 * @returns {{apiLock: ((function(*, *): Promise<undefined|*>)|*), resetLocks: resetLocks}}
 */
export const useApiLock = () => {
  const lockMapRef = useRef(new Map());

  const apiLock = async (key, fn) => {
    if (lockMapRef.current.get(key)) {
      alert("작업을 수행중입니다.");
      return;
    }

    lockMapRef.current.set(key, true);
    try {
      return await fn();
    } finally {
      lockMapRef.current.set(key, false);
    }
  };

  const resetLocks = () => {
    lockMapRef.current.clear();
  };

  return { apiLock, resetLocks };
};
