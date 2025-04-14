import { Route, Routes } from "react-router-dom";
import { publicRoutes, privateRoutes } from "@/router";
import ProtectedRoute from "@/router/protected/index.jsx";

function App() {
  const routeMapping = ({ path, element }) => <Route key={path} path={path} element={element} />;

  return (
    <Routes>
      {publicRoutes.map(routeMapping)}
      <Route element={<ProtectedRoute />}>{privateRoutes.map(routeMapping)}</Route>
    </Routes>
  );
}

export default App;
