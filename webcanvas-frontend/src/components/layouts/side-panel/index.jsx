import Title from "@/components/layouts/side-panel/title/index.jsx";
import { JSX } from "react";

export default function SidePanel({
  title = { label: String(), icon: JSX.Element, button: false, onClick: () => {} },
  left = Boolean(false),
  right = Boolean(false),
  children,
  footer
}) {
  return (
    <div
      className={`w-60 bg-gray-900 ${left ? "border-r" : right ? "border-l" : ""}border-gray-700 p-4 flex flex-col h-full`}
    >
      <Title label={title.label} icon={title.icon} button={title.button} onClick={title.onClick} />

      <div className={"flex-1 overflow-auto"}>
        {children}
      </div>

      {footer && <div className={"mt-4"}>{footer}</div>}
    </div>
  );
}
