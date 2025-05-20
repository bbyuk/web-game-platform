import { Navigate, Outlet, useNavigate } from "react-router-dom";
import { useAuthentication } from "@/contexts/authentication/index.jsx";
import { pages } from "@/router/index.jsx";
import { useEffect } from "react";

export default function ProtectedRoute() {
  const { isAuthenticated } = useAuthentication();
  const navigate = useNavigate();

  if (!isAuthenticated) return <Navigate to={pages.landing.url} replace />;

  useEffect(() => {
    navigate(pages.lobby.url, { replace: true });
  }, [isAuthenticated]);

  return <Outlet />;
}
