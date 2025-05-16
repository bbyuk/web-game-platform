import { createContext, useContext, useEffect, useState } from "react";
import { auth } from "@/api/index.js";
import { defaultHeaders, serverDomain } from "@/hooks/api-client/constnats.js";
import {useApiClient} from "@/hooks/api-client/index.jsx";

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

  useEffect(() => {
    /**
     * authentication 먼저 체크
     */
    apiClient.get(auth.authentication)
      .then(response => {
        if (response.ok) {
          login();
        } else {
          throw new Error();
        }
      })

    fetch(`${serverDomain}${auth.authentication}`, {
      method: "GET",
      headers: defaultHeaders,
      credentials: "include",
    })
      .then((response) => {
        if (response.ok) {
          login();
        } else {
          /**
           * 앱 진입 후 authenticated 상태가 아니면 자동 로그인 요청
           */
          const fingerprint = localStorage.getItem("fingerprint");
          fetch(`${serverDomain}${auth.login}`, {
            method: "POST",
            headers: defaultHeaders,
            body: JSON.stringify({ fingerprint: fingerprint }),
            credentials: "include",
          })
            .then(async (response) => {
              const { fingerprint, isAuthenticated } = await response.json();
              localStorage.setItem("fingerprint", fingerprint);

              if (isAuthenticated) {
                login();
              }
            })
            .catch((error) => console.log(error));
        }
      })
      .catch((error) => {
        alert(error);
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

  return (
    <AuthenticationContext.Provider
      value={{
        isAuthenticated,
        login,
        logout,
      }}
    >
      {children}
    </AuthenticationContext.Provider>
  );
};
