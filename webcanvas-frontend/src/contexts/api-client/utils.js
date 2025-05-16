export const buildUrlWithParams = (url, params = {}) => {
  const query = new URLSearchParams(params).toString();
  return query ? `${url}?${query}` : url;
}
