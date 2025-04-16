import { StrictMode } from 'react';
import { createRoot } from 'react-dom/client';
import './index.css';
import App from './App.jsx';
import { BrowserRouter } from 'react-router-dom';
import { ApplicationContextProvider } from '@/contexts/application/index.jsx';

createRoot(document.getElementById("root")).render(
  // <StrictMode>
    <BrowserRouter>
      <ApplicationContextProvider>
        <App />
      </ApplicationContextProvider>
    </BrowserRouter>
  // </StrictMode>
);
