import {auth} from "@/api/index.js";
import {defaultHeaders, serverDomain} from "@/hooks/api-client/constnats.js";
import {buildUrlWithParams} from "@/hooks/api-client/utils.js";
import {ERROR_CODE_MAPPING} from "@/constants/server-code.js";
import {toast} from "react-toastify";

export const useApiClient = () => {

  /**
   * apiClient 내부 요청 함수
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
      ...defaultHeaders,
      ...options.headers,
    };

    const processedUrl = `${serverDomain}${method === "GET" || method === "DELETE" ? buildUrlWithParams(url, data) : url}`;
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
            throw new Error(error.code);
          }
          /**
           * 토큰 만료시
           * 1. 토큰 refresh 요청
           * 2. API 재요청
           */

          /**
           * 토큰 refresh
           */
          const { isAuthenticated } = await tokenRefresh();

          if (!isAuthenticated) {
            /**
             * 인증 실패 -> 토큰 만료시 refresh 시도 -> refresh마저 실패시 unauthorized handling
             */
            throw new Error(error.code);
          }

          /**
           * API 재요청
           */
          return request(method, url, data, options);
        } else {
          handleErrorMessage(error);
          throw error;
        }
      });
  };

  /**
   * API 요청 후 에러 발생 시 클라이언트에 노출되는 메세지를 처리한다.
   *
   * TODO alert modal로 변경
   * TODO 코드별로 toast 노출할지, modal alert 노출할지 매핑 처리 필요
   */
  const handleErrorMessage = (error) => {
    // 매핑 등록하지 않을 시 서버에서 응답해주는 메세지 alert를 기본으로 한다.
    const errorCodeMapping = ERROR_CODE_MAPPING[error.code];

    if (!errorCodeMapping) {
      alert(error.message);
      return;
    }

    const message = errorCodeMapping.message.override
      ? errorCodeMapping.message.value
      : error.message;

    if (errorCodeMapping.alert) {
      alert(message);
    } else if (errorCodeMapping.toast) {
      // console.log("토스트 작업 TODO ====== ", message);
      toast.error(message);
    }
  };

  /**
   * apiClient 내부 함수 - 토큰 만료시 토큰 리프레쉬 요청
   * @returns {Promise<Response>}
   */
  const tokenRefresh = async () => {
    const processedUrl = `${serverDomain}${auth.refresh}`;
    const options = {
      credentials: "include",
    };
    const fetchOption = {
      method: "POST",
      ...options,
      headers: defaultHeaders,
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
  };

  /**
   * api 요청 클라이언트
   * @type {{get: (function(*, {}=, {}=): Promise<*>), post: (function(*, {}=, {}=): Promise<*>), tokenRefresh: (function(): Promise<*>), constants: {serverDomain: any, defaultHeaders: {'Content-Type': string}}, utils.js: {buildUrlWithParams: (function(*, {}=): string|*)}}}
   */
  const apiClient = {
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
  };

  return apiClient;
};
