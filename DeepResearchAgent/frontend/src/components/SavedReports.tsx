import React, { useState, useEffect } from 'react';
import {
  Box,
  Typography,
  Paper,
  List,
  ListItem,
  ListItemText,
  IconButton,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  Button,
  Stack,
  CircularProgress,
} from '@mui/material';
import { Delete as DeleteIcon, Visibility as ViewIcon } from '@mui/icons-material';
import { researchService } from '../services/researchService';
import type { ResearchResponse } from '../types';

const SavedReports: React.FC = () => {
  const [reports, setReports] = useState<ResearchResponse[]>([]);
  const [selectedReport, setSelectedReport] = useState<ResearchResponse | null>(null);
  const [isLoading, setIsLoading] = useState(false);
  const [isDeleting, setIsDeleting] = useState(false);

  const fetchReports = async () => {
    try {
      setIsLoading(true);
      const data = await researchService.getReports();
      setReports(data);
    } catch (error) {
      console.error('Error fetching reports:', error);
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    fetchReports();
  }, []);

  const handleDelete = async (report: ResearchResponse) => {
    if (!report.topic) return;
    
    try {
      setIsDeleting(true);
      await researchService.deleteReport(report.topic);
      setReports(reports.filter(r => r.topic !== report.topic));
    } catch (error) {
      console.error('Error deleting report:', error);
    } finally {
      setIsDeleting(false);
    }
  };

  const handleView = (report: ResearchResponse) => {
    setSelectedReport(report);
  };

  const handleCloseDialog = () => {
    setSelectedReport(null);
  };

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
        Saved Reports
      </Typography>
      
      {reports.length === 0 ? (
        <Typography color="text.secondary">No saved reports found.</Typography>
      ) : (
        <List>
          {reports.map((report) => (
            <ListItem
              key={report.topic}
              secondaryAction={
                <Stack direction="row" spacing={1}>
                  <IconButton
                    edge="end"
                    aria-label="view"
                    onClick={() => handleView(report)}
                  >
                    <ViewIcon />
                  </IconButton>
                  <IconButton
                    edge="end"
                    aria-label="delete"
                    onClick={() => handleDelete(report)}
                    disabled={isDeleting}
                  >
                    <DeleteIcon />
                  </IconButton>
                </Stack>
              }
            >
              <ListItemText
                primary={report.topic}
                secondary={new Date(report.timestamp).toLocaleString()}
              />
            </ListItem>
          ))}
        </List>
      )}

      <Dialog
        open={!!selectedReport}
        onClose={handleCloseDialog}
        maxWidth="md"
        fullWidth
      >
        {selectedReport && (
          <>
            <DialogTitle>{selectedReport.topic}</DialogTitle>
            <DialogContent>
              <Stack spacing={2}>
                <Typography variant="body1">{selectedReport.summary}</Typography>

                {selectedReport.keyFindings && selectedReport.keyFindings.length > 0 && (
                  <Box>
                    <Typography variant="subtitle1" fontWeight="bold">Key Findings:</Typography>
                    <ul>
                      {selectedReport.keyFindings.map((finding, index) => (
                        <li key={index}>
                          <Typography variant="body2">{finding}</Typography>
                        </li>
                      ))}
                    </ul>
                  </Box>
                )}

                {selectedReport.recommendations && selectedReport.recommendations.length > 0 && (
                  <Box>
                    <Typography variant="subtitle1" fontWeight="bold">Recommendations:</Typography>
                    <ul>
                      {selectedReport.recommendations.map((recommendation, index) => (
                        <li key={index}>
                          <Typography variant="body2">{recommendation}</Typography>
                        </li>
                      ))}
                    </ul>
                  </Box>
                )}

                {selectedReport.sourceContents && Object.entries(selectedReport.sourceContents).map(([source, contents]) => (
                  <Box key={source}>
                    <Typography variant="subtitle1" fontWeight="bold">
                      {source.charAt(0) + source.slice(1).toLowerCase()} Sources:
                    </Typography>
                    {contents.map((content, index) => (
                      <Paper key={index} sx={{ p: 1, mb: 1 }}>
                        <Typography variant="subtitle2">{content.title}</Typography>
                        <Typography variant="body2">{content.content}</Typography>
                        {content.url && (
                          <Typography variant="caption" component="a" href={content.url} target="_blank" rel="noopener noreferrer">
                            Read more
                          </Typography>
                        )}
                      </Paper>
                    ))}
                  </Box>
                ))}
              </Stack>
            </DialogContent>
            <DialogActions>
              <Button onClick={handleCloseDialog}>Close</Button>
            </DialogActions>
          </>
        )}
      </Dialog>
    </Box>
  );
};

export default SavedReports; 