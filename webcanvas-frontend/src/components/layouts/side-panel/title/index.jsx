const Title = ({ label, icon }) => {
  return (
    <div className="text-lg font-bold mb-6 flex items-center space-x-2">
      {icon}
      <span>{label}</span>
    </div>
  );
};

export default Title;
