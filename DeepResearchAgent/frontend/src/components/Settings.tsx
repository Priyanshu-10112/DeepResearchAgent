import {
  Container,
  Typography,
  Paper,
  TextField,
  Button,
  Box,
  Divider,
  Stack,
  useTheme,
} from '@mui/material';
import { Source, TimeRange } from '../types';
import { SourceSelector } from './SourceSelector';
import { useState } from 'react';

interface SettingsProps {
  sources: Source[];
  onSourcesChange: (sources: Source[]) => void;
  timeRange: TimeRange;
  onTimeRangeChange: (timeRange: TimeRange) => void;
}

const Settings: React.FC<SettingsProps> = ({ 
  sources, 
  onSourcesChange, 
  timeRange, 
  onTimeRangeChange 
}) => {
  const [settings, setSettings] = useState({
    maxResults: 10,
    minConfidence: 0.7,
  });
  const theme = useTheme();

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
    <Container maxWidth="md" sx={{ mt: 4, mb: 4, background: theme.palette.background.default }}>
      <Typography variant="h4" component="h1" gutterBottom sx={{ fontWeight: 700, color: 'primary.main' }}>
        Settings
      </Typography>
      <Typography variant="body1" color="text.secondary" sx={{ mb: 4 }}>
        Configure your research preferences and data source settings.
      </Typography>

      <Paper sx={{ p: 3, boxShadow: 2 }}>
        <Typography variant="h6" gutterBottom sx={{ color: 'primary.main', fontWeight: 600 }}>
          Data Sources
        </Typography>
        <Typography variant="body2" color="text.secondary" sx={{ mb: 2 }}>
          Select which sources to include in your research queries. These settings are synchronized with the sidebar.
        </Typography>
        
        <Box sx={{ mb: 3, p: 2, bgcolor: theme.palette.background.paper, borderRadius: 1 }}>
          <Typography variant="subtitle2" sx={{ mb: 1, color: 'text.secondary', fontWeight: 600 }}>
            Available Sources
          </Typography>
          <SourceSelector sources={sources} onSourceChange={onSourcesChange} />
        </Box>

        <Divider sx={{ my: 3 }} />

        <Typography variant="h6" gutterBottom sx={{ color: 'primary.main', fontWeight: 600 }}>
          Time Range
        </Typography>
        <Typography variant="body2" color="text.secondary" sx={{ mb: 2 }}>
          Set the default time range for research queries.
        </Typography>
        
        <Box sx={{ mb: 3, p: 2, bgcolor: theme.palette.background.paper, borderRadius: 1 }}>
          <Stack direction="row" spacing={1}>
            <Button
              variant="outlined"
              size="small"
              onClick={() => onTimeRangeChange(TimeRange.LAST_MONTH)}
              color={timeRange === TimeRange.LAST_MONTH ? 'primary' : 'inherit'}
              sx={{ flex: 1 }}
            >
              Last Month
            </Button>
            <Button
              variant="outlined"
              size="small"
              onClick={() => onTimeRangeChange(TimeRange.LAST_WEEK)}
              color={timeRange === TimeRange.LAST_WEEK ? 'primary' : 'inherit'}
              sx={{ flex: 1 }}
            >
              Last Week
            </Button>
            <Button
              variant="outlined"
              size="small"
              onClick={() => onTimeRangeChange(TimeRange.LAST_DAY)}
              color={timeRange === TimeRange.LAST_DAY ? 'primary' : 'inherit'}
              sx={{ flex: 1 }}
            >
              Last Day
            </Button>
          </Stack>
        </Box>

        <Divider sx={{ my: 3 }} />

        <Typography variant="h6" gutterBottom sx={{ color: 'primary.main', fontWeight: 600 }}>
          Search Parameters
        </Typography>
        <Typography variant="body2" color="text.secondary" sx={{ mb: 2 }}>
          Configure how many results to fetch and the minimum quality threshold for research queries.
        </Typography>
        
        <Box sx={{ display: 'flex', gap: 3, mb: 3 }}>
          <TextField
            label="Maximum Results per Source"
            type="number"
            value={settings.maxResults}
            onChange={handleChange('maxResults')}
            inputProps={{ min: 1, max: 50 }}
            helperText="Number of articles to fetch from each source (1-50)"
            sx={{ flex: 1 }}
          />
          <TextField
            label="Minimum Confidence Score"
            type="number"
            value={settings.minConfidence}
            onChange={handleChange('minConfidence')}
            inputProps={{ min: 0, max: 1, step: 0.1 }}
            helperText="Filter results by relevance score (0.0-1.0)"
            sx={{ flex: 1 }}
          />
        </Box>

        <Box sx={{ mb: 3, p: 2, bgcolor: theme.palette.background.paper, borderRadius: 1, border: '1px solid', borderColor: 'divider' }}>
          <Typography variant="subtitle2" sx={{ mb: 1, fontWeight: 600 }}>
            Current Settings Summary:
          </Typography>
          <Typography variant="body2" color="text.secondary">
            • Fetch up to <strong>{settings.maxResults}</strong> results from each source
          </Typography>
          <Typography variant="body2" color="text.secondary">
            • Only show results with confidence score ≥ <strong>{settings.minConfidence}</strong>
          </Typography>
          <Typography variant="body2" color="text.secondary" sx={{ mt: 1, fontStyle: 'italic' }}>
            Note: These settings will be applied to future research queries.
          </Typography>
        </Box>

        <Box sx={{ display: 'flex', gap: 2 }}>
          <Button
            variant="contained"
            color="primary"
            onClick={handleSave}
            aria-label="Save settings"
            sx={{ minWidth: 120, background: theme.palette.primary.main, color: theme.palette.primary.contrastText, '&:hover': { background: theme.palette.primary.dark } }}
          >
            Save Settings
          </Button>
          <Button
            variant="outlined"
            onClick={() => {
              setSettings({
                maxResults: 10,
                minConfidence: 0.7,
              });
            }}
            aria-label="Reset to defaults"
            sx={{ minWidth: 120 }}
          >
            Reset to Defaults
          </Button>
        </Box>
      </Paper>
    </Container>
  );
};

export default Settings; 