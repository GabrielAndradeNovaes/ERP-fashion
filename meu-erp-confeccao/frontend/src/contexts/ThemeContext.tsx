/* eslint-disable react-refresh/only-export-components */
import React, { createContext, useContext, useState, useEffect } from 'react';
import { ThemeProvider as MUIThemeProvider } from '@mui/material/styles';
import { getTheme } from '../theme/theme';

type ThemeMode = 'light' | 'dark' | 'warm' | 'ocean' | 'nature' | 'sunset' | 'lavender' | 'monochrome';

interface ThemeContextData {
  mode: ThemeMode;
  setMode: (mode: ThemeMode) => void;
  toggleTheme: () => void;
}

const ThemeContext = createContext<ThemeContextData>({} as ThemeContextData);

export const useThemeContext = () => useContext(ThemeContext);

export const ThemeProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const [mode, setMode] = useState<ThemeMode>(() => {
    const saved = localStorage.getItem('@FashionERP:theme') as ThemeMode;
    const validModes: ThemeMode[] = ['light', 'dark', 'warm', 'ocean', 'nature', 'sunset', 'lavender', 'monochrome'];
    if (validModes.includes(saved)) return saved;
    return 'dark'; // Default to dark premium
  });

  useEffect(() => {
    localStorage.setItem('@FashionERP:theme', mode);
    document.documentElement.setAttribute('data-theme', mode);
  }, [mode]);

  const toggleTheme = () => {
    setMode(prev => {
      const validModes: ThemeMode[] = ['light', 'dark', 'warm', 'ocean', 'nature', 'sunset', 'lavender', 'monochrome'];
      const nextIndex = (validModes.indexOf(prev) + 1) % validModes.length;
      return validModes[nextIndex];
    });
  };

  return (
    <ThemeContext.Provider value={{ mode, setMode, toggleTheme }}>
      <MUIThemeProvider theme={getTheme(mode)}>
        {children}
      </MUIThemeProvider>
    </ThemeContext.Provider>
  );
};
