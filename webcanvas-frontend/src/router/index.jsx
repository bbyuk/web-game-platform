import MainPage from "@/pages/main/index.jsx";
import CanvasTest from "@/pages/test/canvas/index.jsx";
import { Navigate } from "react-router-dom";

export const publicRoutes = [
  { path: "/", element: <MainPage /> },
  { path: "*", element: <Navigate to="/" replace /> },
];

export const privateRoutes = [
  { path: "/test", element: <CanvasTest /> },
];
