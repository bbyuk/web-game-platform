import { mockUserData } from '@/api/mocks/user.js';

const BASE = "/user";

export const user = {
  // 유저 등록
  registerUser: {
    url: `${BASE}`,
    mock: mockUserData.registeredUser
  },
};
