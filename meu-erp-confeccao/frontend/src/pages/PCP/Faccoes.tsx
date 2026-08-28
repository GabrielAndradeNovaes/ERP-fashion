import React, { useState, useEffect } from 'react';
import { Truck, CheckCircle2, Factory } from 'lucide-react';
import api from '../../api/axios';
import { useAuth } from '../../contexts/AuthContext';
import {
  Box,
  Typography,
  Button,
  CircularProgress,
  Card,
  CardContent,
  CardActions,
  Grid,
  Chip
} from '@mui/material';

interface OrdemProducao {
  id: string;
  numero: string;
  produtoBaseNome: string;
  fichaTecnicaVersao: string;
  quantidade: number;
  status: string;
  criadoEm: string;
}

const Faccoes = () => {
  const [ordens, setOrdens] = useState<OrdemProducao[]>([]);
  const [loading, setLoading] = useState(true);
  const [processingId, setProcessingId] = useState<string | null>(null);

  const { hasPermission } = useAuth();
  const canEdit = hasPermission('PCP_EDIT');

  useEffect(() => {
    fetchFaccoes();
  }, []);

  const fetchFaccoes = async () => {
    try {
      setLoading(true);
      const res = await api.get('/production/ordens');
      const faccoes = res.data.filter((o: OrdemProducao) => o.status === 'FACCAO');
      setOrdens(faccoes);
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const handleReceber = async (id: string) => {
    if (!canEdit) return;
    try {
      setProcessingId(id);
      await api.put(`/production/ordens/${id}/status`, { status: 'CONCLUIDA' });
      alert('Retorno recebido com sucesso! Estoque de produtos atualizado.');
      fetchFaccoes();
    } catch (err: any) {
      alert(err.response?.data?.message || 'Erro ao receber retorno');
    } finally {
      setProcessingId(null);
    }
  };

  return (
    <Box className="animate-fade-in-up" sx={{ height: '100%', display: 'flex', flexDirection: 'column' }}>
      <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 4 }}>
        <Box>
          <Typography variant="h4" component="h1" gutterBottom sx={{ fontWeight: 800, color: 'var(--text-primary)', letterSpacing: '-0.5px' }}>
            Gestão de <span className="text-gradient">Facções</span>
          </Typography>
          <Typography variant="body1" sx={{ color: 'var(--text-secondary)' }}>
            Gerencie as ordens de produção enviadas para costureiras externas.
          </Typography>
        </Box>
      </Box>

      {loading ? (
        <Box sx={{ flexGrow: 1, display: 'flex', justifyContent: 'center', alignItems: 'center' }}>
          <CircularProgress sx={{ color: 'var(--accent-primary)' }} />
        </Box>
      ) : ordens.length === 0 ? (
        <Box sx={{ flexGrow: 1, display: 'flex', flexDirection: 'column', alignItems: 'center', justifyContent: 'center', opacity: 0.5 }}>
          <Factory size={64} style={{ marginBottom: 16, color: 'var(--text-secondary)' }} />
          <Typography variant="h6" sx={{ color: 'var(--text-secondary)' }}>
            Nenhuma OP em facção no momento.
          </Typography>
        </Box>
      ) : (
        <Grid container spacing={3}>
          {ordens.map(op => (
            <Grid item xs={12} sm={6} md={4} key={op.id}>
              <Card sx={{ 
                bgcolor: 'var(--bg-card)', 
                border: '1px solid var(--border-color)',
                boxShadow: '0 4px 6px rgba(0,0,0,0.1)',
                transition: 'transform 0.2s',
                '&:hover': { transform: 'translateY(-4px)' }
              }}>
                <CardContent>
                  <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 2 }}>
                    <Typography variant="h6" sx={{ fontWeight: 700, color: 'var(--accent-primary)' }}>
                      #{op.numero}
                    </Typography>
                    <Chip 
                      label={`${op.quantidade} peças`}
                      size="small" 
                      sx={{ bgcolor: 'rgba(139, 92, 246, 0.1)', color: '#8b5cf6', fontWeight: 600 }}
                    />
                  </Box>
                  <Typography variant="body1" sx={{ color: 'var(--text-primary)', mb: 1 }}>
                    {op.produtoBaseNome}
                  </Typography>
                  <Typography variant="caption" sx={{ color: 'var(--text-secondary)' }}>
                    Ficha Técnica: {op.fichaTecnicaVersao}
                  </Typography>
                  <Box sx={{ mt: 2, display: 'flex', alignItems: 'center', gap: 1 }}>
                    <Truck size={16} color="#8b5cf6" />
                    <Typography variant="body2" sx={{ color: '#8b5cf6', fontWeight: 600 }}>
                      Em costura externa
                    </Typography>
                  </Box>
                </CardContent>
                {canEdit && (
                  <CardActions sx={{ borderTop: '1px solid var(--border-color)', p: 2, bgcolor: 'rgba(0,0,0,0.1)' }}>
                    <Button 
                      fullWidth
                      variant="contained"
                      onClick={() => handleReceber(op.id)}
                      disabled={processingId === op.id}
                      startIcon={processingId === op.id ? <CircularProgress size={16} color="inherit" /> : <CheckCircle2 size={18} />}
                      sx={{ 
                        bgcolor: 'var(--success)', 
                        '&:hover': { bgcolor: '#059669' },
                        textTransform: 'none',
                        fontWeight: 600
                      }}
                    >
                      Receber Retorno
                    </Button>
                  </CardActions>
                )}
              </Card>
            </Grid>
          ))}
        </Grid>
      )}
    </Box>
  );
};

export default Faccoes;
