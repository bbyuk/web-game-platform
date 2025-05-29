import MainLayout from "@/components/layouts/index.jsx";
import LandingPage from "@/pages/landing/index.jsx";
import GameRoomWaitingPage from "@/pages/game-room/waiting/index.jsx";
import LobbyPage from "@/pages/lobby/index.jsx";
import GameRoomPage from "@/pages/game-room/index.jsx";
import GameRoomPlayingPage from "@/pages/game-room/playing/index.jsx";

export const pages = {
  landing: {
    path: "/",
    url: "/",
  },
  lobby: {
    path: "lobby",
    url: "/lobby",
  },
  gameRoom: {
    path: "game/room/:roomId",
    url: (gameRoomId) => `/game/room/${gameRoomId}`,
    waiting: {
      path: "waiting",
      url: (gameRoomId) => `/game/room/${gameRoomId}/waiting`,
    },
    playing: {
      path: "session::sessionId",
      url: (gameRoomId, gameSessionId) => `/game/room/${gameRoomId}/session/${gameSessionId}`,
    },
  },
};

export const publicRoutes = [
  { path: pages.landing.path, element: <LandingPage /> },
  { path: "*", element: <LandingPage /> },
];

export const privateRoutes = [
  {
    path: pages.landing.path,
    element: <MainLayout />,
    children: [
      { path: pages.lobby.path, element: <LobbyPage /> },
      {
        path: pages.gameRoom.path,
        element: <GameRoomPage />,
        children: [
          { path: pages.gameRoom.waiting.path, element: <GameRoomWaitingPage /> },
          { path: pages.gameRoom.playing.path, element: <GameRoomPlayingPage /> },
        ],
      },
    ],
  },
];
