import { mockUserData } from "@/api/mocks/user.js";

const BASE = "/user";

export const user = {
  // 유저 등록
  registerUser: `${BASE}`,
  findUserState: `${BASE}/state`,
};
