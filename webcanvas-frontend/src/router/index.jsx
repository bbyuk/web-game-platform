import MainPage from '@/pages/main/index.jsx';
import CanvasTest from '@/pages/test/canvas/index.jsx';
import { Navigate } from 'react-router-dom';

export const routes = [
  { path: '/', element: <MainPage /> },
  { path: '/test', element: <CanvasTest /> },
  { path: '*', element: <Navigate to="/" replace /> }
];
