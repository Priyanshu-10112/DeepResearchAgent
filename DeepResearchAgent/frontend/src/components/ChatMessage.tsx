import {
  Box,
  Typography,
  Paper,
  Stack,
  Tooltip,
  IconButton,
  CircularProgress,
  Divider,
} from '@mui/material';
import SaveIcon from '@mui/icons-material/Save';
import DeleteIcon from '@mui/icons-material/Delete';
import { Message, ResearchResponse, Source } from '../types';
import { format } from 'date-fns';
import { motion } from 'framer-motion';
import { styled } from '@mui/material/styles';

const MessageBubble = styled(Paper)(({ theme }) => ({
  padding: theme.spacing(2),
  maxWidth: '80%',
  marginBottom: theme.spacing(1),
  '&.user': {
    backgroundColor: theme.palette.primary.main,
    color: theme.palette.primary.contrastText,
    marginLeft: 'auto',
  },
  '&.assistant': {
    backgroundColor: theme.palette.background.paper,
    border: `1px solid ${theme.palette.divider}`,
  },
}));

interface ChatMessageProps {
  message: Message;
  onSave: () => void;
  onDelete: () => void;
  selectedSources?: Source[];
}

const ChatMessage: React.FC<ChatMessageProps> = ({ message, onSave, onDelete, selectedSources = [Source.NEWS, Source.ACADEMIC, Source.FORUM] }) => {
  const renderResults = () => {
    if (!message.results) return null;

    const { sourceContents, keyFindings, recommendations } = message.results;
    if (!sourceContents) return null;

    const filteredContents = Object.entries(sourceContents)
      .filter(([source]) => selectedSources.includes(source as Source));

    if (filteredContents.length === 0) return null;

    return (
      <Stack spacing={2} sx={{ mt: 2 }}>
        {/* Key Findings Section */}
        {keyFindings && keyFindings.length > 0 && (
          <Box>
            <Typography variant="subtitle1" fontWeight="bold" sx={{ mb: 1 }}>
              Key Findings
            </Typography>
            <Paper 
              variant="outlined" 
              sx={{ 
                p: 2,
                backgroundColor: 'background.paper',
              }}
            >
              <Stack spacing={1}>
                {keyFindings.map((finding, index) => (
                  <Typography 
                    key={index} 
                    variant="body2"
                    sx={{ 
                      display: 'flex',
                      alignItems: 'flex-start',
                      gap: 1,
                      '&::before': {
                        content: '"•"',
                        color: 'primary.main',
                        fontWeight: 'bold',
                      }
                    }}
                  >
                    {finding}
                  </Typography>
                ))}
              </Stack>
            </Paper>
          </Box>
        )}

        {/* Recommendations Section */}
        {recommendations && recommendations.length > 0 && (
          <Box>
            <Typography variant="subtitle1" fontWeight="bold" sx={{ mb: 1 }}>
              Recommendations
            </Typography>
            <Paper 
              variant="outlined" 
              sx={{ 
                p: 2,
                backgroundColor: 'background.paper',
              }}
            >
              <Stack spacing={1}>
                {recommendations.map((recommendation, index) => (
                  <Typography 
                    key={index} 
                    variant="body2"
                    sx={{ 
                      display: 'flex',
                      alignItems: 'flex-start',
                      gap: 1,
                      '&::before': {
                        content: '"→"',
                        color: 'primary.main',
                      }
                    }}
                  >
                    {recommendation}
                  </Typography>
                ))}
              </Stack>
            </Paper>
          </Box>
        )}

        {/* Source Contents Section */}
        <Divider sx={{ my: 2 }} />
        <Typography variant="subtitle1" fontWeight="bold" sx={{ mb: 1 }}>
          Source Details
        </Typography>
        {filteredContents.map(([source, contents]) => {
          if (!contents || contents.length === 0) return null;
          
          return (
            <Box key={source}>
              <Typography variant="subtitle1" fontWeight="bold" sx={{ mb: 1 }}>
                {source.charAt(0) + source.slice(1).toLowerCase()} Sources
              </Typography>
              <Stack spacing={1}>
                {contents.map((content, index) => (
                  <Paper 
                    key={index} 
                    variant="outlined" 
                    sx={{ 
                      p: 2,
                      backgroundColor: 'background.paper',
                      '&:hover': {
                        backgroundColor: 'action.hover',
                      },
                    }}
                  >
                    <Typography variant="subtitle2" gutterBottom>
                      {content.title}
                    </Typography>
                    <Typography variant="body2" color="text.secondary" paragraph>
                      {content.content}
                    </Typography>
                    {content.url && (
                      <Typography 
                        variant="caption" 
                        component="a" 
                        href={content.url} 
                        target="_blank" 
                        rel="noopener noreferrer"
                        sx={{ 
                          color: 'primary.main',
                          textDecoration: 'none',
                          '&:hover': {
                            textDecoration: 'underline',
                          },
                        }}
                      >
                        Read more
                      </Typography>
                    )}
                  </Paper>
                ))}
              </Stack>
            </Box>
          );
        })}
      </Stack>
    );
  };

  return (
    <motion.div
      initial={{ opacity: 0, y: 20 }}
      animate={{ opacity: 1, y: 0 }}
      transition={{ duration: 0.3 }}
      style={{
        display: 'flex',
        justifyContent: message.role === 'user' ? 'flex-end' : 'flex-start',
        marginBottom: '16px',
        width: '100%',
      }}
    >
      <MessageBubble className={message.role}>
        <Stack direction="row" spacing={1} alignItems="center" justifyContent="space-between">
          <Typography variant="body1">{message.content}</Typography>
          <Stack direction="row" spacing={1}>
            {message.role === 'assistant' && (
              <>
                <Tooltip title="Save">
                  <IconButton size="small" onClick={onSave}>
                    <SaveIcon />
                  </IconButton>
                </Tooltip>
                <Tooltip title="Delete">
                  <IconButton size="small" onClick={onDelete}>
                    <DeleteIcon />
                  </IconButton>
                </Tooltip>
              </>
            )}
          </Stack>
        </Stack>
        {message.role === 'assistant' && renderResults()}
        <Typography variant="caption" sx={{ display: 'block', mt: 1, opacity: 0.7 }}>
          {new Date(message.timestamp).toLocaleString()}
        </Typography>
      </MessageBubble>
    </motion.div>
  );
};

export default ChatMessage; 