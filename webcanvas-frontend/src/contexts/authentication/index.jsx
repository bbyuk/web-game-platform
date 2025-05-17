import { createContext, useContext, useEffect, useState } from "react";
import { auth } from "@/api/index.js";
import {getApiClient} from "@/client/http/index.jsx";
import {useNavigate} from "react-router-dom";
import {pages} from "@/router/index.jsx";
import { STORAGE_KEY } from '@/constants/storage-key.js';

const AuthenticationContext = createContext(null);
export const useAuthentication = () => useContext(AuthenticationContext);

/**
 * 전역 Authentication context
 * @param children
 * @returns {JSX.Element}
 * @constructor
 */
export const AuthenticationProvider = ({ children }) => {
  const [isAuthenticated, setIsAuthenticated] = useState(false);
  const apiClient = getApiClient();
  const navigate = useNavigate();


  const onAuthenticationSuccess = () => {
    setIsAuthenticated(true);
  };

  const onLoginSuccess = (fingerprint, accessToken) => {
    localStorage.setItem(STORAGE_KEY.FINGERPRINT, fingerprint);
    localStorage.setItem(STORAGE_KEY.ACCESS_TOKEN, accessToken);

    onAuthenticationSuccess();
  }

  const onAuthenticationFailed = () => {
    localStorage.removeItem(STORAGE_KEY.ACCESS_TOKEN);
    setIsAuthenticated(false);
  };


  useEffect(() => {
    /**
     * authentication 먼저 체크
     */
    apiClient
      .get(auth.authentication)
      .then(success => {
        if (success) {
          onAuthenticationSuccess();
        } else {
          onAuthenticationFailed();
        }
      })
      .catch((error) => {
        /**
         * 앱 진입 인증 요청에 실패시 핸들링
         * 자동 로그인 요청
         */

        const fingerprint = localStorage.getItem(STORAGE_KEY.FINGERPRINT);
        apiClient
          .post(auth.login, {fingerprint: fingerprint})
          .then(async ({fingerprint, accessToken, success}) => {
            if (success) {
              onLoginSuccess(fingerprint, accessToken);
            }
          })
          .catch(error => {
            onAuthenticationFailed();
          });
      });

    return () => {
      onAuthenticationFailed();
    };
  }, []);

  useEffect(() => {
    if (!isAuthenticated) {
      navigate(pages.landing, {replace : true});
    }
  }, [isAuthenticated]);

  return (
    <AuthenticationContext.Provider
      value={{
        isAuthenticated
      }}
    >
      {children}
    </AuthenticationContext.Provider>
  );
};
