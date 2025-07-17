import { useState, useMemo } from 'react';
import { ThemeProvider, CssBaseline, Box, useMediaQuery } from '@mui/material';
import { ToastContainer } from 'react-toastify';
import { BrowserRouter, Routes, Route } from 'react-router-dom';
import 'react-toastify/dist/ReactToastify.css';
import Navigation from './components/Navigation';
import Chat from './components/Chat';
import SavedReports from './components/SavedReports';
import Settings from './components/Settings';
import { theme as createAppTheme } from './theme';
import { Source, TimeRange } from './types';

function App() {
  const [darkMode, setDarkMode] = useState(true); // Default to dark mode
  const [sources, setSources] = useState<Source[]>([Source.NEWS, Source.ACADEMIC, Source.FORUM, Source.GNEWS]);
  const [timeRange, setTimeRange] = useState<TimeRange>(TimeRange.LAST_MONTH);
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
            sources={sources}
            onSourcesChange={setSources}
            timeRange={timeRange}
            onTimeRangeChange={setTimeRange}
          />
          <Box component="main" sx={{ flexGrow: 1, px: { xs: 1, sm: 3 }, pb: 2 }}>
            <Routes>
              <Route path="/" element={<Chat sources={sources} timeRange={timeRange} />} />
              <Route path="/saved" element={<SavedReports />} />
              <Route path="/settings" element={<Settings sources={sources} onSourcesChange={setSources} timeRange={timeRange} onTimeRangeChange={setTimeRange} />} />
            </Routes>
          </Box>
        </Box>
        <ToastContainer />
      </ThemeProvider>
    </BrowserRouter>
  );
}

export default App;
