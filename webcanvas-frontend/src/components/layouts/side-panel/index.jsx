import SidePanelTitle from "@/components/layouts/side-panel/title/index.jsx";
import SidePanelFooter from "@/components/layouts/side-panel/footer/index.jsx";
import { useLeftSideStore } from "@/stores/layout/leftSideStore.jsx";
import { useRightSideStore } from "@/stores/layout/rightSideStore.jsx";

export default function SidePanel({ position }) {
  const { contents, footer } =
    position === "left" ? useLeftSideStore() : position === "right" ? useRightSideStore() : {};

  const ContentComponent = contents.slot;
  const FooterComponent = footer.slot;
  return (
    <div
      className={`w-60 bg-gray-900 ${position === "left" ? "border-r" : position === "right" ? "border-l" : ""}border-gray-700 p-4 flex flex-col h-full`}
    >
      <SidePanelTitle position={position} />

      <div className={"flex-1 overflow-auto"}>
        {ContentComponent ? <ContentComponent {...contents?.props} /> : null}
      </div>

      <SidePanelFooter>
        {FooterComponent ? <FooterComponent {...footer?.props} /> : null}
      </SidePanelFooter>
    </div>
  );
}
