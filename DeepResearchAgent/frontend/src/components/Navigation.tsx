import React from 'react';
import {
  AppBar,
  Toolbar,
  Typography,
  Button,
  Box,
  IconButton,
  useTheme,
} from '@mui/material';
import MenuIcon from '@mui/icons-material/Menu';
import { useNavigate } from 'react-router-dom';

const Navigation: React.FC = () => {
  const navigate = useNavigate();
  const theme = useTheme();

  return (
    <AppBar 
      position="fixed" 
      sx={{ 
        zIndex: theme.zIndex.drawer + 1,
        backgroundColor: 'background.paper',
        color: 'text.primary',
        boxShadow: '0 1px 3px rgba(0,0,0,0.1)'
      }}
    >
      <Toolbar>
        <IconButton
          size="large"
          edge="start"
          color="inherit"
          aria-label="menu"
          sx={{ mr: 2 }}
        >
          <MenuIcon />
        </IconButton>
        
        <Typography
          variant="h6"
          component="div"
          sx={{ 
            flexGrow: 1, 
            cursor: 'pointer',
            fontWeight: 600,
            color: 'primary.main'
          }}
          onClick={() => navigate('/')}
        >
          Deep Research Agent
        </Typography>

        <Box sx={{ display: 'flex', gap: 2 }}>
          <Button 
            color="inherit" 
            onClick={() => navigate('/')}
            sx={{ 
              color: 'text.primary',
              '&:hover': {
                backgroundColor: 'action.hover'
              }
            }}
          >
            Home
          </Button>
          <Button 
            color="inherit" 
            onClick={() => navigate('/saved')}
            sx={{ 
              color: 'text.primary',
              '&:hover': {
                backgroundColor: 'action.hover'
              }
            }}
          >
            Saved Reports
          </Button>
          <Button 
            color="inherit" 
            onClick={() => navigate('/settings')}
            sx={{ 
              color: 'text.primary',
              '&:hover': {
                backgroundColor: 'action.hover'
              }
            }}
          >
            Settings
          </Button>
        </Box>
      </Toolbar>
    </AppBar>
  );
};

export default Navigation; 