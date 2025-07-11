// components/layouts/center-panel/main-panel/index.jsx

const MainPanel = ({ children, className = "" }) => {
  return (
    <div
      className={
        // 1) flex-col으로 세로 정렬, 2) flex-1/h-full로 부모 높이 꽉 채우기
        `flex flex-col flex-1 h-full bg-gray-800 relative ` +
        className
      }
    >
      <div
        className="
          p-4
          flex flex-col    /* 툴바와 캔버스를 세로로 쌓기 */
          flex-1           /* 남은 높이 모두 채우기 */
          overflow-auto    /* 내용이 넘치면 스크롤 */
        "
      >
        {children}
      </div>
    </div>
  );
};

export default MainPanel;
