import { createContext, useContext, useEffect, useState } from "react";
import {post} from "@/utils/request.js";
import {auth} from "@/api/index.js";
import {useNavigate} from "react-router-dom";

const AuthenticationContext = createContext(null);
const accessTokenKey = "accessToken";
const fingerprintKey = "fingerprint";

export function AuthenticationProvider({ children }) {
  const navigate = useNavigate();

  const [accessToken, setAccessToken] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    if (!accessToken) {
      /**
       * 앱 진입 후 authenticated 상태가 아니면 자동 로그인 요청
       */
      const fingerprint = localStorage.getItem(fingerprintKey);
      post(auth.login, {
        fingerprint: fingerprint
      }, {
        credentials: "include"
      })
        .then( (response = {
          accessToken: String,
          fingerprint: String
        }) => {
          saveAuthentication(response);
        })
        .catch(error => {
          console.log(error);
        });
    }
  }, [accessToken]);

  useEffect(() => {
    // 앱 시작 시 localStorage에서 JWT 복원
    const storedAccessToken = localStorage.getItem(accessTokenKey);
    if (storedAccessToken) setAccessToken(storedAccessToken);

    setLoading(false);
  }, []);

  /**
   * 로그인 API 응답 받은 후 클라이언트 localStorage Authentication token과 유저 등록시 생성된 fingerprint를 저장한다.
   * @param accessToken
   * @param fingerprint
   */
  const saveAuthentication = ({ accessToken, fingerprint }) => {
    localStorage.setItem(accessTokenKey, accessToken);
    localStorage.setItem(fingerprintKey, fingerprint);

    setAccessToken(accessToken);
  };

  /**
   * 로그아웃 API 응답 받은 후 클라이언트 localStorage Authentication token을 삭제한다
   */
  const handleUnauthorized = () => {
    localStorage.removeItem(accessTokenKey);
    setAccessToken(null);

    navigate("/", { replace : true });
  };

  return (
    <AuthenticationContext.Provider
      value={{
        accessToken,
        saveAuthentication,
        handleUnauthorized,
        isAuthenticated: !!accessToken,
        loading,
      }}
    >
      {!loading && children}
    </AuthenticationContext.Provider>
  );
}

/**
 * 전역에서 Hook 사용
 * @returns {null}
 */
export function useAuthentication() {
  const context = useContext(AuthenticationContext);

  if (!context) {
    throw new Error("Authentication Provider 안에서만 사용 가능합니다.");
  }

  return context;
}
