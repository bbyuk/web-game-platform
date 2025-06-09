export default function AnswerBoard({ answer }) {
  return (
    <div className="absolute top-8 left-1/2 -translate-x-1/2 z-10">
      <div className="px-4 py-2 bg-gray-800 rounded-xl shadow-md flex items-center gap-2">
        <span className="text-sm text-gray-400">제시어</span>
        <span className="text-lg font-semibold text-green-400 tracking-wide">{answer}</span>
      </div>
    </div>
  );
}
