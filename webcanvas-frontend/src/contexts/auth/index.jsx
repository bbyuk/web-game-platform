import { createContext, useContext, useEffect, useState } from "react";

const AuthContext = createContext(null);
const accessTokenKey = "accessToken";
const refreshTokenKey = "refreshToken";

export function AuthProvider({ children }) {
  const [accessToken, setAccessToken] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    // 앱 시작 시 localStorage에서 JWT 복원

    const storedAccessToken = localStorage.getItem(accessTokenKey);

    if (storedAccessToken) setAccessToken(storedAccessToken);

    setLoading(false);
  }, []);

  const login = ({ accessToken, refreshToken }) => {
    localStorage.setItem(accessTokenKey, accessToken);
    localStorage.setItem(refreshTokenKey, refreshToken);

    setAccessToken(accessToken);
  };

  const logout = () => {
    localStorage.removeItem(accessTokenKey);
    localStorage.removeItem(refreshTokenKey);

    setAccessToken(null);
  };

  return (
    <AuthContext.Provider
      value={{
        accessToken,
        login,
        logout,
        isAuthenticated: !!accessToken,
        loading,
      }}
    >
      {!loading && children}
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
