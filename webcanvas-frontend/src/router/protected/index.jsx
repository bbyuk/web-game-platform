import { Navigate, Outlet } from "react-router-dom";
import { useAuthentication } from '@/contexts/authentication/index.jsx';

export default function ProtectedRoute() {
  const { isAuthenticated } = useAuthentication();

  if (!isAuthenticated) return <Navigate to={"/"} replace />;

  return <Outlet />;
}
