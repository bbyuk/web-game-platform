import { StrictMode } from "react";
import { createRoot } from "react-dom/client";
import "./index.css";
import App from "./App.jsx";
import { BrowserRouter } from "react-router-dom";
import { ApplicationContextProvider } from "@/contexts/index.jsx";
import { AuthenticationProvider } from "@/contexts/authentication/index.jsx";
import { ApiClientProvider } from "@/contexts/api-client/index.jsx";

createRoot(document.getElementById("root")).render(
  // <StrictMode>
  <BrowserRouter>
    <ApplicationContextProvider>
      <AuthenticationProvider>
        <ApiClientProvider>
          <App />
        </ApiClientProvider>
      </AuthenticationProvider>
    </ApplicationContextProvider>
  </BrowserRouter>
  // </StrictMode>
);
