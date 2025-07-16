// ToastProvider.jsx
import React, { createContext, useContext, useState, useCallback } from 'react';
import ReactDOM from 'react-dom';

const ToastContext = createContext(null);

export function ToastProvider({ children }) {
  const [toasts, setToasts] = useState([]);

  const showToast = useCallback((message, duration = 3000) => {
    const id = Date.now();
    setToasts(prev => [...prev, { id, message, duration }]);
    setTimeout(() => setToasts(prev => prev.filter(t => t.id !== id)), duration);
  }, []);

  return (
    <ToastContext.Provider value={showToast}>
      {children}
      {ReactDOM.createPortal(
        <div className="fixed top-4 right-4 flex flex-col gap-2 z-50 pointer-events-none">
          {toasts.map(({ id, message, duration }) => (
            <div
              key={id}
              className="min-w-[200px] max-w-[300px] px-4 py-2 bg-black bg-opacity-80 text-white rounded-lg shadow-lg transform translate-y-5 opacity-0 animate-fade-in-up will-change-transform will-change-opacity"
              style={{ animation: `fade-out 0.3s ${duration - 300}ms forwards ease-in-out` }}
            >
              {message}
            </div>
          ))}
        </div>,
        document.body
      )}
    </ToastContext.Provider>
  );
}

export function useToast() {
  const context = useContext(ToastContext);
  if (!context) throw new Error('useToast must be used within ToastProvider');
  return context;
}
