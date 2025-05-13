import Title from "@/components/layouts/side-panel/title/index.jsx";
import { JSX } from "react";
import ItemList from "@/components/layouts/side-panel/item-list/index.jsx";
import { EMPTY_MESSAGES } from "@/constants/message.js";

export default function SidePanel({
  title = { label: String(), icon: JSX.Element, button: false, onClick: () => {} },
  left = Boolean(false),
  right = Boolean(false),
  children,
}) {
  return (
    <div
      className={`w-60 bg-gray-900 ${left ? "border-r" : right ? "border-l" : ""}border-gray-700 p-4`}
    >
      <Title label={title.label} icon={title.icon} button={title.button} onClick={title.onClick} />
      {children}
    </div>
  );
}
