const Title = ({ label, icon, button, onClick }) => {
  return (
    <div className={`text-lg font-bold mb-6 flex items-center space-x-2`}>
      <span
        className={`flex items-center space-x-2 ${button ? "cursor-pointer" : ""}`}
        onClick={onClick}
      >
        <span>{icon}</span>
        <span>{label}</span>
      </span>
    </div>
  );
};

export default Title;
