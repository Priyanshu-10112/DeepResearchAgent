import React from 'react';
import { ThemeProvider, CssBaseline, Box } from '@mui/material';
import { ToastContainer } from 'react-toastify';
import { BrowserRouter, Routes, Route } from 'react-router-dom';
import 'react-toastify/dist/ReactToastify.css';
import Navigation from './components/Navigation';
import Chat from './components/Chat';
import SavedReports from './components/SavedReports';
import Settings from './components/Settings';
import { theme } from './theme';

function App() {
  return (
    <BrowserRouter>
      <ThemeProvider theme={theme}>
        <CssBaseline />
        <Box sx={{ display: 'flex', flexDirection: 'column', minHeight: '100vh' }}>
          <Navigation />
          <Box 
            component="main" 
            sx={{ 
              flexGrow: 1, 
              pt: 8, // Add padding top to account for fixed navbar
              px: 2,
              pb: 2,
              backgroundColor: 'background.default'
            }}
          >
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
