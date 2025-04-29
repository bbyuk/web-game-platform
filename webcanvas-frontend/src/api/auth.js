import { mockAuthData } from '@/api/mocks/auth.js';

const BASE = "/auth.js";

export const auth = {
  // 로그인
  login: {
    url: `${BASE}/login`,
    mock: mockAuthData.loginResult
  },
  // 토큰 refresh
  refresh: {
    url: `${BASE}/refresh`,
    mock: mockAuthData.tokenRefreshResult
  },
};
