import React from 'react';
import { Box, Typography, Button } from '@mui/material';

const LandingPage = () => {
  return (
    <Box sx={{ 
      display: 'flex', 
      flexDirection: 'column', 
      alignItems: 'center', 
      justifyContent: 'center', 
      minHeight: '100vh',
      background: 'linear-gradient(135deg, #f3f4f6 0%, #e5e7eb 100%)',
      textAlign: 'center',
      p: 4
    }}>
      <Typography variant="h2" sx={{ fontWeight: 800, mb: 2, background: 'var(--accent-gradient)', WebkitBackgroundClip: 'text', WebkitTextFillColor: 'transparent' }}>
        Fashion ERP
      </Typography>
      <Typography variant="h5" sx={{ color: 'text.secondary', mb: 4, maxWidth: 600 }}>
        O sistema completo para gestão de confecções, produção e estoque. Em breve nossa nova Landing Page estará no ar!
      </Typography>
      <Button 
        variant="contained" 
        size="large"
        href="/login"
        sx={{ 
          background: 'var(--accent-gradient)',
          px: 4,
          py: 1.5,
          borderRadius: 2,
          textTransform: 'none',
          fontSize: '1.1rem',
          fontWeight: 600
        }}
      >
        Acessar o Sistema
      </Button>
    </Box>
  );
};

export default LandingPage;
