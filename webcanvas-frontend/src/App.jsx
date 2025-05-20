import { Route, Routes } from "react-router-dom";
import { privateRoutes, publicRoutes } from "@/router";
import ProtectedRoute from "@/router/protected/index.jsx";
import { useEffect } from "react";
import { useAuthentication } from "@/contexts/authentication/index.jsx";

function App() {
  const routeMapping = (route) => {
    if (route.children) {
      return (
        <Route key={route.path} path={route.path} element={route.element}>
          {route.children?.map(routeMapping)}
        </Route>
      );
    }

    return <Route key={route.path} path={route.path} element={route.element} />;
  };
  const { isAuthenticated } = useAuthentication();

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
      {isAuthenticated ? (
        <Route path={"/"} element={<ProtectedRoute />}>
          {privateRoutes.map(routeMapping)}
        </Route>
      ) : (
        publicRoutes.map(routeMapping)
      )}
    </Routes>
  );
}

export default App;
