const BASE = "/auth";

export const auth = {
  // 로그인
  login: `${BASE}/login`,
  // 토큰 refresh
  refresh: `${BASE}/refresh`,
  // 인증 확인
  authentication: `${BASE}/authentication`
};
