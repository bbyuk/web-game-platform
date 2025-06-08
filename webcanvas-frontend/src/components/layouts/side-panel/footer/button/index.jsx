export default function SidePanelFooterButton({ status = "not-ready", onClick = () => {} }) {
  const getButtonProps = () => {
    switch (status) {
      case "ready":
        return {
          label: "READY",
          disabled: false,
          className: "bg-green-600 cursor-default",
        };
      case "not-ready":
        return {
          label: "SET READY",
          disabled: false,
          className: "bg-gray-600 hover:bg-gray-500",
        };
      case "not-all-ready":
        return {
          label: "START",
          disabled: true,
          className: "bg-gray-600 cursor-default",
        };
      case "all-ready":
        return {
          label: "START",
          disabled: false,
          className: "bg-indigo-600 hover:bg-indigo-500",
        };
      default:
        return {
          label: "UNKNOWN",
          disabled: true,
          className: "bg-red-600",
        };
    }
  };

  const { label, disabled, className } = getButtonProps();

  return (
    <button
      onClick={onClick}
      disabled={disabled}
      className={`w-full py-2 px-4 rounded-md text-white font-semibold transition ${className} ${disabled ? "cursor-not-allowed" : "hover:cursor-pointer"}`}
    >
      {label}
    </button>
  );
}
