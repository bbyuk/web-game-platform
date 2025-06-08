import Index from "@/components/layouts/side-panel/contents/item-list/item/index.jsx";

const ItemList = ({ value, emptyPlaceholder }) => {
  const hasData = value && value.length > 0;
  console.log(value);
  return (
    <ul className="space-y-2">
      {hasData ? (
        value.map((el, index) => (
          <Index
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
    </ul>
  );
};

export default ItemList;
