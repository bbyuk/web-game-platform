const Container = ({ children }) => {
  return <div className="flex border-b border-gray-700 bg-gray-800">{children}</div>;
};

const Tab = ({ label, selected = false, onClick }) => {
  return (
    <div
      onClick={onClick}
      className={
        selected
          ? "px-4 py-2 bg-gray-900 text-white border-r border-gray-700"
          : "px-4 py-2 bg-gray-800 text-gray-400 hover:text-white hover:bg-gray-700 border-r border-gray-700 cursor-pointer"
      }
    >
      <div
        className={`w-3 h-3 ${label === "black" ? "bg-black" : `bg-${label}-400`} rounded-full inline-block mr-2`}
      />
      {label}
    </div>
  );
};

const TopTabs = ({ tabs, selectedIndex, onSelected }) => {
  return (
    <Container>
      {tabs.map((tab, index) => (
        <Tab
          key={`file-tab-${index}`}
          description={tab.description}
          label={tab.label}
          selected={index === selectedIndex}
          onClick={() => onSelected(index)}
        />
      ))}
    </Container>
  );
};

export default TopTabs;
