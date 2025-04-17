const FileTab = () => {
  return (
    <div className="h-10 flex items-center space-x-2 px-4 border-b border-gray-700 bg-gray-900">
      <div className="flex items-center bg-gray-800 rounded-t px-3 py-1 shadow-sm">
        <span className="material-icons mr-2 text-gray-400 text-sm">description</span>
        <span className="text-sm">canvas.js</span>
      </div>
    </div>
  );
};

const CanvasContainer = ({ children }) => {
  return (
    <div className="flex-1 p-4 overflow-auto">
      <div className="w-full h-full bg-gray-100 rounded-lg p-6">{children}</div>
    </div>
  );
};

export default function EditorArea() {
  return (
    <div className="flex-1 flex flex-col bg-gray-800">
      {/* 파일 탭 */}
      <FileTab />
      {/* 코드 캔버스 */}
      <CanvasContainer></CanvasContainer>
    </div>
  );
}
