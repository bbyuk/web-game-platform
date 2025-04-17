export default function Layout() {
  return (
    <div className="flex flex-col h-screen bg-gray-800 text-gray-100">
      {/* 메인 영역 */}
      <div className="flex flex-1 overflow-hidden">

        {/* 왼쪽 사이드바 */}
        <div className="w-64 bg-gray-900 flex flex-col border-r border-gray-700">
          {/* 프로젝트 탭 */}
          <div className="h-12 flex items-center px-4 border-b border-gray-700">
            <span className="text-sm font-semibold">방 목록</span>
          </div>
          {/* 방 리스트 */}
          <div className="flex-1 overflow-y-auto p-2 space-y-2">
            {["방1", "방2", "방3", "방4"].map((room, idx) => (
              <div key={idx} className="flex items-center p-2 rounded hover:bg-gray-700 cursor-pointer">
                <span className="material-icons mr-2 text-gray-400 text-base">meeting_room</span>
                <span>{room}</span>
              </div>
            ))}
          </div>
        </div>

        {/* 중앙 코드 영역 */}
        <div className="flex-1 flex flex-col bg-gray-800">
          {/* 파일 탭 */}
          <div className="h-10 flex items-center space-x-2 px-4 border-b border-gray-700 bg-gray-900">
            <div className="flex items-center bg-gray-800 rounded-t px-3 py-1 shadow-sm">
              <span className="material-icons mr-2 text-gray-400 text-sm">description</span>
              <span className="text-sm">canvas.js</span>
            </div>
          </div>

          {/* 코드 캔버스 */}
          <div className="flex-1 p-4 overflow-auto">
            <div className="w-full h-full bg-gray-100 rounded-lg p-6">
              {/* 캔버스나 코드 에디터 */}
              <p className="text-gray-800">// 여기에 캔버스 그리기</p>
            </div>
          </div>
        </div>

      </div>

      {/* 하단 터미널 영역 */}
      <div className="h-48 bg-gray-900 flex flex-col border-t border-gray-700">
        {/* 터미널 탭 */}
        <div className="h-10 flex items-center px-4 border-b border-gray-700">
          <div className="flex items-center bg-gray-800 rounded-t px-3 py-1 shadow-sm">
            <span className="material-icons mr-2 text-gray-400 text-sm">chat</span>
            <span className="text-sm">채팅</span>
          </div>
        </div>
        {/* 채팅 영역 */}
        <div className="flex-1 overflow-y-auto p-4 text-sm">
          <div className="mb-2 text-gray-300">[User1]: 안녕하세요!</div>
          <div className="mb-2 text-gray-300">[User2]: 반갑습니다 :)</div>
        </div>
      </div>
    </div>
  );
}
