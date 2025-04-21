import { GitCommit, ChevronRight } from "lucide-react";

export default function LeftSidebar() {
  const isInRoom = false; // 방에 입장했는지 여부

  const roomList = [
    { label: "ABCD123456", current: 2, capacity: 5 },
    { label: "WXYZ987654", current: 1, capacity: 4 },
    { label: "QWER112233", current: 5, capacity: 5 },
  ];

  const userList = [
    { label: "Alice", color: "#FF5733" },
    { label: "Bob", color: "#33A1FF" },
    { label: "Charlie", color: "#8D33FF" },
  ];

  const list = isInRoom ? userList : roomList;

  return (
    <div className="w-60 bg-gray-900 border-r border-gray-700 p-4">
      <div className="text-lg font-bold mb-6 flex items-center space-x-2">
        <GitCommit size={20} className="text-gray-400" />
        <span>main</span>
      </div>

      <ul className="space-y-2">
        {list.map((el, index) => (
          <li
            className="flex items-start space-x-2 hover:bg-gray-700 p-2 rounded cursor-pointer"
            key={`left-el-${index}`}
          >
            <ChevronRight size={16} className="text-gray-400 shrink-0 mt-1" />
            <div className="flex flex-col">
              {isInRoom ? (
                <span className="truncate" style={{ color: el.color }}>
                  {el.label}
                </span>
              ) : (
                <>
                  <span className="text-gray-300 font-semibold truncate">{el.label}</span>
                  <span className="text-gray-400 text-xs">
                    {el.current} / {el.capacity}
                  </span>
                </>
              )}
            </div>
          </li>
        ))}
      </ul>
    </div>
  );
}
