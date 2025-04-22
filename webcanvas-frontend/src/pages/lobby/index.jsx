import { LobbyPlaceholder } from "@/components/lobby-placeholder/index.jsx";
import { useEffect } from "react";

export default function LobbyPage() {
  useEffect(() => {
    console.log("lobby page loaded");
  }, []);

  return (
    <>
      <LobbyPlaceholder className={"w-full h-full"} />
    </>
  );
}
