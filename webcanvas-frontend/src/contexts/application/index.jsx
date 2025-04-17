import { createContext, useContext, useEffect, useState } from 'react';
import { auth } from '@/api/index.js';
import { useNavigate } from 'react-router-dom';

const ApplicationContext = createContext(null);

export function ApplicationContextProvider({ children }) {

  const navigate = useNavigate();
  const [savedAccessToken, setSavedAccessToken] = useState(localStorage.getItem("accessToken"));


  /**
   * constant with hook
   */
  /**
   * fetch wrapper
   * @param method
   * @param url
   * @param data
   * @param options
   * @param refreshedAccessToken
   * @returns {Promise<any>}
   */
  const request = async (method, url, data = {}, options = {}, refreshedAccessToken) => {
    const headers = {
      ...api.constants.defaultHeaders,
      ...(refreshedAccessToken
        ? { Authorization: `Bearer ${refreshedAccessToken}` }
        : savedAccessToken && { Authorization: `Bearer ${savedAccessToken}` }),
      ...options.headers,
    };

    const processedUrl = `${api.constants.serverDomain}${method === "GET" ? api.utils.buildUrlWithParams(url, data) : url}`;
    const fetchOption = {
      method,
      headers,
      ...options,
      ...(method !== "GET" && { body: JSON.stringify(data) }),
    };

    return fetch(processedUrl, fetchOption)
      .then(async (response) => {
        if (!response.ok) {
          const error = await response.json();

          throw {
            status: response.status,
            ...error,
          };
        }

        return response.json();
      })
      .catch(async (error) => {
        if (error.status === 401) {
          if (error.code !== "A001") {
            /**
             * 토큰 만료가 아닐 경우 unauthorized handler 호출
             */
            authentication.handleUnauthorized();
            return;
          }
          /**
           * 토큰 만료시
           * 1. 토큰 refresh 요청
           * 2. API 재요청
           */

          /**
           * 토큰 refresh
           */
          const { accessToken, fingerprint } = await api.post(auth.refresh, {}, {
            credentials: "include"
          });
          authentication.saveAuthentication({accessToken, fingerprint});

          /**
           * API 재요청
           */
          return request(method, url, data, options, accessToken);
        }
      });
  };
  const api = {
    get: async (url, params = {}, options = {}) => {
      return request("GET", url, params, options);
    },
    post: async (url, data = {}, options = {}) => {
      return request("POST", url, data, options);
    },
    constants: {
      serverDomain:  import.meta.env.VITE_WEB_CANVAS_SERVICE,
      defaultHeaders: {
        "Content-Type": "application/json",
      }
    },
    utils: {
      buildUrlWithParams: (url, params = {}) => {
        const query = new URLSearchParams(params).toString();
        return query ? `${url}?${query}` : url;
      }
    }
  };
  const authentication = {
    /**
     * 로그인 API 응답 받은 후 클라이언트 localStorage Authentication token과 유저 등록시 생성된 fingerprint를 저장한다.
     * @param accessToken
     * @param fingerprint
     */
    saveAuthentication: ({ accessToken, fingerprint }) => {
      localStorage.setItem("accessToken", accessToken);
      localStorage.setItem("fingerprint", fingerprint);

      setSavedAccessToken(accessToken);
    },
    /**
     * 로그아웃 API 응답 받은 후 클라이언트 localStorage Authentication token을 삭제한다
     */
    handleUnauthorized: () => {
      localStorage.removeItem("accessToken");
      setSavedAccessToken(null);

      navigate("/", { replace : true });
    },
    isAuthenticated: !!savedAccessToken
  };

  useEffect(() => {
    if (!savedAccessToken) {
      /**
       * 앱 진입 후 authenticated 상태가 아니면 자동 로그인 요청
       */
      const fingerprint = localStorage.getItem("fingerprint");
      api.post(auth.login, {
        fingerprint: fingerprint
      }, {
        credentials: "include"
      })
        .then( (response = {
          accessToken: String,
          fingerprint: String
        }) => {
          authentication.saveAuthentication(response);
        })
        .catch(error => {
          console.log(error);
        });
    }
  }, [savedAccessToken]);

  return (
    <ApplicationContext.Provider
      value={{
        api,
        authentication
      }}
    >
      {children}
    </ApplicationContext.Provider>
  );
}

/**
 * 전역에서 Hook 사용
 * @returns {null}
 */
export function useApplicationContext() {
  const context = useContext(ApplicationContext);

  if (!context) {
    throw new Error("ApplicationContext Provider 안에서만 사용 가능합니다.");
  }

  return context;
}
