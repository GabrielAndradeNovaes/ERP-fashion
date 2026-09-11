import React, { useState, useEffect } from 'react';
import { useParams } from 'react-router-dom';
import { Box, Typography, Card, CircularProgress, Button, Divider, Alert } from '@mui/material';
import { CheckCircle, Copy, ShieldCheck, CreditCard } from 'lucide-react';
import { QRCodeSVG } from 'qrcode.react';
import axios from 'axios';

interface CheckoutDetails {
  tituloDescricao: string;
  valor: number;
  statusTitulo: string;
  qrCodePayload: string;
  qrCodeImageUrl: string;
  statusTransacao: string;
}

const Checkout = () => {
  const { id } = useParams<{ id: string }>();
  const [details, setDetails] = useState<CheckoutDetails | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(false);
  const [copied, setCopied] = useState(false);

  const fetchDetails = async () => {
    try {
      const response = await axios.get(`/api/public/checkout/${id}`);
      setDetails(response.data);
      if (response.data.statusTitulo === 'PAID' || response.data.statusTransacao === 'PAID') {
        setLoading(false);
      }
    } catch (err) {
      setError(true);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchDetails();
    
    // Polling a cada 5 segundos se não estiver pago
    const interval = setInterval(() => {
      if (details?.statusTitulo !== 'PAID') {
        fetchDetails();
      }
    }, 5000);

    return () => clearInterval(interval);
  }, [id, details?.statusTitulo]);

  const handleCopyPix = () => {
    if (details?.qrCodePayload) {
      navigator.clipboard.writeText(details.qrCodePayload);
      setCopied(true);
      setTimeout(() => setCopied(false), 2000);
    }
  };

  const handleSimulatePayment = async () => {
    try {
      await axios.post(`/api/public/checkout/mock-webhook/${id}`);
      fetchDetails();
    } catch (err) {
      console.error(err);
    }
  };

  if (loading && !details) {
    return (
      <Box sx={{ height: '100vh', display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
        <CircularProgress />
      </Box>
    );
  }

  if (error || !details) {
    return (
      <Box sx={{ height: '100vh', display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
        <Typography color="error">Cobrança não encontrada ou inválida.</Typography>
      </Box>
    );
  }

  const isPaid = details.statusTitulo === 'PAID' || details.statusTransacao === 'PAID';

  return (
    <Box sx={{ 
      minHeight: '100vh', 
      bgcolor: '#f5f7fa',
      display: 'flex',
      alignItems: 'center',
      justifyContent: 'center',
      p: 2
    }}>
      <Card sx={{ 
        maxWidth: 450, 
        width: '100%', 
        p: { xs: 3, md: 5 }, 
        borderRadius: 4, 
        boxShadow: '0 20px 40px rgba(0,0,0,0.08)' 
      }}>
        <Box sx={{ textAlign: 'center', mb: 4 }}>
          <Box sx={{ display: 'flex', justifyContent: 'center', mb: 2 }}>
            <Box sx={{ bgcolor: 'primary.main', color: 'white', p: 1.5, borderRadius: 2 }}>
              <CreditCard size={28} />
            </Box>
          </Box>
          <Typography variant="h5" fontWeight="bold" gutterBottom>
            Pagamento Seguro
          </Typography>
          <Typography variant="body2" color="text.secondary">
            {details.tituloDescricao}
          </Typography>
        </Box>

        {isPaid ? (
          <Box sx={{ textAlign: 'center', py: 4 }}>
            <Box sx={{ color: 'success.main', mb: 2 }}>
              <CheckCircle size={64} style={{ margin: '0 auto' }} />
            </Box>
            <Typography variant="h5" color="success.main" fontWeight="bold" gutterBottom>
              Pagamento Aprovado!
            </Typography>
            <Typography variant="body1" color="text.secondary">
              Seu pagamento de <strong>R$ {details.valor.toFixed(2)}</strong> foi confirmado com sucesso.
            </Typography>
          </Box>
        ) : (
          <>
            <Box sx={{ textAlign: 'center', mb: 3 }}>
              <Typography variant="h3" fontWeight="bold" color="primary.main">
                R$ {details.valor.toFixed(2)}
              </Typography>
            </Box>

            {details.qrCodePayload ? (
              <Box sx={{ display: 'flex', flexDirection: 'column', alignItems: 'center' }}>
                <Box sx={{ 
                  p: 2, 
                  bgcolor: 'white', 
                  border: '1px solid', 
                  borderColor: 'divider',
                  borderRadius: 2,
                  mb: 3
                }}>
                  <QRCodeSVG value={details.qrCodePayload} size={200} />
                </Box>

                <Button
                  fullWidth
                  variant="contained"
                  color="primary"
                  size="large"
                  startIcon={<Copy />}
                  onClick={handleCopyPix}
                  sx={{ py: 1.5, mb: 2, borderRadius: 2, fontWeight: 'bold' }}
                >
                  {copied ? 'Chave Copiada!' : 'Copiar Chave Copia e Cola'}
                </Button>

                <Alert severity="info" sx={{ width: '100%', mb: 3, borderRadius: 2 }}>
                  Abra o app do seu banco e escolha a opção <strong>PIX Copia e Cola</strong>.
                </Alert>

                <Button 
                  size="small" 
                  color="warning" 
                  onClick={handleSimulatePayment}
                  sx={{ textTransform: 'none', mt: 2 }}
                >
                  [DEV] Simular pagamento (Mock Webhook)
                </Button>
              </Box>
            ) : (
              <Alert severity="warning">
                Nenhum link de pagamento gerado. O administrador precisa gerar a cobrança.
              </Alert>
            )}
          </>
        )}

        <Divider sx={{ my: 3 }} />
        
        <Box sx={{ display: 'flex', alignItems: 'center', justifyContent: 'center', color: 'text.secondary', gap: 1 }}>
          <ShieldCheck size={18} />
          <Typography variant="caption" fontWeight="medium">
            Ambiente Seguro e Criptografado
          </Typography>
        </Box>
      </Card>
    </Box>
  );
};

export default Checkout;
