import { createRoot } from 'react-dom/client';
import './index.css';
import App from './App.jsx';
import { BrowserRouter } from 'react-router-dom';
import { AuthenticationProvider } from '@/contexts/authentication/index.jsx';
import { ToastProvider } from '@/contexts/toast/index.jsx';

createRoot(document.getElementById('root')).render(
  // <StrictMode>
  <BrowserRouter>
    <ToastProvider>
      <AuthenticationProvider>
        <App />
      </AuthenticationProvider>
    </ToastProvider>
  </BrowserRouter>
  // </StrictMode>
);
