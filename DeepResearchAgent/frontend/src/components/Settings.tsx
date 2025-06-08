import React, { useState } from 'react';
import {
  Container,
  Typography,
  Paper,
  FormGroup,
  FormControlLabel,
  Switch,
  TextField,
  Button,
  Box,
  Divider,
} from '@mui/material';

const Settings: React.FC = () => {
  const [settings, setSettings] = useState({
    enableNewsSources: true,
    enableAcademicSources: true,
    enableForumSources: true,
    maxResults: 10,
    minConfidence: 0.7,
  });

  const handleChange = (name: string) => (event: React.ChangeEvent<HTMLInputElement>) => {
    setSettings({
      ...settings,
      [name]: event.target.type === 'checkbox' ? event.target.checked : event.target.value,
    });
  };

  const handleSave = () => {
    // TODO: Implement settings save functionality
    console.log('Saving settings:', settings);
  };

  return (
    <Container maxWidth="md" sx={{ mt: 4, mb: 4 }}>
      <Typography variant="h4" component="h1" gutterBottom>
        Settings
      </Typography>

      <Paper sx={{ p: 3 }}>
        <Typography variant="h6" gutterBottom>
          Data Sources
        </Typography>
        <FormGroup>
          <FormControlLabel
            control={
              <Switch
                checked={settings.enableNewsSources}
                onChange={handleChange('enableNewsSources')}
              />
            }
            label="News Sources"
          />
          <FormControlLabel
            control={
              <Switch
                checked={settings.enableAcademicSources}
                onChange={handleChange('enableAcademicSources')}
              />
            }
            label="Academic Databases"
          />
          <FormControlLabel
            control={
              <Switch
                checked={settings.enableForumSources}
                onChange={handleChange('enableForumSources')}
              />
            }
            label="Forum Sources"
          />
        </FormGroup>

        <Divider sx={{ my: 3 }} />

        <Typography variant="h6" gutterBottom>
          Search Parameters
        </Typography>
        <Box sx={{ display: 'flex', gap: 2, mb: 3 }}>
          <TextField
            label="Maximum Results"
            type="number"
            value={settings.maxResults}
            onChange={handleChange('maxResults')}
            inputProps={{ min: 1, max: 50 }}
          />
          <TextField
            label="Minimum Confidence"
            type="number"
            value={settings.minConfidence}
            onChange={handleChange('minConfidence')}
            inputProps={{ min: 0, max: 1, step: 0.1 }}
          />
        </Box>

        <Button
          variant="contained"
          color="primary"
          onClick={handleSave}
        >
          Save Settings
        </Button>
      </Paper>
    </Container>
  );
};

export default Settings; 