import MainLayout from "@/components/layouts/index.jsx";
import { Navigate } from "react-router-dom";
import LandingPage from "@/pages/landing/index.jsx";
import GameRoomPage from "@/pages/game-room/index.jsx";
import LobbyPage from "@/pages/lobby/index.jsx";

export const routeMap = {
  landing: "/",
  lobby: "/platform",
  gameRoom: "/platform/game/room",
};

export const publicRoutes = [
  { path: routeMap.landing, element: <LandingPage /> },
];

export const privateRoutes = [
  {
    path: routeMap.lobby,
    element: <MainLayout />,
    children: [
      { path: routeMap.lobby, element: <LobbyPage /> },
      { path: `${routeMap.gameRoom}/:roomId`, element: <GameRoomPage /> },
    ],
  },
];
