import { ChevronRight } from "lucide-react";
import { useEffect } from 'react';

const Container = ({ children }) => {
  return <ul className="space-y-2">{children}</ul>;
};

const Item = ({ label, color, current = -1, capacity = -1, isButton = false }) => {
  return (
    <li
      className={`flex items-start space-x-2 p-2 rounded ${isButton ? "hover:bg-gray-700 cursor-pointer" : ""}`}
    >
      <ChevronRight size={16} className="text-gray-400 shrink-0 mt-1" />
      <div className="flex flex-col">
        {/* 아이템  컬러 */}
        {color ? (
          <span className="truncate" style={{ color: color }}>
            {label}
          </span>
        ) : (
          <span className="text-gray-300 font-semibold truncate">{label}</span>
        )}
        {/* 인원 display */}
        {current > 0 && capacity > 0 && current <= capacity ? (
          <span className={`text-xs ${current === capacity ? "text-red-400" : "text-gray-400"}`}>
            {current} / {capacity}
          </span>
        ) : null}
      </div>
    </li>
  );
};


const ItemList = ({ value }) => {
  return (
    <Container>
      {value.map((el, index) => (
        <Item
          key={`left-item-${index}`}
          label={el.label}
          color={el.color}
          current={el.current}
          capacity={el.capacity}
          isButton={el.isButton}
        />
      ))}
    </Container>
  );
};

export default ItemList;
