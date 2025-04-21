const MainPanel = ({ children }) => {
  return (
    <div className="flex-1 justify-center items-center bg-gray-800 relative">
      <div className="p-4 flex justify-center items-center w-full h-full">{children}</div>
    </div>
  );
};

export default MainPanel;
