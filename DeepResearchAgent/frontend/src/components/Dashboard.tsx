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
  useTheme,
  alpha,
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
  const theme = useTheme();
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
    <Box sx={{ p: 2, background: theme.palette.background.default, minHeight: '100vh' }}>
      <Typography variant="h4" gutterBottom fontWeight="bold">
        Recent Research Reports
      </Typography>

      {recentReports.length === 0 ? (
        <Typography color="text.secondary">No recent reports found.</Typography>
      ) : (
        <Grid container spacing={3}>
          {recentReports.map((results: ResearchResponse) => (
            <Grid item xs={12} md={6} key={results.topic}>
              <Card elevation={4} sx={{ borderRadius: 3, overflow: 'hidden', background: theme.palette.background.paper }}>
                {/* Summary Section */}
                <Paper elevation={0} sx={{ p: 3, background: theme.palette.mode === 'dark' ? alpha(theme.palette.primary.main, 0.10) : alpha(theme.palette.primary.main, 0.04), borderRadius: 0 }}>
                  <Typography variant="h6" fontWeight="bold" gutterBottom>
                    {results.topic}
                  </Typography>
                  <Typography variant="caption" color="text.secondary" gutterBottom>
                    {new Date(results.timestamp).toLocaleString()}
                  </Typography>
                  <Divider sx={{ my: 1 }} />
                  <Typography variant="body1">{results.summary}</Typography>
                </Paper>

                {/* Key Findings */}
                {results.keyFindings && results.keyFindings.length > 0 && (
                  <Box sx={{ p: 2, background: theme.palette.background.paper }}>
                    <Typography variant="subtitle1" fontWeight="bold" sx={{ mb: 1 }}>
                      💡 Key Findings
                    </Typography>
                    <ul style={{ margin: 0, paddingLeft: 20 }}>
                      {results.keyFindings.map((finding: string, index: number) => (
                        <li key={index}>
                          <Typography variant="body2">{finding}</Typography>
                        </li>
                      ))}
                    </ul>
                  </Box>
                )}

                {/* Recommendations */}
                {results.recommendations && results.recommendations.length > 0 && (
                  <Box sx={{ p: 2, background: theme.palette.background.paper }}>
                    <Typography variant="subtitle1" fontWeight="bold" sx={{ mb: 1 }}>
                      ✔️ Recommendations
                    </Typography>
                    <ul style={{ margin: 0, paddingLeft: 20 }}>
                      {results.recommendations.map((recommendation: string, index: number) => (
                        <li key={index}>
                          <Typography variant="body2">{recommendation}</Typography>
                        </li>
                      ))}
                    </ul>
                  </Box>
                )}

                {/* Source Cards */}
                {results.sourceContents && Object.entries(results.sourceContents).map(([source, contents]: [string, SourceContent[]]) => (
                  <Box key={source} sx={{ p: 2, background: theme.palette.background.paper }}>
                    <Typography variant="subtitle1" fontWeight="bold" sx={{ mb: 1 }}>
                      {source === 'NEWS' && '📰 News'}
                      {source === 'ACADEMIC' && '🎓 Academic'}
                      {source === 'FORUM' && '💬 Forum'}
                      {!(source === 'NEWS' || source === 'ACADEMIC' || source === 'FORUM') && source}
                    </Typography>
                    <Grid container spacing={2}>
                      {contents.slice(0, 3).map((content: SourceContent, index: number) => (
                        <Grid item xs={12} key={index}>
                          <Card variant="outlined" sx={{ mb: 1, borderRadius: 2, background: theme.palette.background.paper }}>
                            <CardContent>
                              <Typography variant="subtitle2" fontWeight="bold">
                                {content.title}
                              </Typography>
                              <Typography variant="body2" sx={{ mb: 1 }}>
                                {content.content}
                              </Typography>
                              <Stack direction="row" spacing={1} alignItems="center" sx={{ mb: 1 }}>
                                {content.author && (
                                  <Chip size="small" label={content.author} icon={<BookmarkBorderIcon fontSize="small" />} />
                                )}
                                {content.publishedDate && (
                                  <Chip size="small" label={content.publishedDate} />
                                )}
                              </Stack>
                              {content.url && (
                                <Button size="small" href={content.url} target="_blank" rel="noopener noreferrer" startIcon={<SearchIcon />}>
                                  Read more
                                </Button>
                              )}
                            </CardContent>
                          </Card>
                        </Grid>
                      ))}
                    </Grid>
                  </Box>
                ))}

                <Divider sx={{ my: 2 }} />
                <Box sx={{ p: 2, textAlign: 'right' }}>
                  <Button variant="contained" size="small" endIcon={<SearchIcon />}>
                    View Full Report
                  </Button>
                </Box>
              </Card>
            </Grid>
          ))}
        </Grid>
      )}
    </Box>
  );
};

export default Dashboard; 