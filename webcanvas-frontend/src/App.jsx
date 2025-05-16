import { Route, Routes } from "react-router-dom";
import { privateRoutes, publicRoutes } from "@/router";
import ProtectedRoute from "@/router/protected/index.jsx";
import {useEffect} from "react";

function App() {
  const routeMapping = (route) => {
    if (route.children) {
      return (
        <Route key={route.path} path={route.path} element={route.element}>
          {route.children.map((child) => (
            <Route key={child.path} path={child.path} element={child.element} />
          ))}
        </Route>
      );
    }
    return <Route key={route.path} path={route.path} element={route.element} />;
  };

  useEffect(() => {
    const handleBeforeUnload = (event) => {
      event.preventDefault();
      event.returnValue = "";
    };

    window.addEventListener("beforeunload", handleBeforeUnload);

    return () => {
      window.removeEventListener("beforeunload", handleBeforeUnload);
    };
  }, []);

  return (
    <Routes>
      {publicRoutes.map(routeMapping)}
      <Route element={<ProtectedRoute />}>{privateRoutes.map(routeMapping)}</Route>
    </Routes>
  );
}

export default App;
