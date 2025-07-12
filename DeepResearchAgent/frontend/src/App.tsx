import React, { useState, useMemo } from 'react';
import { ThemeProvider, CssBaseline, Box, useMediaQuery, IconButton, Tooltip } from '@mui/material';
import { ToastContainer } from 'react-toastify';
import { BrowserRouter, Routes, Route } from 'react-router-dom';
import 'react-toastify/dist/ReactToastify.css';
import Navigation from './components/Navigation';
import Chat from './components/Chat';
import SavedReports from './components/SavedReports';
import Settings from './components/Settings';
import { theme as baseTheme } from './theme';
import Brightness4Icon from '@mui/icons-material/Brightness4';
import Brightness7Icon from '@mui/icons-material/Brightness7';

function App() {
  const [darkMode, setDarkMode] = useState(false);
  const theme = useMemo(() => {
    return {
      ...baseTheme,
      palette: {
        ...baseTheme.palette,
        mode: darkMode ? 'dark' : 'light',
      },
    };
  }, [darkMode]);

  const isDesktop = useMediaQuery('(min-width:900px)');

  return (
    <BrowserRouter>
      <ThemeProvider theme={theme}>
        <CssBaseline />
        <Box sx={{ display: 'flex', minHeight: '100vh', background: darkMode ? 'linear-gradient(135deg, #232526 0%, #414345 100%)' : 'linear-gradient(135deg, #f8fafc 0%, #e0e7ff 100%)' }}>
          <Navigation
            variant={isDesktop ? 'permanent' : 'temporary'}
            darkMode={darkMode}
            onToggleDarkMode={() => setDarkMode((prev) => !prev)}
          />
          <Box component="main" sx={{ flexGrow: 1, pt: 8, px: { xs: 1, sm: 3 }, pb: 2 }}>
            <Routes>
              <Route path="/" element={<Chat />} />
              <Route path="/saved" element={<SavedReports />} />
              <Route path="/settings" element={<Settings />} />
            </Routes>
          </Box>
        </Box>
        <ToastContainer />
      </ThemeProvider>
    </BrowserRouter>
  );
}

export default App;
