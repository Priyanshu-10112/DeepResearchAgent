import React, { useState, useMemo } from 'react';
import { ThemeProvider, CssBaseline, Box, useMediaQuery } from '@mui/material';
import { ToastContainer } from 'react-toastify';
import { BrowserRouter, Routes, Route } from 'react-router-dom';
import 'react-toastify/dist/ReactToastify.css';
import Navigation from './components/Navigation';
import Chat from './components/Chat';
import SavedReports from './components/SavedReports';
import Settings from './components/Settings';
import { theme as createAppTheme } from './theme';

function App() {
  const [darkMode, setDarkMode] = useState(true); // Default to dark mode
  const mode = darkMode ? 'dark' : 'light';
  const theme = useMemo(() => createAppTheme(mode), [mode]);
  const isDesktop = useMediaQuery('(min-width:900px)');

  return (
    <BrowserRouter>
      <ThemeProvider theme={theme}>
        <CssBaseline />
        <Box sx={{ display: 'flex', minHeight: '100vh', background: theme.palette.background.default }}>
          <Navigation
            variant={isDesktop ? 'permanent' : 'temporary'}
            darkMode={darkMode}
            onToggleDarkMode={() => setDarkMode((prev) => !prev)}
          />
          <Box component="main" sx={{ flexGrow: 1, px: { xs: 1, sm: 3 }, pb: 2 }}>
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
