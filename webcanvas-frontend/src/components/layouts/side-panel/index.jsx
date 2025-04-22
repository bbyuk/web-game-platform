import Title from "@/components/layouts/side-panel/title/index.jsx";

export default function SidePanel({
  children,
  title = { label: String(), icon: JSX },
  left = Boolean(false),
  right = Boolean(false),
}) {
  return (
    <div
      className={`w-60 bg-gray-900 ${left ? "border-r" : right ? "border-l" : ""} border-gray-700 p-4`}
    >
      <Title label={title.label} icon={title.icon} />
      {children}
    </div>
  );
}
