import { GitCommit } from "lucide-react";

export default function LeftSidebar({ list }) {
  return (
    <div className="w-60 bg-gray-900 border-r border-gray-700 p-2">
      <div className="text-lg font-bold mb-4">
        <GitCommit size={20} className="text-gray-400 inline-block" /> main
      </div>
      <ul className="space-y-2">
        {list.map((el, index) => (
          <li className="hover:bg-gray-700 p-2 rounded cursor-pointer" key={`left-el-${index}`}>
            {el.label}
          </li>
        ))}
      </ul>
    </div>
  );
}
