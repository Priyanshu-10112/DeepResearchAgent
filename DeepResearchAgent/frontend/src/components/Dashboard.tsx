import {
  Box,
  Container,
  Typography,
  Paper,
  TextField,
  Button,
  CircularProgress,
  Alert,
  Chip,
  Grid,
  FormControl,
  InputLabel,
  Select,
  MenuItem,
  Stack,
  Card,
  CardContent,
  CardActions,
  IconButton,
  Tooltip,
  Divider,
  List,
  ListItem,
  ListItemText,
} from '@mui/material';
import SearchIcon from '@mui/icons-material/Search';
import BookmarkIcon from '@mui/icons-material/Bookmark';
import BookmarkBorderIcon from '@mui/icons-material/BookmarkBorder';
import ShareIcon from '@mui/icons-material/Share';
import DownloadIcon from '@mui/icons-material/Download';
import { researchService, availableSources } from '../services/researchService';
import { QueryType, TimeRange } from '../types';
import type { ResearchResponse, SourceContent } from '../types';
import { toast } from 'react-toastify';
import { Save as SaveIcon, Delete as DeleteIcon, Refresh as RefreshIcon } from '@mui/icons-material';
import { useState, useEffect } from 'react';

const QUERY_TYPES = Object.values(QueryType);
const TIME_RANGES = Object.values(TimeRange);

const Dashboard: React.FC = () => {
  const [recentReports, setRecentReports] = useState<ResearchResponse[]>([]);
  const [isLoading, setIsLoading] = useState(false);

  const fetchRecentReports = async () => {
    try {
      setIsLoading(true);
      const reports = await researchService.getReports();
      setRecentReports(reports.slice(0, 5)); // Get only the 5 most recent reports
    } catch (error) {
      console.error('Error fetching recent reports:', error);
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    fetchRecentReports();
  }, []);

  if (isLoading) {
    return (
      <Box sx={{ display: 'flex', justifyContent: 'center', p: 3 }}>
        <CircularProgress />
      </Box>
    );
  }

  return (
    <Box sx={{ p: 2 }}>
      <Typography variant="h5" gutterBottom>
        Recent Research Reports
      </Typography>

      {recentReports.length === 0 ? (
        <Typography color="text.secondary">No recent reports found.</Typography>
      ) : (
        <Grid container spacing={2}>
          {recentReports.map((results: ResearchResponse) => (
            <Grid item xs={12} key={results.topic}>
              <Paper sx={{ p: 2 }}>
                <Typography variant="h6" gutterBottom>
                  {results.topic}
                </Typography>
                <Typography variant="body2" color="text.secondary" gutterBottom>
                  {new Date(results.timestamp).toLocaleString()}
                </Typography>
                <Typography variant="body1" paragraph>
                  {results.summary}
                </Typography>

                {results.keyFindings && results.keyFindings.length > 0 && (
                  <Box sx={{ mb: 2 }}>
                    <Typography variant="subtitle1" fontWeight="bold">
                      Key Findings:
                    </Typography>
                    <ul>
                      {results.keyFindings.map((finding: string, index: number) => (
                        <li key={index}>
                          <Typography variant="body2">{finding}</Typography>
                        </li>
                      ))}
                    </ul>
                  </Box>
                )}

                {results.recommendations && results.recommendations.length > 0 && (
                  <Box sx={{ mb: 2 }}>
                    <Typography variant="subtitle1" fontWeight="bold">
                      Recommendations:
                    </Typography>
                    <ul>
                      {results.recommendations.map((recommendation: string, index: number) => (
                        <li key={index}>
                          <Typography variant="body2">{recommendation}</Typography>
                        </li>
                      ))}
                    </ul>
                  </Box>
                )}

                {results.sourceContents && Object.entries(results.sourceContents).map(([source, contents]: [string, SourceContent[]]) => (
                  <Box key={source} sx={{ mb: 2 }}>
                    <Typography variant="subtitle1" fontWeight="bold">
                      {source.charAt(0) + source.slice(1).toLowerCase()} Sources:
                    </Typography>
                    <Stack spacing={1}>
                      {contents.map((content: SourceContent, index: number) => (
                        <Paper key={index} variant="outlined" sx={{ p: 1 }}>
                          <Typography variant="subtitle2">{content.title}</Typography>
                          <Typography variant="body2">{content.content}</Typography>
                          {content.url && (
                            <Typography variant="caption" component="a" href={content.url} target="_blank" rel="noopener noreferrer">
                              Read more
                            </Typography>
                          )}
                        </Paper>
                      ))}
                    </Stack>
                  </Box>
                ))}

                <Box sx={{ mt: 2 }}>
                  <Button variant="outlined" size="small">
                    View Full Report
                  </Button>
                </Box>
              </Paper>
            </Grid>
          ))}
        </Grid>
      )}
    </Box>
  );
};

export default Dashboard; 