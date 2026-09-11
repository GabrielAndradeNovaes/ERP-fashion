import React, { useState, useEffect } from 'react';
import { Box, Typography, Paper, Chip, Button, IconButton, CircularProgress } from '@mui/material';
import { ExternalLink, CreditCard, RefreshCw } from 'lucide-react';
import api from "../api/axios";

interface FaturaSaaS {
  id: string;
  tenantId: string;
  nomeEmpresa: string;
  descricao: string;
  valor: number;
  dataVencimento: string;
  status: 'PENDING' | 'PAID' | 'CANCELED' | 'REFUNDED';
}

const TenantBilling = () => {
  const [faturas, setFaturas] = useState<FaturaSaaS[]>([]);
  const [loading, setLoading] = useState(true);
  const [gerando, setGerando] = useState<string | null>(null);

  const fetchFaturas = async () => {
    setLoading(true);
    try {
      const { data } = await api.get('/billing/faturas');
      setFaturas(data);
    } catch (error) {
      console.error('Erro ao buscar faturas:', error);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchFaturas();
  }, []);

  const handleGerarPagamento = async (id: string) => {
    setGerando(id);
    try {
      await api.post(`/billing/faturas/${id}/gerar-pagamento?gateway=MOCK`);
      window.open(`/pagamento/${id}`, '_blank');
      fetchFaturas();
    } catch (error) {
      console.error('Erro ao gerar pagamento:', error);
      alert('Erro ao gerar pagamento');
    } finally {
      setGerando(null);
    }
  };

  const getStatusColor = (status: string) => {
    switch (status) {
      case 'PAID': return 'success';
      case 'PENDING': return 'warning';
      case 'CANCELED': return 'error';
      default: return 'default';
    }
  };

  return (
    <Box sx={{ p: 4, maxWidth: 1400, margin: '0 auto' }}>
      <Box sx={{ mb: 4, display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
        <Box>
          <Typography variant="h4" sx={{ fontWeight: 800, color: 'var(--text-primary)' }}>
            Minha Assinatura
          </Typography>
          <Typography variant="body1" sx={{ color: 'var(--text-secondary)', mt: 1 }}>
            Gerencie o pagamento da sua assinatura do sistema
          </Typography>
        </Box>
        <Button 
          variant="outlined" 
          startIcon={<RefreshCw size={18} />}
          onClick={fetchFaturas}
          disabled={loading}
        >
          Atualizar
        </Button>
      </Box>

      <Paper sx={{ borderRadius: 4, background: 'var(--bg-card)', border: '1px solid var(--border-color)', overflow: 'hidden' }}>
        {loading ? (
          <Box sx={{ p: 4, display: 'flex', justifyContent: 'center' }}>
            <CircularProgress />
          </Box>
        ) : (
          <Box sx={{ overflowX: 'auto' }}>
            <table style={{ width: '100%', borderCollapse: 'collapse', textAlign: 'left' }}>
              <thead>
                <tr style={{ borderBottom: '1px solid var(--border-color)' }}>
                  <th style={{ padding: '16px', fontWeight: 600 }}>Empresa</th>
                  <th style={{ padding: '16px', fontWeight: 600 }}>Descrição</th>
                  <th style={{ padding: '16px', fontWeight: 600 }}>Vencimento</th>
                  <th style={{ padding: '16px', fontWeight: 600 }}>Valor</th>
                  <th style={{ padding: '16px', fontWeight: 600 }}>Status</th>
                  <th style={{ padding: '16px', fontWeight: 600, textAlign: 'right' }}>Ações</th>
                </tr>
              </thead>
              <tbody>
                {faturas.length === 0 ? (
                  <tr>
                    <td colSpan={6} style={{ padding: '32px', textAlign: 'center', color: 'var(--text-secondary)' }}>
                      Nenhuma fatura encontrada.
                    </td>
                  </tr>
                ) : (
                  faturas.map((fatura) => (
                    <tr key={fatura.id} style={{ borderBottom: '1px solid var(--border-color)' }}>
                      <td style={{ padding: '16px' }}>{fatura.nomeEmpresa}</td>
                      <td style={{ padding: '16px' }}>{fatura.descricao}</td>
                      <td style={{ padding: '16px' }}>
                        {new Intl.DateTimeFormat('pt-BR', { timeZone: 'UTC' }).format(new Date(fatura.dataVencimento))}
                      </td>
                      <td style={{ padding: '16px', fontWeight: 600 }}>
                        {new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' }).format(fatura.valor)}
                      </td>
                      <td style={{ padding: '16px' }}>
                        <Chip 
                          label={fatura.status} 
                          color={getStatusColor(fatura.status)} 
                          size="small" 
                          sx={{ fontWeight: 600 }}
                        />
                      </td>
                      <td style={{ padding: '16px', textAlign: 'right' }}>
                        {fatura.status === 'PENDING' && (
                          <Button
                            variant="contained"
                            size="small"
                            onClick={() => handleGerarPagamento(fatura.id)}
                            disabled={gerando === fatura.id}
                            startIcon={<CreditCard size={16} />}
                            sx={{ mr: 1 }}
                          >
                            Pagar Fatura
                          </Button>
                        )}
                        <IconButton 
                          size="small" 
                          onClick={() => window.open(`/pagamento/${fatura.id}`, '_blank')}
                          title="Ver Página de Checkout"
                        >
                          <ExternalLink size={18} />
                        </IconButton>
                      </td>
                    </tr>
                  ))
                )}
              </tbody>
            </table>
          </Box>
        )}
      </Paper>
    </Box>
  );
};

export default TenantBilling;
