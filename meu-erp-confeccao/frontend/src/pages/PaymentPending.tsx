import React from 'react';
import { Box, Typography, Card, Button } from '@mui/material';
import { AlertCircle, LogOut } from 'lucide-react';
import { useAuth } from '../contexts/AuthContext';

const PaymentPending = () => {
  const { logout, user } = useAuth();

  return (
    <Box 
      sx={{ 
        height: '100vh', 
        display: 'flex', 
        alignItems: 'center', 
        justifyContent: 'center',
        background: 'linear-gradient(135deg, #fff5f5 0%, #ffe3e3 100%)'
      }}
    >
      <Card sx={{ p: 5, width: '100%', maxWidth: 450, borderRadius: 3, boxShadow: '0 8px 32px rgba(255,0,0,0.1)', textAlign: 'center' }}>
        <Box sx={{ display: 'flex', justifyContent: 'center', mb: 3 }}>
          <Box sx={{ bgcolor: 'error.light', p: 2, borderRadius: '50%', color: 'error.main' }}>
            <AlertCircle size={48} />
          </Box>
        </Box>
        <Typography variant="h5" sx={{ fontWeight: 'bold', mb: 1, color: 'error.main' }}>
          Assinatura Pendente
        </Typography>
        <Typography variant="body1" color="text.secondary" sx={{ mb: 4 }}>
          Olá {user?.nome}, o acesso ao sistema para a sua empresa (<strong>{user?.tenantId}</strong>) encontra-se temporariamente suspenso devido a pendências financeiras.
        </Typography>
        
        <Box sx={{ p: 2, bgcolor: 'rgba(0,0,0,0.03)', borderRadius: 2, mb: 4 }}>
          <Typography variant="body2" sx={{ fontWeight: 500 }}>
            Para regularizar seu acesso, por favor entre em contato com nosso suporte ou efetue o pagamento da última fatura em aberto.
          </Typography>
        </Box>

        <Button
          fullWidth
          variant="outlined"
          color="inherit"
          startIcon={<LogOut />}
          onClick={logout}
          sx={{ py: 1.5, fontWeight: 'bold' }}
        >
          Sair da Conta
        </Button>
      </Card>
    </Box>
  );
};

export default PaymentPending;
