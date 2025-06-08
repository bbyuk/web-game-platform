import { ChevronRight } from "lucide-react";
import { useEffect, useState } from "react";

const Container = ({ children }) => {
  return <ul className="space-y-2">{children}</ul>;
};

const Item = ({
  label,
  color,
  icon,
  current = -1,
  capacity = -1,
  highlight = false,
  theme,
  isButton = false,
  onClick = () => {},
}) => {
  const [bg, setBg] = useState("bg-gray-800");
  const [border, setBorder] = useState("border-gray-600");
  const [text, setText] = useState("text-gray-300");

  useEffect(() => {
    console.log("theme : " + theme);
    console.log("클래스 변경");
    if (theme === "indigo") {
      setBg("bg-indigo-900");
      setBorder("border-indigo-500");
      setText("text-white");
    } else if (theme === "green") {
      setBg("bg-green-900/40");
      setBorder("border-green-500");
      setText("text-white");
    }
  }, [theme]);

  return (
    <li
      className={`flex items-start space-x-2 p-2 rounded
                  ${isButton ? "hover:bg-gray-700 cursor-pointer" : ""}
                   ${highlight ? `${bg} ${border}` : ""}`}
      onClick={isButton ? onClick : null}
    >
      {icon ? icon : <ChevronRight size={16} className="text-gray-400 shrink-0 mt-1" />}
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

const ItemList = ({ value, emptyPlaceholder }) => {
  const hasData = value && value.length > 0;
  console.log(value);
  return (
    <Container>
      {hasData ? (
        value.map((el, index) => (
          <Item
            key={`left-item-${index}`}
            label={el.label}
            color={el.color}
            highlight={el.highlight}
            theme={el.theme}
            current={el.current}
            capacity={el.capacity}
            isButton={el.isButton}
            onClick={el.onClick}
          />
        ))
      ) : (
        <li className="text-sm text-gray-500 px-2 py-4 text-center border border-dashed border-gray-600 rounded">
          {emptyPlaceholder}
        </li>
      )}
    </Container>
  );
};

export default ItemList;
