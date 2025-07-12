import React, { useState, useRef, useEffect } from 'react';
import { Box, Typography, Paper, Stack, Button, CircularProgress, TextField } from '@mui/material';
import { Send as SendIcon } from '@mui/icons-material';
import InputAdornment from '@mui/material/InputAdornment';
import SearchIcon from '@mui/icons-material/Search';
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
    <Box sx={{ height: '100%', display: 'flex', flexDirection: 'column', position: 'relative' }}>
      {/* Chat messages area, with padding bottom to avoid overlap with fixed input bar */}
      <Box sx={{ flex: 1, overflow: 'auto', mb: { xs: '90px', sm: '100px' } }}>
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

      {/* Fixed input bar at the bottom */}
      <Paper 
        elevation={2}
        sx={{ 
          position: 'fixed',
          left: { xs: 0, sm: '260px' },
          right: 0,
          bottom: 0,
          zIndex: 1201,
          p: { xs: 1, sm: 2 },
          background: 'linear-gradient(90deg, #232526 0%, #343541 100%)',
          borderTop: 1,
          borderColor: 'divider',
          borderRadius: 0,
          boxShadow: '0 -2px 16px 0 rgba(16,163,127,0.12)',
          width: { xs: '100%', sm: 'calc(100% - 260px)' },
          maxWidth: '100vw',
        }}
      >
        <Stack direction="row" spacing={2} alignItems="center">
          <TextField
            fullWidth
            multiline
            minRows={1}
            maxRows={4}
            value={input}
            onChange={(e) => setInput(e.target.value)}
            onKeyPress={handleKeyPress}
            placeholder="Type your research query..."
            disabled={loading}
            InputProps={{
              startAdornment: (
                <InputAdornment position="start">
                  <SearchIcon sx={{ color: 'primary.main', opacity: 0.7 }} />
                </InputAdornment>
              ),
              style: { borderRadius: 24, background: 'rgba(255,255,255,0.08)' },
              'aria-label': 'Chat input',
            }}
            sx={{
              borderRadius: 4,
              background: 'rgba(255,255,255,0.04)',
              boxShadow: '0 2px 8px rgba(16,163,127,0.10)',
              border: '1.5px solid',
              borderColor: 'primary.main',
              transition: 'box-shadow 0.2s, border-color 0.2s',
              '& .MuiOutlinedInput-root': {
                borderRadius: 4,
                background: 'rgba(255,255,255,0.08)',
                fontSize: '1.08rem',
                padding: '6px 12px',
                border: 'none',
                boxShadow: 'none',
                '&.Mui-focused': {
                  boxShadow: '0 0 0 2px #10a37f44',
                  borderColor: 'primary.main',
                },
              },
            }}
          />
          <Button
            variant="contained"
            onClick={handleSendMessage}
            disabled={loading || !input.trim()}
            sx={{
              minWidth: 56,
              minHeight: 56,
              borderRadius: '50%',
              boxShadow: '0 4px 16px 0 #10a37f33',
              p: 0,
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'center',
              background: 'linear-gradient(90deg, #10a37f 0%, #0b7c5c 100%)',
              '&:hover': {
                background: 'linear-gradient(90deg, #0b7c5c 0%, #10a37f 100%)',
                boxShadow: '0 6px 24px 0 #10a37f44',
              },
            }}
            aria-label="Send message"
          >
            {loading ? <CircularProgress size={24} /> : <SendIcon fontSize="medium" />}
          </Button>
        </Stack>
      </Paper>
    </Box>
  );
};

export default Chat; 