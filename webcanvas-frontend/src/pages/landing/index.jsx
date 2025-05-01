import { useNavigate } from "react-router-dom";
import { useEffect } from "react";
import { useApplicationContext } from "@/contexts/index.jsx";

export default function LandingPage() {
  const navigate = useNavigate();
  const { authentication } = useApplicationContext();
  useEffect(() => {
    if (authentication.isAuthenticated) {
      navigate("/platform", { replace: true });
    }
  }, [authentication.isAuthenticated]);
  return <></>;
}
