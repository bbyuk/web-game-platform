import { Loader2 } from "lucide-react";

export default function LoadingOverlay() {
  return (
    <div className="absolute inset-0 z-50 flex flex-col items-center justify-center bg-black/50 backdrop-blur-sm">
      <Loader2 className="w-10 h-10 text-white animate-spin" />
      <p className="mt-3 text-sm text-gray-300">로딩 중입니다...</p>
    </div>
  );
}
