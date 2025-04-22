import MainLayout from "@/components/layouts/index.jsx";
import { Navigate } from "react-router-dom";
import LandingPage from "@/pages/landing/index.jsx";
import GameRoomPage from "@/pages/game-room/index.jsx";
import LobbyPage from "@/pages/lobby/index.jsx";

export const publicRoutes = [
  { path: "/", element: <LandingPage /> },
  { path: "*", element: <Navigate to="/" replace /> },
];

export const privateRoutes = [
  {
    path: "/platform",
    element: <MainLayout />,
    children: [
      { path: "/platform", element: <LobbyPage /> },
      { path: "/platform/game/room/:roomId", element: <GameRoomPage /> },
    ],
  },
  // {
  //   path: "*",
  //   element: <Navigate to={"/platform"} replace />,
  // },
];
