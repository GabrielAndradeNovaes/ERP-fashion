import React from 'react';
import { Box, Typography, Paper } from '@mui/material';

const AdminBilling = () => {
  return (
    <Box sx={{ p: 4, maxWidth: 1400, margin: '0 auto' }}>
      <Box sx={{ mb: 4 }}>
        <Typography variant="h4" sx={{ fontWeight: 800, mb: 1 }}>
          Faturamento (Global)
        </Typography>
        <Typography variant="body1" sx={{ color: 'text.secondary' }}>
          Gestão financeira do ERP e assinaturas
        </Typography>
      </Box>

      <Paper sx={{ p: 4, borderRadius: 4, background: 'var(--bg-card)', border: '1px solid var(--border-color)', display: 'flex', justifyContent: 'center', alignItems: 'center', height: 400 }}>
        <Typography variant="h6" color="text.secondary">Em breve: Integração com Gateway de Pagamento</Typography>
      </Paper>
    </Box>
  );
};

export default AdminBilling;
