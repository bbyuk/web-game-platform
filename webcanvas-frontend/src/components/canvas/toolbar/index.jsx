import React from 'react';
import { Eraser, XCircle } from 'lucide-react';

/**
 * CanvasToolbar Component
 * @param {{
 *   tool: 'pen' | 'eraser',
 *   size: number,
 *   color: string,
 *   colors: string[],
 *   onChangeTool: (tool: 'pen' | 'eraser') => void,
 *   onChangeSize: (size: number) => void,
 *   onChangeColor: (color: string) => void,
 *   onClear: () => void,
 * }} props
 */
export default function CanvasToolbar({
                                        tool,
                                        size,
                                        color,
                                        colors = ['#000000', '#FF0000', '#00AA00', '#0000FF', '#FFAA00'],
                                        onChangeTool,
                                        onChangeSize,
                                        onChangeColor,
                                        onClear,
                                      }) {
  return (
    <div className="flex items-center justify-between w-full p-2 bg-gray-800 border-b border-gray-700">
      {/* Left group: Color Pens, Eraser & Brush Size */}
      <div className="flex items-center space-x-2">
        {colors.map((c) => (
          <button
            key={c}
            type="button"
            onClick={() => {
              onChangeColor(c);
              onChangeTool('pen');
            }}
            className={`w-6 h-6 rounded-full border-2 focus:outline-none transition-colors cursor-pointer ${
              color === c ? 'border-white' : 'border-gray-600'
            }`}
            style={{ backgroundColor: c }}
            title={`Color ${c}`}
          />
        ))}
        <button
          type="button"
          onClick={() => onChangeTool('eraser')}
          className={`w-6 h-6 rounded-full border-2 flex items-center justify-center transition-colors focus:outline-none cursor-pointer ${
            tool === 'eraser'
              ? 'bg-indigo-600 border-white text-white'
              : 'bg-gray-700 border-gray-600 text-gray-300 hover:bg-gray-600'
          }`}
          title="지우개"
        >
          <Eraser className="w-4 h-4" />
        </button>
        <div className="flex items-center space-x-1">
          <label htmlFor="brush-size" className="text-sm text-gray-300">
            크기
          </label>
          <input
            id="brush-size"
            type="range"
            min="1"
            max="10"
            value={size}
            onChange={(e) => onChangeSize(Number(e.target.value))}
            className="h-1 w-32 accent-indigo-500 cursor-pointer"
          />
        </div>
      </div>

      {/* Right group: Clear All Button */}
      <button
        type="button"
        onClick={onClear}
        className="p-2 rounded-full bg-red-600 hover:bg-red-500 text-white transition-colors focus:outline-none cursor-pointer"
        title="전체 지우기"
      >
        <XCircle className="w-5 h-5" />
      </button>
    </div>
  );
}
