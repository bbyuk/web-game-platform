import { createRoot } from 'react-dom/client';
import './index.css';
import App from './App.jsx';
import { BrowserRouter } from 'react-router-dom';
import { AuthenticationProvider } from '@/contexts/authentication/index.jsx';

createRoot(document.getElementById('root')).render(
  // <StrictMode>
  <BrowserRouter>
    <AuthenticationProvider>
      <App />
    </AuthenticationProvider>
  </BrowserRouter>
  // </StrictMode>
);
