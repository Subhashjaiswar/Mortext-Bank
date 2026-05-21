import React, { createContext, useContext, useState, useCallback } from 'react';
import { AlertCircle, CheckCircle, Info, X, AlertTriangle } from 'lucide-react';
import './Toast.css';

const ToastContext = createContext(null);

export const useToast = () => {
  const context = useContext(ToastContext);
  if (!context) {
    throw new Error('useToast must be used within a ToastProvider');
  }
  return context;
};

export const ToastProvider = ({ children }) => {
  const [toasts, setToasts] = useState([]);

  const addToast = useCallback((message, type = 'info', duration = 4000) => {
    const id = Date.now();
    setToasts((prevToasts) => [...prevToasts, { id, message, type, duration }]);
    
    setTimeout(() => {
      removeToast(id);
    }, duration);
  }, []);

  const removeToast = useCallback((id) => {
    setToasts((prevToasts) => prevToasts.filter((t) => t.id !== id));
  }, []);

  const success = useCallback((msg, dur) => addToast(msg, 'success', dur), [addToast]);
  const error = useCallback((msg, dur) => addToast(msg, 'error', dur), [addToast]);
  const warning = useCallback((msg, dur) => addToast(msg, 'warning', dur), [addToast]);
  const info = useCallback((msg, dur) => addToast(msg, 'info', dur), [addToast]);

  return (
    <ToastContext.Provider value={{ success, error, warning, info, addToast, removeToast }}>
      {children}
      <div className="toast-container">
        {toasts.map((toast) => (
          <ToastItem
            key={toast.id}
            toast={toast}
            onClose={() => removeToast(toast.id)}
          />
        ))}
      </div>
    </ToastContext.Provider>
  );
};

const ToastItem = ({ toast, onClose }) => {
  const { message, type } = toast;
  
  const getIcon = () => {
    switch (type) {
      case 'success':
        return <CheckCircle size={20} className="toast-icon-svg" />;
      case 'error':
        return <AlertCircle size={20} className="toast-icon-svg" />;
      case 'warning':
        return <AlertTriangle size={20} className="toast-icon-svg" />;
      case 'info':
      default:
        return <Info size={20} className="toast-icon-svg" />;
    }
  };

  return (
    <div className={`toast-item toast-${type} animate-slide-in-right`}>
      <div className="toast-icon-wrapper">
        {getIcon()}
      </div>
      <div className="toast-message">{message}</div>
      <button className="toast-close-btn" onClick={onClose}>
        <X size={16} />
      </button>
    </div>
  );
};
