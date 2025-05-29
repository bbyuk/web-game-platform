import { Outlet, useNavigate } from "react-router-dom";
import { useAuthentication } from "@/contexts/authentication/index.jsx";
import { pages } from "@/router/index.jsx";
import { useEffect } from "react";
import { useUserStore } from "@/stores/user/userStore.jsx";
import { getApiClient } from "@/client/http/index.jsx";
import { game, user } from "@/api/index.js";

export default function ProtectedRoute() {
  const { isAuthenticated } = useAuthentication();
  const navigate = useNavigate();
  const { userState, setUserState } = useUserStore();
  const apiClient = getApiClient();

  useEffect(() => {
    if (!isAuthenticated) {
      navigate(pages.landing.url, { replace: true });
      return;
    }
    if (!userState) {
      apiClient.get(user.findUserState).then((response) => {
        setUserState(response.state);

        if (response.state === "IN_LOBBY") {
          navigate(pages.lobby.url, { replace: true });
          return;
        } else if (response.state === "IN_ROOM" || response.state === "IN_GAME") {
          navigate(pages.gameRoom.waiting.url("temp"), {
            state: response,
            replace: true,
          });
          return;
        } else {
          alert("유저 상태를 찾지 못했습니다.");
        }
      });
    }
  }, [isAuthenticated]);

  return <Outlet />;
}
