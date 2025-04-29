import { LobbyPlaceholder } from "@/components/lobby-placeholder/index.jsx";
import { useEffect, useState } from "react";
import { useApplicationContext } from "@/contexts/index.jsx";

export default function LobbyPage() {
  const { leftSidebar, mock } = useApplicationContext();

  useEffect(() => {
    // mock 데이터 사용시 mock 데이터 set
    if (mock.use) {
      leftSidebar.setItems(mock.pages.lobby.leftSidebar);
    }
  }, []);

  return (
    <>
      <LobbyPlaceholder className={"w-full h-full"} />
    </>
  );
}
