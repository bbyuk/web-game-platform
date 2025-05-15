import { createContext, useContext, useEffect, useState } from "react";
import { auth } from "@/api/index.js";
import { useNavigate } from "react-router-dom";
import { EMPTY_MESSAGES } from "@/constants/message.js";
import { GitCommit, MessageCircle } from "lucide-react";

const ApplicationContext = createContext(null);

export function ApplicationContextProvider({ children }) {
  const navigate = useNavigate();
  /**
   * ========================= states ===========================
   */
  const init = {
    selectedTopTabIndex: -1,
    topTabItems: [],
    leftSidebarItems: [],
    leftSidebarEmptyPlaceholder: EMPTY_MESSAGES.GENERIC,
    leftSidebarTitle: {
      label: "main",
      icon: <GitCommit size={20} className="text-gray-400" />,
      button: false,
      onClick: () => {},
    },
    rightSidebarTitle: {
      label: "chat",
      icon: <MessageCircle size={20} className="text-gray-400" />,
      button: false,
      onClick: () => {},
    },
    currentGameRoomId: null,
    currentGameRoomEntranceId: null,
    currentGameRoomEnteredUsers: [],
  };

  /**
   * 상단 바 관련 state
   */
  const [selectedTopTabIndex, setSelectedTopTabIndex] = useState(init.selectedTopTabIndex);
  const [topTabItems, setTopTabItems] = useState(init.topTabItems);

  /**
   * 좌측 sidebar 관련 state
   */
  const [leftSidebarItems, setLeftSidebarItems] = useState(init.leftSidebarItems);
  const [leftSidebarEmptyPlaceholder, setLeftSidebarEmptyPlaceholder] = useState(
    init.leftSidebarEmptyPlaceholder
  );

  const [leftSidebarTitle, setLeftSidebarTitle] = useState(init.leftSidebarTitle);

  /**
   * 우측 sidebar 관련 state
   */
  const [rightSidebarTitle, setRightSidebarTitle] = useState(init.rightSidebarTitle);

  /**
   * 현재 입장한 게임 방 관련 state
   */
  // 현재 입장한 방의 ID
  const [currentGameRoomId, setCurrentGameRoomId] = useState(init.currentGameRoomId);
  // 현재 입장 ID
  const [currentGameRoomEntranceId, setCurrentGameRoomEntranceId] = useState(
    init.currentGameRoomEntranceId
  );
  const [currentGameRoomEnteredUsers, setCurrentGameRoomEnteredUsers] = useState(
    init.currentGameRoomEnteredUsers
  );

  /**
   * ========================= states ===========================
   */

  /**
   * constant with hook
   */
  /**
   * fetch wrapper
   * @param method
   * @param url
   * @param data
   * @param options
   * @returns {Promise<any>}
   */
  const request = async (method, url, data = {}, options = {}) => {
    const headers = {
      ...api.constants.defaultHeaders,
      ...options.headers,
    };

    const processedUrl = `${api.constants.serverDomain}${method === "GET" || method === "DELETE" ? api.utils.buildUrlWithParams(url, data) : url}`;
    const fetchOption = {
      method,
      headers,
      credentials: "include",
      ...options,
      ...(method !== "GET" && method !== "DELETE" && { body: JSON.stringify(data) }),
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
            navigate("/", { replace: true });
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
          const { isAuthenticated } = await api.tokenRefresh();

          if (!isAuthenticated) {
            /**
             * 인증 실패 -> 토큰 만료시 refresh 시도 -> refresh마저 실패시 unauthorized handling
             */
            navigate("/", { replace: true });
            return;
          }

          /**
           * API 재요청
           */
          return request(method, url, data, options);
        } else {
          /**
           * TODO alert modal로 변경
           */
          alert(error.message);
          throw error;
        }
      });
  };

  /**
   * api 요청 클라이언트
   * @type {{get: (function(*, {}=, {}=): Promise<*>), post: (function(*, {}=, {}=): Promise<*>), tokenRefresh: (function(): Promise<*>), constants: {serverDomain: any, defaultHeaders: {'Content-Type': string}}, utils: {buildUrlWithParams: (function(*, {}=): string|*)}}}
   */
  const api = {
    get: async (target, params = {}, options = {}) => {
      return request("GET", target, params, options);
    },
    post: async (target, data = {}, options = {}) => {
      return request("POST", target, data, options);
    },
    put: (target, data = {}, options = {}) => {
      return request("PUT", target, data, options);
    },
    delete: (target, params = {}, options = {}) => {
      return request("DELETE", target, params, options);
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
          const { fingerprint, isAuthenticated } = await response.json();
          localStorage.setItem("fingerprint", fingerprint);

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
   * 상단 탭 관련 context
   * @type {{selectedIndex: number, onSelected: (function(*): void), items: *[], clear: topTabs.clear, setValue: topTabs.setValue}}
   */
  const topTabs = {
    selectedIndex: selectedTopTabIndex,
    onSelected: (index) => setSelectedTopTabIndex(index),
    items: topTabItems,
    clear: () => {
      setTopTabItems(init.topTabItems);
      setSelectedTopTabIndex(init.selectedTopTabIndex);
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
    title: leftSidebarTitle,
    setItems: (value) => {
      setLeftSidebarItems(value);
    },
    setEmptyPlaceholder: (value) => {
      setLeftSidebarEmptyPlaceholder(value);
    },
    setTitle: (value) => {
      setLeftSidebarTitle(value);
    },
    clear: () => {
      setLeftSidebarItems(init.leftSidebarItems);
      setLeftSidebarEmptyPlaceholder(init.leftSidebarEmptyPlaceholder);
      setLeftSidebarTitle(init.leftSidebarTitle);
    },
  };

  /**
   * 우측 sidebar 관련 context
   * @type {{}}
   */
  const rightSidebar = {
    title: rightSidebarTitle,
    clear: () => {
      setRightSidebarTitle(init.rightSidebarTitle);
    },
  };

  /**
   * current gameRoom
   */
  const currentGame = {
    gameRoomId: currentGameRoomId,
    gameRoomEntranceId: currentGameRoomEntranceId,
    enteredUsers: currentGameRoomEnteredUsers,
    setGameRoomInfo: ({ gameRoomId, gameRoomEntranceId, enteredUsers }) => {
      setCurrentGameRoomId(gameRoomId);
      setCurrentGameRoomEntranceId(gameRoomEntranceId);
      setCurrentGameRoomEnteredUsers(enteredUsers);
    },
    clear: () => {
      setCurrentGameRoomId(init.currentGameRoomId);
      setCurrentGameRoomEntranceId(init.currentGameRoomEntranceId);
      setCurrentGameRoomEnteredUsers(init.currentGameRoomEnteredUsers);
    },
  };

  const utils = {
    redirectTo: (url) => {
      navigate(url, { replace: true });
    },
  };

  useEffect(() => {
    /**
     * authentication 먼저 체크
     */
    api.get(auth.authentication)
      .then(success => {

      })
      .catch((error) => {
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
                isAuthenticated: Boolean,
                fingerprint: String,
              }
            ) => {
              localStorage.setItem("fingerprint", response.fingerprint);
            }
          )
          .catch((error) => {
            console.log(error);
          });
      });
  }, []);

  return (
    <ApplicationContext.Provider
      value={{
        api,
        topTabs,
        leftSidebar,
        rightSidebar,
        currentGame,
        utils,
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
