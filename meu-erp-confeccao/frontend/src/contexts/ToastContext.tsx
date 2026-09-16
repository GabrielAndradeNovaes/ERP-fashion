import React, { createContext, useContext, useState, ReactNode, useCallback, useEffect } from 'react';
import { Snackbar, Alert } from '@mui/material';

export type AlertColor = 'success' | 'info' | 'warning' | 'error';

interface ToastContextData {
  showToast: (message: string, severity?: AlertColor) => void;
}

const ToastContext = createContext<ToastContextData>({} as ToastContextData);

export const ToastProvider = ({ children }: { children: ReactNode }) => {
  const [open, setOpen] = useState(false);
  const [message, setMessage] = useState('');
  const [severity, setSeverity] = useState<AlertColor>('info');

  const showToast = useCallback((msg: string, sev: AlertColor = 'info') => {
    setMessage(msg);
    setSeverity(sev);
    setOpen(true);
  }, []);

  const handleClose = (event?: React.SyntheticEvent | Event, reason?: string) => {
    if (reason === 'clickaway') {
      return;
    }
    setOpen(false);
  };

  // Escuta eventos globais de erro disparados pelo Axios ou outras partes fora do React Tree
  useEffect(() => {
    const handleApiError = (event: any) => {
      if (event.detail && typeof event.detail === 'string') {
        showToast(event.detail, 'error');
      }
    };
    
    window.addEventListener('apiError', handleApiError);
    return () => {
      window.removeEventListener('apiError', handleApiError);
    };
  }, [showToast]);

  return (
    <ToastContext.Provider value={{ showToast }}>
      {children}
      <Snackbar 
        open={open} 
        autoHideDuration={4000} 
        onClose={handleClose}
        anchorOrigin={{ vertical: 'top', horizontal: 'right' }}
        sx={{ mt: 6 }}
      >
        <Alert onClose={handleClose} severity={severity} variant="filled" sx={{ width: '100%' }}>
          {message}
        </Alert>
      </Snackbar>
    </ToastContext.Provider>
  );
};

export const useToast = () => {
  const context = useContext(ToastContext);
  if (!context) {
    throw new Error('useToast must be used within a ToastProvider');
  }
  return context;
};
