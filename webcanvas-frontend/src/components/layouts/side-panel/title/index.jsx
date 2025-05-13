const Title = ({ label, icon, button, onClick }) => {
  return (
    <div
      className={`text-lg font-bold mb-6 flex items-center space-x-2 ${button ? "cursor-pointer" : ""}`}
    >
      <span onClick={onClick}>{icon}</span>
      <span>{label}</span>
    </div>
  );
};

export default Title;
