import { useNavigate } from "react-router-dom";
import { useEffect } from "react";
import { useAuthentication } from '@/contexts/authentication/index.jsx';

export default function LandingPage() {
  const navigate = useNavigate();
  const { isAuthenticated } = useAuthentication();
  useEffect(() => {
    console.log(isAuthenticated);
    if (isAuthenticated) {
      navigate("/platform", { replace: true });
    }
  }, [isAuthenticated]);
  return <></>;
}
