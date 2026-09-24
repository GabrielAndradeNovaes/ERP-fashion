import { createTheme } from '@mui/material/styles';

export const getTheme = (mode: 'light' | 'dark' | 'warm' | 'ocean') => {
  // MUI só entende light e dark nativamente, então para o warm e ocean, passamos 'light'
  const muiMode = mode === 'dark' ? 'dark' : 'light';
  const isDark = mode === 'dark';
  const isWarm = mode === 'warm';
  const isOcean = mode === 'ocean';

  return createTheme({
  palette: {
    mode: muiMode,
    primary: {
      main: isDark ? '#6366f1' : (isWarm ? '#d4af37' : (isOcean ? '#0284c7' : '#4f46e5')),
    },
    secondary: {
      main: isWarm ? '#e3a8b4' : (isOcean ? '#06b6d4' : '#ec4899'), 
    },
    background: {
      default: isDark ? '#0b0f19' : (isWarm ? '#fdfbf7' : (isOcean ? '#f0f9ff' : '#f8fafc')),
      paper: isDark ? '#151b2b' : '#ffffff',
    },
    text: {
      primary: isDark ? '#f8fafc' : (isWarm ? '#2d2625' : (isOcean ? '#0c4a6e' : '#0f172a')),
      secondary: isDark ? '#94a3b8' : (isWarm ? '#796d6a' : (isOcean ? '#0369a1' : '#475569')),
    },
    divider: isDark ? 'rgba(255, 255, 255, 0.08)' : (isWarm ? 'rgba(121, 109, 106, 0.15)' : (isOcean ? 'rgba(14, 165, 233, 0.15)' : 'rgba(15, 23, 42, 0.1)')),
  },
  typography: {
    fontFamily: [
      'Inter',
      '-apple-system',
      'BlinkMacSystemFont',
      '"Segoe UI"',
      'Roboto',
      'sans-serif',
    ].join(','),
  },
  components: {
    MuiTextField: {
      styleOverrides: {
        root: {
          '& .MuiOutlinedInput-root': {
            '& fieldset': {
              borderColor: mode === 'dark' ? 'rgba(255, 255, 255, 0.15)' : 'rgba(0, 0, 0, 0.23)',
            },
            '&:hover fieldset': {
              borderColor: mode === 'dark' ? 'rgba(255, 255, 255, 0.3)' : 'rgba(0, 0, 0, 0.5)',
            },
            '&.Mui-focused fieldset': {
              borderColor: mode === 'dark' ? '#6366f1' : '#4f46e5',
            },
          },
        },
      },
    },
    MuiSelect: {
      styleOverrides: {
        root: {
          '& .MuiOutlinedInput-notchedOutline': {
            borderColor: mode === 'dark' ? 'rgba(255, 255, 255, 0.15)' : 'rgba(0, 0, 0, 0.23)',
          },
          '&:hover .MuiOutlinedInput-notchedOutline': {
            borderColor: mode === 'dark' ? 'rgba(255, 255, 255, 0.3)' : 'rgba(0, 0, 0, 0.5)',
          },
          '&.Mui-focused .MuiOutlinedInput-notchedOutline': {
            borderColor: mode === 'dark' ? '#6366f1' : '#4f46e5',
          },
        },
      },
    },
    MuiButton: {
      styleOverrides: {
        root: {
          textTransform: 'none',
          borderRadius: '8px',
        },
      },
    },
    MuiCard: {
      styleOverrides: {
        root: {
          backgroundImage: 'none',
        },
      },
    },
    MuiPaper: {
      styleOverrides: {
        root: {
          backgroundImage: 'none',
        },
      },
    },
  },
});
};
