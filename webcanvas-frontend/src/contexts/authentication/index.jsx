import { createContext, useContext, useEffect, useState } from "react";
import { auth } from "@/api/index.js";
import {useApiClient} from "@/hooks/api-client/index.jsx";
import {useNavigate} from "react-router-dom";
import {pages} from "@/router/index.jsx";

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
  const apiClient = useApiClient();
  const navigate = useNavigate();


  const login = () => {
    setIsAuthenticated(true);
  };

  const logout = () => {
    setIsAuthenticated(false);
  };


  useEffect(() => {
    /**
     * 앱 진입 인증 요청에 실패시 핸들링
     */
    const onAuthenticationFailedHandler = () => {
      const fingerprint = localStorage.getItem("fingerprint");
      apiClient
        .post(auth.login, {fingerprint: fingerprint})
        .then(async (response) => {
          const {fingerprint, success} = await response.json();
          localStorage.setItem("fingerprint", fingerprint);
          if (success) {
            login();
          }
        })
        .catch(error => {
          logout();
        });
    }
    /**
     * authentication 먼저 체크
     */
    apiClient
      .get(auth.authentication)
      .then(success => {
        if (success) {
          login();
        } else {
          logout();
        }
      })
      .catch((error) => {
        onAuthenticationFailedHandler();
      });

    return () => {
      logout();
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
