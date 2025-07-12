import React, { useState } from 'react';
import {
  AppBar,
  Toolbar,
  Typography,
  IconButton,
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
} from '@mui/material';
import MenuIcon from '@mui/icons-material/Menu';
import SearchIcon from '@mui/icons-material/Search';
import BookmarkIcon from '@mui/icons-material/Bookmark';
import SettingsIcon from '@mui/icons-material/Settings';
import Brightness4Icon from '@mui/icons-material/Brightness4';
import Brightness7Icon from '@mui/icons-material/Brightness7';
import { useNavigate, useLocation } from 'react-router-dom';

interface NavigationProps {
  variant: 'permanent' | 'temporary';
  darkMode: boolean;
  onToggleDarkMode: () => void;
}

const drawerWidth = 220;

const Navigation: React.FC<NavigationProps> = ({ variant, darkMode, onToggleDarkMode }) => {
  const [drawerOpen, setDrawerOpen] = useState(false);
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
    <Box sx={{ width: drawerWidth, pt: 2 }}>
      <Typography variant="h6" align="center" sx={{ mb: 2, fontWeight: 700, color: 'primary.main', letterSpacing: 1 }}>
        Deep Research Agent
      </Typography>
      <Divider />
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
      <Box sx={{ flexGrow: 1 }} />
      <Box sx={{ display: 'flex', justifyContent: 'center', mt: 2 }}>
        <Tooltip title={darkMode ? 'Switch to light mode' : 'Switch to dark mode'}>
          <IconButton onClick={onToggleDarkMode} color="inherit">
            {darkMode ? <Brightness7Icon /> : <Brightness4Icon />}
          </IconButton>
        </Tooltip>
      </Box>
    </Box>
  );

  return (
    <>
      <AppBar
        position="fixed"
        sx={{
          zIndex: theme.zIndex.drawer + 1,
          backgroundColor: 'background.paper',
          color: 'text.primary',
          boxShadow: '0 1px 6px rgba(0,0,0,0.08)',
        }}
      >
        <Toolbar>
          {variant === 'temporary' && (
            <IconButton
              size="large"
              edge="start"
              color="inherit"
              aria-label="menu"
              onClick={handleDrawerToggle}
              sx={{ mr: 2 }}
            >
              <MenuIcon />
            </IconButton>
          )}
          <Typography
            variant="h6"
            component="div"
            sx={{ flexGrow: 1, fontWeight: 700, letterSpacing: 1, cursor: 'pointer', color: 'primary.main' }}
            onClick={() => navigate('/')}
          >
            Deep Research Agent
          </Typography>
          <Box sx={{ display: { xs: 'none', md: 'flex' }, alignItems: 'center', gap: 1 }}>
            {menuItems.map((item) => (
              <Button
                key={item.text}
                color={location.pathname === item.path ? 'primary' : 'inherit'}
                onClick={() => navigate(item.path)}
                sx={{ fontWeight: 500 }}
              >
                {item.text}
              </Button>
            ))}
            <Tooltip title={darkMode ? 'Switch to light mode' : 'Switch to dark mode'}>
              <IconButton onClick={onToggleDarkMode} color="inherit">
                {darkMode ? <Brightness7Icon /> : <Brightness4Icon />}
              </IconButton>
            </Tooltip>
            <Avatar sx={{ ml: 2, bgcolor: 'primary.main', width: 32, height: 32 }}>A</Avatar>
          </Box>
        </Toolbar>
      </AppBar>
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
    </>
  );
};

export default Navigation; 