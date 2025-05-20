export default function SidePanelFooterButton({
  label = "확인",
  onClick = () => {},
  disabled = false,
}) {
  return (
    <button
      onClick={onClick}
      disabled={disabled}
      className={`w-full py-2 px-4 rounded-md text-white font-semibold transition
        ${disabled ? "bg-gray-600 cursor-not-allowed" : "bg-indigo-600 hover:bg-indigo-500"}`}
    >
      {label}
    </button>
  );
}
