const defaultHeaders = {
    "Content-Type": "application/json"
};

const getToken = () => localStorage.getItem("accessToken");

// 🔁 GET 쿼리 파라미터 붙이기
const buildUrlWithParams = (url, params = {}) => {
    const query = new URLSearchParams(params).toString();
    return query ? `${url}?${query}` : url;
};

const request = async (method, url, data = {}, options = {}) => {
    const token = getToken();

    const headers = {
        ...defaultHeaders,
        ...(token && {Authorization: `Bearer ${token}`}),
        ...options.headers,
    };

    const processedUrl = method === "GET" ? buildUrlWithParams(url, data) : url;

    const fetchOption = {
        method,
        headers,
        ...options,
        ...(method !== "GET" && {body: JSON.stringify(data)})
    };

    return fetch(processedUrl, fetchOption)
        .then(async response => {
            if (!response.ok) {
                const error = await response.json().catch(() => ({}));

                throw {
                    status: response.status,
                    message: response.statusText,
                    ...error
                };
            }

            return response.json()
                .catch(() => {
                    alert("응답 json 가져오는 중 error");
                });
        });
}

export const get = async (url, params = {}, options = {}) => {
    return request("GET", url, params, options);
}

export const post = async (url, data = {}, options = {}) => {
    return request("POST", url, data, options);
}