import { createTheme } from '@mui/material/styles';

export const getTheme = (mode: 'light' | 'dark' | 'warm' | 'ocean' | 'nature' | 'sunset' | 'lavender' | 'monochrome' | 'cyberpunk' | 'brutalism') => {
  // MUI só entende light e dark nativamente
  const isDark = mode === 'dark' || mode === 'cyberpunk';
  const muiMode = isDark ? 'dark' : 'light';
  
  const getPrimary = () => {
    switch (mode) {
      case 'dark': return '#6366f1';
      case 'warm': return '#d4af37';
      case 'ocean': return '#0284c7';
      case 'nature': return '#16a34a';
      case 'sunset': return '#ea580c';
      case 'lavender': return '#8b5cf6';
      case 'monochrome': return '#111827';
      case 'cyberpunk': return '#facc15';
      case 'brutalism': return '#000000';
      default: return '#4f46e5';
    }
  };

  const getSecondary = () => {
    switch (mode) {
      case 'warm': return '#e3a8b4';
      case 'ocean': return '#06b6d4';
      case 'nature': return '#84cc16';
      case 'sunset': return '#f43f5e';
      case 'lavender': return '#d946ef';
      case 'monochrome': return '#4b5563';
      case 'cyberpunk': return '#f472b6';
      case 'brutalism': return '#000000';
      default: return '#ec4899';
    }
  };

  const getBgDefault = () => {
    switch (mode) {
      case 'dark': return '#0b0f19';
      case 'warm': return '#fdfbf7';
      case 'ocean': return '#f0f9ff';
      case 'nature': return '#f2fbf5';
      case 'sunset': return '#fffaf5';
      case 'lavender': return '#f5f3ff';
      case 'monochrome': return '#f9fafb';
      case 'cyberpunk': return '#050505';
      case 'brutalism': return '#ffffff';
      default: return '#f8fafc';
    }
  };

  const getBgPaper = () => isDark ? '#151b2b' : '#ffffff';

  const getTextPrimary = () => {
    switch (mode) {
      case 'dark': return '#f8fafc';
      case 'warm': return '#2d2625';
      case 'ocean': return '#0c4a6e';
      case 'nature': return '#14532d';
      case 'sunset': return '#7c2d12';
      case 'lavender': return '#4c1d95';
      case 'monochrome': return '#111827';
      case 'cyberpunk': return '#fcd34d';
      case 'brutalism': return '#000000';
      default: return '#0f172a';
    }
  };

  const getTextSecondary = () => {
    switch (mode) {
      case 'dark': return '#94a3b8';
      case 'warm': return '#796d6a';
      case 'ocean': return '#0369a1';
      case 'nature': return '#166534';
      case 'sunset': return '#9a3412';
      case 'lavender': return '#5b21b6';
      case 'monochrome': return '#374151';
      case 'cyberpunk': return '#f472b6';
      case 'brutalism': return '#000000';
      default: return '#475569';
    }
  };

  return createTheme({
  palette: {
    mode: muiMode,
    primary: { main: getPrimary() },
    secondary: { main: getSecondary() },
    background: { default: getBgDefault(), paper: getBgPaper() },
    text: { primary: getTextPrimary(), secondary: getTextSecondary() },
    divider: isDark ? 'rgba(255, 255, 255, 0.08)' : 'rgba(15, 23, 42, 0.1)',
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
