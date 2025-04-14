import { useAuth } from "@/contexts/auth/index.jsx";

export default function MainPage() {
  const { isAuthenticated, logout } = useAuth();

  return <div className="p-10 text-center text-2xl font-bold text-blue-600">메인 페이지</div>;
}
