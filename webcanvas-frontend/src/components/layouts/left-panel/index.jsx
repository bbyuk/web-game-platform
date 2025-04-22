import Title from "@/components/layouts/left-panel/title";
import ItemList from "@/components/layouts/left-panel/item-list/index.jsx";

export default function LeftPanel({
  gameRoomEntered = false,
  itemList = [],
  title = {
    label: String(),
    icon: JSX,
  },
}) {
  return (
    <div className="w-60 bg-gray-900 border-r border-gray-700 p-4">
      <Title label={title.label} icon={title.icon} />

      <ItemList value={itemList} gameRoomEntered={gameRoomEntered} />
    </div>
  );
}
