import { Box, ToggleButton, ToggleButtonGroup } from '@mui/material';
import { Source } from '../types';

interface SourceSelectorProps {
  sources: Source[];
  onSourceChange: (sources: Source[]) => void;
}

export const SourceSelector: React.FC<SourceSelectorProps> = ({ sources, onSourceChange }) => {
  const handleChange = (_: React.MouseEvent<HTMLElement>, newSources: Source[]) => {
    if (newSources.length > 0) {
      onSourceChange(newSources);
    }
  };

  return (
    <Box sx={{ display: 'flex', flexDirection: 'column', gap: 1 }}>
      <ToggleButtonGroup
        value={sources}
        onChange={handleChange}
        aria-label="source selection"
        size="small"
        sx={{ flexWrap: 'wrap', gap: 1 }}
      >
        {Object.values(Source).map((source) => (
          <ToggleButton
            key={source}
            value={source}
            aria-label={source.toLowerCase()}
            sx={{
              textTransform: 'none',
              borderRadius: '20px !important',
              '&.Mui-selected': {
                backgroundColor: 'primary.main',
                color: 'primary.contrastText',
                '&:hover': {
                  backgroundColor: 'primary.dark',
                },
              },
            }}
          >
            {source.charAt(0) + source.slice(1).toLowerCase()}
          </ToggleButton>
        ))}
      </ToggleButtonGroup>
    </Box>
  );
}; 