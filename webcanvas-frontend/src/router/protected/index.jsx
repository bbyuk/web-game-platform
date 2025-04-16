import { useAuthentication } from "@/contexts/authentication/index.jsx";
import { Navigate, Outlet } from "react-router-dom";

export default function ProtectedRoute() {
  const { isAuthenticated, loading } = useAuthentication();

  if (loading) return <div>로딩 중...</div>;
  if (!isAuthenticated) return <Navigate to={"/login"} replace />;

  return <Outlet />;
}
