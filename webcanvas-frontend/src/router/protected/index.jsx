import { Navigate, Outlet } from "react-router-dom";
import { useApplicationContext } from "@/contexts/application/index.jsx";

export default function ProtectedRoute() {
  const { authentication } = useApplicationContext();

  if (!authentication.isAuthenticated) return <Navigate to={"/"} replace />;

  return <Outlet />;
}
