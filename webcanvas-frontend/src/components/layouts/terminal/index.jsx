export default function Terminal() {
  return (
    <div className="h-48 bg-gray-900 flex flex-col border-t border-gray-700">
      {/* 터미널 탭 */}
      <div className="h-10 flex items-center px-4 border-b border-gray-700">
        <div className="flex items-center bg-gray-800 rounded-t px-3 py-1 shadow-sm">
          <span className="material-icons mr-2 text-gray-400 text-sm">Terminal :</span>
          <span className="text-sm">방 입장코드 영역</span>
        </div>
      </div>
      {/* 채팅 영역 */}
      <div className="flex-1 overflow-y-auto p-4 text-sm">
        <div className="mb-2 text-gray-300">[User1]: 안녕하세요!</div>
        <div className="mb-2 text-gray-300">[User2]: 반갑습니다 :)</div>
      </div>
    </div>
  );
}
