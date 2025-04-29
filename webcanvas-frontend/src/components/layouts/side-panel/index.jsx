import Title from "@/components/layouts/side-panel/title/index.jsx";
import { JSX } from "react";
import { useApplicationContext } from "@/contexts/index.jsx";
import ItemList from "@/components/layouts/side-panel/item-list/index.jsx";

export default function SidePanel({
  title = { label: String(), icon: JSX.Element },
  left = Boolean(false),
  right = Boolean(false),
}) {
  const { leftSidebar } = useApplicationContext();

  return (
    <div
      className={`w-60 bg-gray-900 ${left ? "border-r" : right ? "border-l" : ""} border-gray-700 p-4`}
    >
      <Title label={title.label} icon={title.icon} />
      {left ? <ItemList value={leftSidebar.items} emptyPlaceholder={leftSidebar.emptyPlaceholder}/> : null}
    </div>
  );
}
