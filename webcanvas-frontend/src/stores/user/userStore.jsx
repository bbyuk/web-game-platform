import { create } from "zustand";

// IN_LOBBY / IN_ROOM / IN_GAME

export const useUserStore = create((set) => ({
  userState: null,
  setUserState: (value) => {
    set({ userState: value });
  },
}));
