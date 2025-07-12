import { createTheme } from '@mui/material/styles';
import type { PaletteMode } from '@mui/material';

const darkPalette = {
  mode: 'dark' as PaletteMode,
  primary: {
    main: '#10a37f',
    light: '#4aeab3',
    dark: '#0b7c5c',
    contrastText: '#fff',
  },
  secondary: {
    main: '#40414f',
    light: '#5c5f73',
    dark: '#23232f',
    contrastText: '#fff',
  },
  background: {
    default: '#343541',
    paper: '#444654',
  },
  text: {
    primary: '#f7f7f8',
    secondary: '#b4bcd0',
    disabled: '#7a7b8a',
  },
  divider: 'rgba(255,255,255,0.08)',
  success: { main: '#10a37f' },
  error: { main: '#e53935' },
  warning: { main: '#ffa000' },
  info: { main: '#1976d2' },
};

const lightPalette = {
  mode: 'light' as PaletteMode,
  primary: {
    main: '#10a37f',
    light: '#4aeab3',
    dark: '#0b7c5c',
    contrastText: '#fff',
  },
  secondary: {
    main: '#e5e7eb',
    light: '#f3f4f6',
    dark: '#cbd5e1',
    contrastText: '#222',
  },
  background: {
    default: '#f7f7f8',
    paper: '#fff',
  },
  text: {
    primary: '#222b45',
    secondary: '#6b7280',
    disabled: '#b0b8c1',
  },
  divider: 'rgba(16,163,127,0.08)',
  success: { main: '#10a37f' },
  error: { main: '#e53935' },
  warning: { main: '#ffa000' },
  info: { main: '#1976d2' },
};

export const theme = (mode: PaletteMode = 'dark') => createTheme({
  palette: mode === 'dark' ? darkPalette : lightPalette,
  typography: {
    fontFamily: [
      'Inter',
      '-apple-system',
      'BlinkMacSystemFont',
      'Segoe UI',
      'Roboto',
      'Helvetica Neue',
      'Arial',
      'sans-serif',
    ].join(','),
    h1: {
      fontSize: '2.2rem',
      fontWeight: 700,
      letterSpacing: '-0.02em',
      color: mode === 'dark' ? '#fff' : '#222b45',
    },
    h2: {
      fontSize: '1.7rem',
      fontWeight: 600,
      color: mode === 'dark' ? '#fff' : '#222b45',
    },
    h3: {
      fontSize: '1.3rem',
      fontWeight: 600,
      color: mode === 'dark' ? '#fff' : '#222b45',
    },
    h4: {
      fontSize: '1.1rem',
      fontWeight: 600,
      color: mode === 'dark' ? '#fff' : '#222b45',
    },
    h5: {
      fontSize: '1rem',
      fontWeight: 600,
      color: mode === 'dark' ? '#fff' : '#222b45',
    },
    h6: {
      fontSize: '0.95rem',
      fontWeight: 500,
      color: mode === 'dark' ? '#fff' : '#222b45',
    },
    body1: {
      fontSize: '1rem',
      color: mode === 'dark' ? '#f7f7f8' : '#222b45',
    },
    body2: {
      fontSize: '0.95rem',
      color: mode === 'dark' ? '#b4bcd0' : '#6b7280',
    },
    subtitle1: {
      fontWeight: 500,
      color: mode === 'dark' ? '#fff' : '#222b45',
    },
    subtitle2: {
      fontWeight: 500,
      color: mode === 'dark' ? '#b4bcd0' : '#6b7280',
    },
    button: {
      fontWeight: 600,
      letterSpacing: '0.03em',
    },
  },
  shape: {
    borderRadius: 16,
  },
  components: {
    MuiButton: {
      styleOverrides: {
        root: {
          textTransform: 'none',
          fontWeight: 600,
          borderRadius: 8,
          boxShadow: 'none',
          transition: 'all 0.2s cubic-bezier(.4,0,.2,1)',
        },
        containedPrimary: {
          background: 'linear-gradient(90deg, #10a37f 0%, #0b7c5c 100%)',
          color: '#fff',
        },
        outlined: {
          borderColor: '#10a37f',
        },
      },
    },
    MuiCard: {
      styleOverrides: {
        root: {
          borderRadius: 18,
          boxShadow: mode === 'dark' ? '0 4px 24px 0 rgba(16,163,127,0.10)' : '0 2px 8px rgba(16,163,127,0.08)',
          background: mode === 'dark' ? '#444654' : '#fff',
          border: '1px solid rgba(16,163,127,0.08)',
        },
      },
    },
    MuiPaper: {
      styleOverrides: {
        root: {
          background: mode === 'dark' ? '#444654' : '#fff',
          boxShadow: mode === 'dark' ? '0 2px 8px rgba(16,163,127,0.10)' : '0 2px 8px rgba(16,163,127,0.06)',
          border: '1px solid rgba(16,163,127,0.06)',
        },
      },
    },
    MuiChip: {
      styleOverrides: {
        root: {
          borderRadius: 8,
          fontWeight: 500,
        },
      },
    },
    MuiDrawer: {
      styleOverrides: {
        paper: {
          borderRight: '1px solid rgba(255,255,255,0.10)',
          background: mode === 'dark' ? '#202123' : '#fff',
        },
      },
    },
    MuiListItem: {
      styleOverrides: {
        root: {
          borderRadius: 10,
          margin: '4px 8px',
          transition: 'background 0.2s',
          '&.Mui-selected': {
            backgroundColor: mode === 'dark' ? 'rgba(16,163,127,0.10)' : 'rgba(16,163,127,0.08)',
            '&:hover': {
              backgroundColor: mode === 'dark' ? 'rgba(16,163,127,0.16)' : 'rgba(16,163,127,0.12)',
            },
          },
        },
      },
    },
    MuiTextField: {
      styleOverrides: {
        root: {
          borderRadius: 8,
          background: mode === 'dark' ? '#343541' : '#fff',
        },
      },
    },
    MuiAppBar: {
      styleOverrides: {
        root: {
          background: mode === 'dark' ? '#202123' : '#fff',
          color: mode === 'dark' ? '#f7f7f8' : '#1a237e',
          boxShadow: '0 2px 8px rgba(16,163,127,0.08)',
        },
      },
    },
  },
});

// Default export for immediate use
export default createTheme({ palette: darkPalette }); 