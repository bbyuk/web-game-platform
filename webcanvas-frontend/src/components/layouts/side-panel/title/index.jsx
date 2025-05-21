import { useLeftSideStore } from "@/stores/layout/leftSideStore.jsx";
import { useRightSideStore } from "@/stores/layout/rightSideStore.jsx";

const SidePanelTitle = ({ position }) => {
  const { label, icon, button, onClick } =
    position === "left"
      ? useLeftSideStore((s) => s.title)
      : position === "right"
        ? useRightSideStore((s) => s.title)
        : null;

  return (
    <div className={`text-lg font-bold mb-6 flex items-center space-x-2`}>
      <span
        className={`flex items-center space-x-2 ${button ? "cursor-pointer" : ""}`}
        onClick={onClick}
      >
        <span>{icon}</span>
        <span>{label}</span>
      </span>
    </div>
  );
};

export default SidePanelTitle;
