import { createContext, useContext, useEffect, useState } from 'react';
import { a } from 'tailwindcss/dist/chunk-HTB5LLOP.mjs';


const AuthContext = createContext(null);
const accessTokenKey = 'accessToken';
const refreshTokenKey = 'refreshToken';

export function AuthProvider({ childeren }) {
  const [accessToken, setAccessToken] = useState(null);
  const [refreshToken, setRefreshToken] = useState(null);
  const [loading, setLoading] = useState(true);


  useEffect(() => {
    // 앱 시작 시 localStorage에서 JWT 복원
    const storedAccessToken = localStorage.getItem(accessTokenKey);
    const storedRefreshToken = localStorage.getItem(refreshTokenKey);

    if (storedAccessToken) setAccessToken(storedAccessToken);
    if (storedRefreshToken) setRefreshToken(storedRefreshToken);

    setLoading(false);
  }, []);

  const login = ({ accessToken, refreshToken }) => {
    localStorage.setItem(accessTokenKey, accessToken);
    localStorage.setItem(refreshTokenKey, refreshToken);

    setAccessToken(accessToken);
    setRefreshToken(refreshToken);
  };

  const logout = () => {
    localStorage.removeItem(accessTokenKey);
    localStorage.removeItem(refreshTokenKey);

    setAccessToken(null);
    setRefreshToken(null);
  };

  return (
    <AuthContext.Provider value={{
      accessToken,
      refreshToken,
      login,
      logout,
      isAuthenticated: !!accessToken,
      loading
    }}>
      {childeren}
    </AuthContext.Provider>
  );
}

/**
 * 전역에서 Hook 사용
 * @returns {null}
 */
export function useAuth() {
  const context = useContext(AuthContext);

  if (!context) {
    throw new Error("AuthProvider 안에서만 사용 가능합니다.");
  }

  return context;
}
