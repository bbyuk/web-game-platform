import { create } from "zustand";
import { MessageCircle } from "lucide-react";

const init = {
  title: {
    label: "chat",
    icon: <MessageCircle size={20} className="text-gray-400" />,
    button: false,
    onClick: () => {},
  },
  contents: {
    slot: "",
    props: {},
  },
  footer: {
    slot: "",
    props: {},
  },
};

export const useRightSideStore = create((set) => ({
  title: init.title,
  setTitle: (value) => set({ title: value }),
  contents: init.contents,
  setContents: (value) => set({ contents: value }),
  footer: init.footer,
  setFooter: (value) => set({ footer: value }),

  clear: () => set({ title: init.title, contents: init.contents, footer: init.footer }),
}));
