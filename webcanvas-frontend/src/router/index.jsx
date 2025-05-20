import MainLayout from "@/components/layouts/index.jsx";
import LandingPage from "@/pages/landing/index.jsx";
import GameRoomPage from "@/pages/game-room/waiting/index.jsx";
import LobbyPage from "@/pages/lobby/index.jsx";

export const pages = {
  landing: {
    path: "/",
    url: "/"
  },
  lobby: {
    path: "/platform",
    url: "/platform"
  },
  gameRoom: {
    waiting: {
      path: "/platform/game/room/:roomId/waiting",
      url: (gameRoomId) => `/platform/game/room/${gameRoomId}/waiting`
    },
    playing: {
      path: "/platform/game/room/:roomId/playing",
      url: (gameRoomId) => `/platform/game/room/${gameRoomId}/playing`
    }
  }
};

export const publicRoutes = [
  { path: pages.landing.path, element: <LandingPage /> },
  { path: "*", element: <LandingPage /> },
];

export const privateRoutes = [
  {
    path: pages.lobby.path,
    element: <MainLayout />,
    children: [
      { path: pages.lobby.path, element: <LobbyPage /> },
      { path: pages.gameRoom.waiting.path, element: <GameRoomPage /> },
      { path: pages.gameRoom.playing.path, element: <></>}
    ],
  },
];
