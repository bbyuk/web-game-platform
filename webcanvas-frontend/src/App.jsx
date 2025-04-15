import { Route, Routes } from "react-router-dom";
import { publicRoutes, privateRoutes } from "@/router";
import ProtectedRoute from "@/router/protected/index.jsx";
import { useEffect } from 'react';
import { useAuth } from '@/contexts/auth/index.jsx';
import { post } from '@/utils/request.js';
import { auth } from '@/api/index.js';

function App() {
  const routeMapping = ({ path, element }) => <Route key={path} path={path} element={element} />;
  const { isAuthenticated } = useAuth();

  useEffect(() => {
    if (!isAuthenticated) {
      /**
       * 앱 진입 후 authenticated 상태가 아니면 자동 로그인 요청
       */
      const fingerprint = localStorage.getItem("fingerprint");

      const callLoginApi = async () => {
        const response = await post(auth.login, {
          fingerprint: fingerprint
        });

        alert(JSON.stringify(response));
      };

      callLoginApi();
    }
  }, [isAuthenticated]);

  return (
    <Routes>
      {publicRoutes.map(routeMapping)}
      <Route element={<ProtectedRoute />}>{privateRoutes.map(routeMapping)}</Route>
    </Routes>
  );
}

export default App;
