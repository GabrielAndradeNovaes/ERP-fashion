import React, { createContext, useContext, useState, useEffect } from 'react';
import { ThemeProvider as MUIThemeProvider } from '@mui/material/styles';
import { getTheme } from '../theme/theme';

type ThemeMode = 'light' | 'dark' | 'warm';

interface ThemeContextData {
  mode: ThemeMode;
  toggleTheme: () => void;
}

const ThemeContext = createContext<ThemeContextData>({} as ThemeContextData);

export const useThemeContext = () => useContext(ThemeContext);

export const ThemeProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const [mode, setMode] = useState<ThemeMode>(() => {
    const saved = localStorage.getItem('@FashionERP:theme');
    if (saved === 'light' || saved === 'dark' || saved === 'warm') return saved as ThemeMode;
    return 'dark'; // Default to dark premium
  });

  useEffect(() => {
    localStorage.setItem('@FashionERP:theme', mode);
    document.documentElement.setAttribute('data-theme', mode);
  }, [mode]);

  const toggleTheme = () => {
    setMode(prev => {
      if (prev === 'light') return 'dark';
      if (prev === 'dark') return 'warm';
      return 'light';
    });
  };

  return (
    <ThemeContext.Provider value={{ mode, toggleTheme }}>
      <MUIThemeProvider theme={getTheme(mode)}>
        {children}
      </MUIThemeProvider>
    </ThemeContext.Provider>
  );
};
