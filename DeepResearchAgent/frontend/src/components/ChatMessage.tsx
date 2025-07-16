import {
  Box,
  Typography,
  Paper,
  Stack,
  Tooltip,
  IconButton,
  CircularProgress,
  Divider,
  Button,
  useTheme,
  alpha,
} from '@mui/material';
import SaveIcon from '@mui/icons-material/Save';
import DeleteIcon from '@mui/icons-material/Delete';
import SearchIcon from '@mui/icons-material/Search';
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
  const theme = useTheme();
  const renderResults = () => {
    if (!message.results) return null;

    const { sourceContents, keyFindings, recommendations, summary, topic, timestamp } = message.results as ResearchResponse & { topic?: string; summary?: string; timestamp?: string };
    if (!sourceContents) return null;

    const filteredContents = Object.entries(sourceContents)
      .filter(([source]) => selectedSources.includes(source as Source));

    if (filteredContents.length === 0) return null;

    return (
      <Box sx={{ mt: 2, background: theme.palette.background.default, borderRadius: 2, p: 2 }}>
        {/* Summary Section */}
        {summary && (
          <Paper elevation={0} sx={{ p: 2, background: theme.palette.background.paper, borderRadius: 2, mb: 2 }}>
            {topic && (
              <Typography variant="h6" fontWeight="bold" gutterBottom>
                {topic}
              </Typography>
            )}
            <Typography variant="body1">{summary}</Typography>
            {timestamp && (
              <Typography variant="caption" color="text.secondary" sx={{ display: 'block', mt: 1 }}>
                {new Date(timestamp).toLocaleString()}
              </Typography>
            )}
          </Paper>
        )}

        {/* Key Findings */}
        {keyFindings && keyFindings.length > 0 && (
          <Box sx={{ p: 2, background: theme.palette.background.paper, borderRadius: 2, mb: 2 }}>
            <Typography variant="subtitle1" fontWeight="bold" sx={{ mb: 1 }}>
              💡 Key Findings
            </Typography>
            <ul style={{ margin: 0, paddingLeft: 20 }}>
              {keyFindings.map((finding, index) => (
                <li key={index}>
                  <Typography variant="body2">{finding}</Typography>
                </li>
              ))}
            </ul>
          </Box>
        )}

        {/* Recommendations */}
        {recommendations && recommendations.length > 0 && (
          <Box sx={{ p: 2, background: theme.palette.background.paper, borderRadius: 2, mb: 2 }}>
            <Typography variant="subtitle1" fontWeight="bold" sx={{ mb: 1 }}>
              ✔️ Recommendations
            </Typography>
            <ul style={{ margin: 0, paddingLeft: 20 }}>
              {recommendations.map((recommendation, index) => (
                <li key={index}>
                  <Typography variant="body2">{recommendation}</Typography>
                </li>
              ))}
            </ul>
          </Box>
        )}

        {/* Source Cards */}
        {filteredContents.map(([source, contents]) => (
          <Box key={source} sx={{ p: 2, background: theme.palette.background.paper, borderRadius: 2, mb: 2 }}>
            <Typography variant="subtitle1" fontWeight="bold" sx={{ mb: 1 }}>
              {source === 'NEWS' && '📰 News'}
              {source === 'ACADEMIC' && '🎓 Academic'}
              {source === 'FORUM' && '💬 Forum'}
              {!(source === 'NEWS' || source === 'ACADEMIC' || source === 'FORUM') && source}
            </Typography>
            <Stack spacing={2}>
              {contents.map((content, index) => (
                <Paper key={index} variant="outlined" sx={{ p: 2, borderRadius: 2, background: theme.palette.background.paper }}>
                  <Typography variant="subtitle2" fontWeight="bold">
                    {content.title}
                  </Typography>
                  <Typography variant="body2" sx={{ mb: 1 }}>
                    {content.content}
                  </Typography>
                  <Stack direction="row" spacing={1} alignItems="center" sx={{ mb: 1 }}>
                    {content.author && (
                      <Typography variant="caption" color="text.secondary">👤 {content.author}</Typography>
                    )}
                    {content.publishedDate && (
                      <Typography variant="caption" color="text.secondary">📅 {content.publishedDate}</Typography>
                    )}
                  </Stack>
                  {content.url && (
                    <Button size="small" href={content.url} target="_blank" rel="noopener noreferrer" startIcon={<SearchIcon />} aria-label={`Read more about ${content.title}`}
                      sx={{ background: theme.palette.primary.main, color: theme.palette.primary.contrastText, '&:hover': { background: theme.palette.primary.dark } }}>
                      Read more
                    </Button>
                  )}
                </Paper>
              ))}
            </Stack>
          </Box>
        ))}
      </Box>
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
                  <IconButton size="small" onClick={onSave} aria-label="Save message">
                    <SaveIcon />
                  </IconButton>
                </Tooltip>
                <Tooltip title="Delete">
                  <IconButton size="small" onClick={onDelete} aria-label="Delete message">
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