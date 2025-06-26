import { create } from "zustand";

export const useClientStore = create((set) => ({
  isLoading: false,
  startLoading: () => {
    set({ isLoading: true });
  },
  endLoading: () => {
    set({ isLoading: false });
  },
}));
