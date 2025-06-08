import React, { useState, KeyboardEvent, useCallback } from 'react';
import { Box, TextField, IconButton, Tooltip } from '@mui/material';
import SendIcon from '@mui/icons-material/Send';
import { styled } from '@mui/material/styles';

const StyledTextField = styled(TextField)(({ theme }) => ({
  '& .MuiOutlinedInput-root': {
    borderRadius: theme.spacing(2),
    backgroundColor: theme.palette.background.paper,
    '&:hover': {
      '& > fieldset': {
        borderColor: theme.palette.primary.main,
      },
    },
  },
}));

interface ChatInputProps {
  onSend: (message: string) => void;
  disabled?: boolean;
}

export const ChatInput: React.FC<ChatInputProps> = ({ onSend, disabled }) => {
  const [message, setMessage] = useState('');
  const [error, setError] = useState<string | null>(null);

  const validateMessage = useCallback((text: string): boolean => {
    if (!text.trim()) {
      setError('Message cannot be empty');
      return false;
    }
    if (text.length > 1000) {
      setError('Message is too long (max 1000 characters)');
      return false;
    }
    setError(null);
    return true;
  }, []);

  const handleSend = useCallback(() => {
    if (validateMessage(message) && !disabled) {
      onSend(message.trim());
      setMessage('');
      setError(null);
    }
  }, [message, disabled, onSend, validateMessage]);

  const handleKeyPress = (event: KeyboardEvent<HTMLDivElement>) => {
    if (event.key === 'Enter' && !event.shiftKey) {
      event.preventDefault();
      handleSend();
    }
  };

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const newMessage = e.target.value;
    setMessage(newMessage);
    validateMessage(newMessage);
  };

  return (
    <Box
      sx={{
        display: 'flex',
        gap: 1,
        p: 2,
        backgroundColor: 'background.paper',
        borderTop: 1,
        borderColor: 'divider',
      }}
    >
      <StyledTextField
        fullWidth
        multiline
        maxRows={4}
        value={message}
        onChange={handleChange}
        onKeyPress={handleKeyPress}
        placeholder="Type your research query..."
        disabled={disabled}
        variant="outlined"
        size="small"
        error={!!error}
        helperText={error}
      />
      <Tooltip title={disabled ? 'Processing...' : 'Send message'}>
        <span>
          <IconButton
            color="primary"
            onClick={handleSend}
            disabled={!message.trim() || disabled}
            sx={{
              alignSelf: 'flex-end',
              mb: 0.5,
            }}
          >
            <SendIcon />
          </IconButton>
        </span>
      </Tooltip>
    </Box>
  );
}; 