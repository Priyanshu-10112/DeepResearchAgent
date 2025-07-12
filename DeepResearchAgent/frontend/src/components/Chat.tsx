import React, { useState, useRef, useEffect } from 'react';
import { Box, Typography, Paper, Stack, Button, CircularProgress, TextField } from '@mui/material';
import { Send as SendIcon } from '@mui/icons-material';
import { researchService } from '../services/researchService';
import { QueryType, TimeRange, Source } from '../types';
import type { Message, ResearchQuery, ResearchResponse } from '../types';
import ChatMessage from './ChatMessage';
import { SourceSelector } from './SourceSelector';

const Chat: React.FC = () => {
  const [messages, setMessages] = useState<Message[]>([]);
  const [input, setInput] = useState('');
  const [loading, setLoading] = useState(false);
  const [sources, setSources] = useState<Source[]>([Source.NEWS, Source.ACADEMIC, Source.FORUM]); // Default to all sources
  const [timeRange, setTimeRange] = useState<TimeRange>(TimeRange.LAST_MONTH);
  const [queryType, setQueryType] = useState<QueryType>(QueryType.GENERAL_RESEARCH);
  const messagesEndRef = useRef<HTMLDivElement>(null);

  const scrollToBottom = () => {
    messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' });
  };

  useEffect(() => {
    scrollToBottom();
  }, [messages]);

  const handleSendMessage = async () => {
    if (!input.trim()) return;

    const userMessage: Message = {
      id: Date.now().toString(),
      content: input,
      role: 'user',
      timestamp: new Date().toISOString(),
    };

    setMessages(prev => [...prev, userMessage]);
    setInput('');

    const botMessage: Message = {
      id: (Date.now() + 1).toString(),
      content: '',
      role: 'assistant',
      timestamp: new Date().toISOString(),
    };

    setMessages(prev => [...prev, botMessage]);
    setLoading(true);

    try {
      const query: ResearchQuery = {
        query: input,
        preferredSources: sources,
        queryType,
        timeRange,
      };

      const response = await researchService.submitQuery(query);
      
      setMessages(prev => prev.map(msg => 
        msg.id === botMessage.id 
          ? { ...msg, content: response.summary, results: response }
          : msg
      ));
    } catch (error) {
      setMessages(prev => prev.map(msg => 
        msg.id === botMessage.id 
          ? { ...msg, content: error instanceof Error ? error.message : 'An error occurred' }
          : msg
      ));
    } finally {
      setLoading(false);
    }
  };

  const handleKeyPress = (e: React.KeyboardEvent) => {
    if (e.key === 'Enter' && !e.shiftKey) {
      e.preventDefault();
      handleSendMessage();
    }
  };

  return (
    <Box sx={{ height: '100%', display: 'flex', flexDirection: 'column' }}>
      <Box
        sx={{
          position: 'sticky',
          top: { xs: 64, sm: 72 }, // AppBar height
          zIndex: 10,
          mb: 2,
        }}
      >
        <Paper 
          elevation={2} 
          sx={{ 
            p: { xs: 1, sm: 2 },
            backgroundColor: 'background.paper',
            border: 1,
            borderColor: 'divider',
            borderRadius: 2,
            boxShadow: '0 2px 8px rgba(0,0,0,0.04)',
          }}
        >
          <Stack spacing={2} direction={{ xs: 'column', sm: 'row' }} alignItems="center" justifyContent="space-between">
            <Box>
              <Typography variant="subtitle1" fontWeight="medium">
                Select Sources
              </Typography>
              <SourceSelector
                sources={sources}
                onSourceChange={setSources}
              />
            </Box>
            <Box sx={{ display: 'flex', gap: 1, mt: { xs: 1, sm: 0 } }}>
              <Button
                variant="outlined"
                size="small"
                onClick={() => setTimeRange(TimeRange.LAST_MONTH)}
                color={timeRange === TimeRange.LAST_MONTH ? 'primary' : 'inherit'}
              >
                Last Month
              </Button>
              <Button
                variant="outlined"
                size="small"
                onClick={() => setTimeRange(TimeRange.LAST_WEEK)}
                color={timeRange === TimeRange.LAST_WEEK ? 'primary' : 'inherit'}
              >
                Last Week
              </Button>
              <Button
                variant="outlined"
                size="small"
                onClick={() => setTimeRange(TimeRange.LAST_DAY)}
                color={timeRange === TimeRange.LAST_DAY ? 'primary' : 'inherit'}
              >
                Last Day
              </Button>
            </Box>
          </Stack>
        </Paper>
      </Box>

      <Box sx={{ flex: 1, overflow: 'auto', mb: 2 }}>
        {messages.map((message) => (
          <ChatMessage
            key={message.id}
            message={message}
            onSave={() => {}}
            onDelete={() => {}}
            selectedSources={sources}
          />
        ))}
        <div ref={messagesEndRef} />
      </Box>

      <Paper 
        elevation={0}
        sx={{ 
          p: 2,
          backgroundColor: 'background.default',
          border: 1,
          borderColor: 'divider',
          borderRadius: 2,
        }}
      >
        <Stack direction="row" spacing={2}>
          <TextField
            fullWidth
            multiline
            maxRows={4}
            value={input}
            onChange={(e) => setInput(e.target.value)}
            onKeyPress={handleKeyPress}
            placeholder="Type your research query..."
            disabled={loading}
            sx={{
              '& .MuiOutlinedInput-root': {
                backgroundColor: 'background.paper',
              },
            }}
          />
          <Button
            variant="contained"
            onClick={handleSendMessage}
            disabled={loading || !input.trim()}
            sx={{ minWidth: 100 }}
          >
            {loading ? <CircularProgress size={24} /> : <SendIcon />}
          </Button>
        </Stack>
      </Paper>
    </Box>
  );
};

export default Chat; 