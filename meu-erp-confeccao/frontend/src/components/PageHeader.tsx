import React from 'react';
import { Box, Typography } from '@mui/material';

interface PageHeaderProps {
  title: string;
  subtitle: string;
  icon: React.ReactNode;
  action?: React.ReactNode;
}

const PageHeader: React.FC<PageHeaderProps> = ({ title, subtitle, icon, action }) => {
  return (
    <Box sx={{ display: 'flex', alignItems: 'center', mb: 4, gap: 2 }}>
      <Box sx={{ p: 1.5, bgcolor: 'primary.main', borderRadius: 2, color: 'white', display: 'flex' }}>
        {icon}
      </Box>
      <Box>
        <Typography variant="h4" sx={{ fontWeight: 800, color: 'var(--text-primary)' }}>
          {title}
        </Typography>
        <Typography variant="body1" color="text.secondary">
          {subtitle}
        </Typography>
      </Box>
      {action && (
        <Box sx={{ ml: 'auto' }}>
          {action}
        </Box>
      )}
    </Box>
  );
};

export default PageHeader;
