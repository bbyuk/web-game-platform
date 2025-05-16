import {createContext, useContext, useEffect, useState} from 'react';
import {auth} from '@/api/index.js';
import {defaultHeaders, serverDomain} from "@/contexts/api-client/constnats.js";

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

  useEffect(() => {
    /**
     * authentication 먼저 체크
     */
    fetch(`${serverDomain}${auth.authentication}`, {
      method: "GET",
      headers: defaultHeaders,
      credentials: "include",
    })
      .then(response => {
        login();
      })
      .catch((error) => {
        /**
         * 앱 진입 후 authenticated 상태가 아니면 자동 로그인 요청
         */
        const fingerprint = localStorage.getItem("fingerprint");

        fetch(`${serverDomain}${auth.login}`, {
          method: "POST",
          headers: defaultHeaders,
          body: JSON.stringify({ fingerprint: fingerprint }),
          credentials: "include"
        }).then((response = {
          isAuthenticated: Boolean,
          fingerprint: String,
        }) => {
          localStorage.setItem("fingerprint", response.fingerprint);
        }).catch((error) => (console.log(error)));
      });

    return () => {
      logout();
    };
  }, []);

  const login = () => {
    setIsAuthenticated(true);
  };

  const logout = () => {
    setIsAuthenticated(false);
  };

  return <AuthenticationContext.Provider
    value={{
      isAuthenticated,
      login,
      logout
    }}>{children}</AuthenticationContext.Provider>;
};
