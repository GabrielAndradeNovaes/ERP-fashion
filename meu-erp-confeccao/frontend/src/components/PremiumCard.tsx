import React from 'react';
import { Card } from '@mui/material';
import type { CardProps } from '@mui/material';

const PremiumCard: React.FC<CardProps> = ({ children, sx, ...props }) => {
  return (
    <Card 
      className="glass-card"
      sx={{ 
        borderRadius: 3, 
        overflow: 'hidden',
        ...sx 
      }} 
      {...props}
    >
      {children}
    </Card>
  );
};

export default PremiumCard;
