import { createContext, useContext, useEffect, useState } from "react";
import { auth } from "@/api/index.js";
import { useNavigate } from "react-router-dom";
import { EMPTY_MESSAGES } from "@/constants/message.js";

const ApplicationContext = createContext(null);

export function ApplicationContextProvider({ children }) {
  /**
   * ========================= states ===========================
   */

  const navigate = useNavigate();
  /**
   * api authentication 관련 토큰 state
   */
  const [savedAccessToken, setSavedAccessToken] = useState(localStorage.getItem("accessToken"));

  /**
   * 상단 바 관련 state
   */
  const [selectedTopTabIndex, setSelectedTopTabIndex] = useState(-1);
  const [topTabItems, setTopTabItems] = useState([]);

  /**
   * 좌측 sidebar 관련 state
   */
  const [leftSidebarItems, setLeftSidebarItems] = useState([]);
  const [leftSidebarEmptyPlaceholder, setLeftSidebarEmptyPlaceholder] = useState(
    EMPTY_MESSAGES.GENERIC
  );

  /**
   * ========================= states ===========================
   */

  const useMock = import.meta.env.VITE_USE_MOCK === "true";

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
          const accessToken = await api.tokenRefresh();

          if (!accessToken) {
            /**
             * 인증 실패 -> 토큰 만료시 refresh 시도 -> refresh마저 실패시 unauthorized handling
             */
            authentication.handleUnauthorized();
            return;
          }

          /**
           * API 재요청
           */
          return request(method, url, data, options, accessToken);
        } else {
          /**
           * TODO alert modal로 변경
           */
          alert(error.message);
        }
      });
  };

  /**
   * api 요청 클라이언트
   * @type {{get: (function(*, {}=, {}=): Promise<*>), post: (function(*, {}=, {}=): Promise<*>), tokenRefresh: (function(): Promise<*>), constants: {serverDomain: any, defaultHeaders: {'Content-Type': string}}, utils: {buildUrlWithParams: (function(*, {}=): string|*)}}}
   */
  const api = {
    get: async (target, params = {}, options = {}) => {
      if (useMock) {
        return Promise.resolve(target.mock);
      }
      return request("GET", target.url, params, options);
    },
    post: async (target, data = {}, options = {}) => {
      if (useMock) {
        return Promise.resolve(target.mock);
      }
      return request("POST", target.url, data, options);
    },
    tokenRefresh: async () => {
      const processedUrl = `${api.constants.serverDomain}${auth.refresh}`;
      const options = {
        credentials: "include",
      };
      const fetchOption = {
        method: "POST",
        ...options,
        headers: api.constants.defaultHeaders,
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
          /**
           * refresh 성공
           */
          const { accessToken, fingerprint } = await response.json();
          authentication.saveAuthentication({ accessToken, fingerprint });

          return accessToken;
        })
        .catch((error) => {
          alert(error.message);
          return null;
        });
    },
    constants: {
      serverDomain: import.meta.env.VITE_WEB_CANVAS_SERVICE,
      defaultHeaders: {
        "Content-Type": "application/json",
      },
    },
    utils: {
      buildUrlWithParams: (url, params = {}) => {
        const query = new URLSearchParams(params).toString();
        return query ? `${url}?${query}` : url;
      },
    },
  };

  /**
   * 인증에 관련된 전역 상태 context
   * @type {{saveAuthentication: authentication.saveAuthentication, handleUnauthorized: authentication.handleUnauthorized, isAuthenticated: boolean}}
   */
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

      navigate("/", { replace: true });
    },
    isAuthenticated: !!savedAccessToken,
  };

  /**
   * 상단 탭 관련 context
   * @type {{selectedIndex: number, onSelected: (function(*): void), items: *[], clear: topTabs.clear, setValue: topTabs.setValue}}
   */
  const topTabs = {
    selectedIndex: selectedTopTabIndex,
    onSelected: (index) => setSelectedTopTabIndex(index),
    items: topTabItems,
    clear: () => {
      setTopTabItems([]);
      setSelectedTopTabIndex(-1);
    },
    setItems: (value) => {
      setTopTabItems(value);
      setSelectedTopTabIndex(0);
    },
  };

  /**
   * 좌측 sidebar 관련 context
   * @type {{}}
   */
  const leftSidebar = {
    items: leftSidebarItems,
    emptyPlaceholder: leftSidebarEmptyPlaceholder,
    setItems: (value) => {
      setLeftSidebarItems(value);
    },
    setEmptyPlaceholder: (value) => {
      setLeftSidebarEmptyPlaceholder(value);
    },
    clear: () => {
      setLeftSidebarItems([]);
      setLeftSidebarEmptyPlaceholder(EMPTY_MESSAGES.GENERIC);
    },
  };

  useEffect(() => {
    if (!savedAccessToken) {
      /**
       * 앱 진입 후 authenticated 상태가 아니면 자동 로그인 요청
       */
      const fingerprint = localStorage.getItem("fingerprint");
      api
        .post(
          auth.login,
          {
            fingerprint: fingerprint,
          },
          {
            credentials: "include",
          }
        )
        .then(
          (
            response = {
              accessToken: String,
              fingerprint: String,
            }
          ) => {
            authentication.saveAuthentication(response);
          }
        )
        .catch((error) => {
          console.log(error);
        });
    }
  }, [savedAccessToken]);

  return (
    <ApplicationContext.Provider
      value={{
        api,
        authentication,
        topTabs,
        leftSidebar,
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
