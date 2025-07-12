import React, { useState } from 'react';
import {
  Drawer,
  List,
  ListItem,
  ListItemIcon,
  ListItemText,
  Box,
  Avatar,
  Tooltip,
  useTheme,
  Divider,
  Button,
  TextField,
  InputAdornment,
  Typography,
} from '@mui/material';
import IconButton from '@mui/material/IconButton';
import SearchIcon from '@mui/icons-material/Search';
import BookmarkIcon from '@mui/icons-material/Bookmark';
import SettingsIcon from '@mui/icons-material/Settings';
import Brightness4Icon from '@mui/icons-material/Brightness4';
import Brightness7Icon from '@mui/icons-material/Brightness7';
import { useNavigate, useLocation } from 'react-router-dom';
import { SourceSelector } from './SourceSelector';
import { Source, TimeRange } from '../types';

interface NavigationProps {
  variant: 'permanent' | 'temporary';
  darkMode: boolean;
  onToggleDarkMode: () => void;
}

const drawerWidth = 260;

const Navigation: React.FC<NavigationProps> = ({ variant, darkMode, onToggleDarkMode }) => {
  const [drawerOpen, setDrawerOpen] = useState(false);
  const [sources, setSources] = useState<Source[]>([Source.NEWS, Source.ACADEMIC, Source.FORUM]);
  const [timeRange, setTimeRange] = useState<TimeRange>(TimeRange.LAST_MONTH);
  const theme = useTheme();
  const navigate = useNavigate();
  const location = useLocation();

  const menuItems = [
    { text: 'Research', icon: <SearchIcon />, path: '/' },
    { text: 'Saved Reports', icon: <BookmarkIcon />, path: '/saved' },
    { text: 'Settings', icon: <SettingsIcon />, path: '/settings' },
  ];

  const handleDrawerToggle = () => setDrawerOpen((open) => !open);

  const drawer = (
    <Box sx={{ width: drawerWidth, height: '100%', display: 'flex', flexDirection: 'column', pt: 2 }}>
      <Typography variant="h6" align="center" sx={{ mb: 2, fontWeight: 700, color: 'primary.main', letterSpacing: 1 }}>
        Deep Research Agent
      </Typography>
      <Divider />
      <Box sx={{ p: 2, pb: 0 }}>
        <Typography variant="subtitle2" sx={{ mb: 1, color: 'text.secondary', fontWeight: 600 }}>
          Select Sources
        </Typography>
        <SourceSelector sources={sources} onSourceChange={setSources} />
        <Box sx={{ display: 'flex', gap: 1, mt: 2, mb: 1 }}>
          <Button
            variant="outlined"
            size="small"
            onClick={() => setTimeRange(TimeRange.LAST_MONTH)}
            color={timeRange === TimeRange.LAST_MONTH ? 'primary' : 'inherit'}
            sx={{ flex: 1 }}
          >
            Last Month
          </Button>
          <Button
            variant="outlined"
            size="small"
            onClick={() => setTimeRange(TimeRange.LAST_WEEK)}
            color={timeRange === TimeRange.LAST_WEEK ? 'primary' : 'inherit'}
            sx={{ flex: 1 }}
          >
            Last Week
          </Button>
          <Button
            variant="outlined"
            size="small"
            onClick={() => setTimeRange(TimeRange.LAST_DAY)}
            color={timeRange === TimeRange.LAST_DAY ? 'primary' : 'inherit'}
            sx={{ flex: 1 }}
          >
            Last Day
          </Button>
        </Box>
      </Box>
      <Divider sx={{ my: 2 }} />
      <Box sx={{ flex: 1, overflowY: 'auto' }}>
        <List>
          {menuItems.map((item) => (
            <ListItem
              button
              key={item.text}
              selected={location.pathname === item.path}
              onClick={() => {
                navigate(item.path);
                if (variant === 'temporary') setDrawerOpen(false);
              }}
              sx={{ borderRadius: 2, mx: 1, my: 0.5 }}
            >
              <ListItemIcon>{item.icon}</ListItemIcon>
              <ListItemText primary={item.text} />
            </ListItem>
          ))}
        </List>
      </Box>
      <Box sx={{ display: 'flex', justifyContent: 'center', mt: 2, mb: 2 }}>
        <Tooltip title={darkMode ? 'Switch to light mode' : 'Switch to dark mode'}>
          <IconButton onClick={onToggleDarkMode} color="inherit">
            {darkMode ? <Brightness7Icon /> : <Brightness4Icon />}
          </IconButton>
        </Tooltip>
      </Box>
    </Box>
  );

  return (
    <nav>
      <Drawer
        variant={variant}
        open={variant === 'permanent' ? true : drawerOpen}
        onClose={handleDrawerToggle}
        sx={{
          width: variant === 'permanent' ? drawerWidth : undefined,
          flexShrink: 0,
          '& .MuiDrawer-paper': {
            width: drawerWidth,
            boxSizing: 'border-box',
            backgroundColor: 'background.paper',
            borderRight: '1px solid #e0e0e0',
          },
          display: { xs: 'block', md: variant === 'permanent' ? 'block' : 'none' },
        }}
      >
        {drawer}
      </Drawer>
    </nav>
  );
};

export default Navigation; 