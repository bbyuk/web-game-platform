import MainLayout from "@/components/layouts/index.jsx";
import LandingPage from "@/pages/landing/index.jsx";
import GameRoomPage from "@/pages/game-room/index.jsx";
import LobbyPage from "@/pages/lobby/index.jsx";
import { useNavigate } from "react-router-dom";

export const pages = {
  landing: "/",
  lobby: "/platform",
  gameRoom: "/platform/game/room",
};

export const publicRoutes = [
  { path: pages.landing, element: <LandingPage /> },
  { path: "*", element: <LandingPage /> }
];

export const privateRoutes = [
  {
    path: pages.lobby,
    element: <MainLayout />,
    children: [
      { path: pages.lobby, element: <LobbyPage /> },
      { path: `${pages.gameRoom}/:roomId`, element: <GameRoomPage /> },
    ],
  },
];
