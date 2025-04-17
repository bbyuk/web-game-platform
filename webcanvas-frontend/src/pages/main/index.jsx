import EditorArea from "@/components/layouts/editor-area/index.jsx";
import LNB from "@/components/layouts/lnb/index.jsx";
import Terminal from "@/components/layouts/terminal/index.jsx";

export default function Layout() {
  return (
    <div className="flex flex-col h-screen bg-gray-800 text-gray-100">
      <div className="flex flex-1 overflow-hidden">
        <LNB />
        <EditorArea />
      </div>
      <Terminal />
    </div>
  );
}
