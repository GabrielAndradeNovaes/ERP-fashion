import React, { useState, useEffect } from 'react';
import { Box, Typography, Paper, Table, TableBody, TableCell, TableContainer, TableHead, TableRow, Button, Grid, TextField, Alert } from '@mui/material';
import api from '../../api/axios';
import { useAuth } from '../../contexts/AuthContext';
import { Play } from 'lucide-react';

interface ResumoProdutividade {
  funcionarioId: string;
  funcionarioNome: string;
  totalCupons: number;
  tempoPadraoProduzido: number;
}

const Produtividade: React.FC = () => {
  const [dataInicio, setDataInicio] = useState(new Date(new Date().setDate(1)).toISOString().split('T')[0]);
  const [dataFim, setDataFim] = useState(new Date().toISOString().split('T')[0]);
  const [metaMinima, setMetaMinima] = useState(75);
  const [premio100, setPremio100] = useState(1000);
  const [resumos, setResumos] = useState<ResumoProdutividade[]>([]);
  const [tempoTeorico, setTempoTeorico] = useState(10000); // Ex: 10 mil minutos
  const [ocorrencias, setOcorrencias] = useState<Record<string, number>>({});
  
  const { hasPermission } = useAuth();
  const canEdit = hasPermission('PCP_EDIT');

  const carregarResumo = async () => {
    try {
      const res = await api.get('/production/produtividade', {
        params: {
          start: dataInicio + 'T00:00:00',
          end: dataFim + 'T23:59:59'
        }
      });
      setResumos(res.data);
      const newOcorrencias = { ...ocorrencias };
      res.data.forEach((r: ResumoProdutividade) => {
        if (newOcorrencias[r.funcionarioId] === undefined) {
          newOcorrencias[r.funcionarioId] = 0; // Padrão 0 minutos
        }
      });
      setOcorrencias(newOcorrencias);
    } catch (err) {
      console.error(err);
      alert('Erro ao carregar produtividade');
    }
  };

  const calcularValorDevido = (resumo: ResumoProdutividade) => {
    const minutosOcorrencia = ocorrencias[resumo.funcionarioId] || 0;
    const minutosTrabalhados = tempoTeorico - minutosOcorrencia;
    
    if (minutosTrabalhados <= 0) return { produtividade: 0, valorPagar: 0 };

    const produtividade = (resumo.tempoPadraoProduzido / minutosTrabalhados) * 100;
    
    if (produtividade <= metaMinima) {
      return { produtividade, valorPagar: 0 };
    }

    const pontosAcima = produtividade - metaMinima;
    const valorPorPonto = premio100 / (100 - metaMinima);
    const valorPagar = pontosAcima * valorPorPonto;
    
    return { produtividade, valorPagar };
  };

  const handleGerarPagamentos = async () => {
    if (!window.confirm("Isso irá dar baixa nos cupons selecionados e gerar títulos a pagar. Confirma?")) return;
    
    const pagamentos = resumos.map(r => {
      const { valorPagar } = calcularValorDevido(r);
      return {
        funcionarioId: r.funcionarioId,
        valorPagar: parseFloat(valorPagar.toFixed(2))
      };
    }).filter(p => p.valorPagar > 0);

    if (pagamentos.length === 0) {
      alert('Nenhum pagamento gerado. Nenhuma funcionária atingiu a meta ou todos os valores são zero.');
      return;
    }

    try {
      await api.post('/production/produtividade/pagar', pagamentos, {
        params: {
          start: dataInicio + 'T00:00:00',
          end: dataFim + 'T23:59:59'
        }
      });
      alert('Pagamentos gerados com sucesso!');
      carregarResumo();
    } catch (err) {
      console.error(err);
      alert('Erro ao gerar pagamentos');
    }
  };

  const formatCurrency = (val: number) => {
    return new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' }).format(val);
  };

  return (
    <Box sx={{ p: 4, height: '100%' }}>
      <Typography variant="h4" sx={{ fontWeight: 'bold', mb: 4 }}>Produtividade e Pagamento</Typography>
      
      <Paper className="premium-card" sx={{ p: 3, mb: 4 }}>
        <Grid container spacing={3} sx={{ alignItems: 'center' }}>
          <Grid size={{ xs: 12, sm: 3 }}>
            <TextField label="Data Inicial" type="date" fullWidth value={dataInicio} onChange={e => setDataInicio(e.target.value)} slotProps={{ inputLabel: { shrink: true } }} />
          </Grid>
          <Grid size={{ xs: 12, sm: 3 }}>
            <TextField label="Data Final" type="date" fullWidth value={dataFim} onChange={e => setDataFim(e.target.value)} slotProps={{ inputLabel: { shrink: true } }} />
          </Grid>
          <Grid size={{ xs: 12, sm: 2 }}>
            <TextField label="Tempo Teórico (Minutos)" type="number" fullWidth value={tempoTeorico} onChange={e => setTempoTeorico(Number(e.target.value))} />
          </Grid>
          <Grid size={{ xs: 12, sm: 2 }}>
            <TextField label="Meta Mínima (%)" type="number" fullWidth value={metaMinima} onChange={e => setMetaMinima(Number(e.target.value))} />
          </Grid>
          <Grid size={{ xs: 12, sm: 2 }}>
            <TextField label="Premiação 100% (R$)" type="number" fullWidth value={premio100} onChange={e => setPremio100(Number(e.target.value))} />
          </Grid>
          <Grid size={{ xs: 12, sm: 2 }}>
            <Button variant="contained" fullWidth onClick={carregarResumo} sx={{ height: '56px', background: 'var(--accent-gradient)' }}>
              Buscar
            </Button>
          </Grid>
        </Grid>
        
        <Box sx={{ mt: 3, display: 'flex', gap: 2 }}>
          <Alert severity="info" sx={{ flex: 1 }}>
            Valor por % adicional calculado: <strong>{formatCurrency(premio100 / (100 - metaMinima))}</strong>
          </Alert>
        </Box>
      </Paper>

      {resumos.length > 0 && (
        <>
          <TableContainer component={Paper} className="premium-card">
            <Table>
              <TableHead>
                <TableRow>
                  <TableCell>Funcionária</TableCell>
                  <TableCell>Cupons Bipados</TableCell>
                  <TableCell>Tempo Padrão (Minutos)</TableCell>
                  <TableCell>Ocorrências (Minutos)</TableCell>
                  <TableCell>Produtividade (%)</TableCell>
                  <TableCell>Valor a Pagar (R$)</TableCell>
                </TableRow>
              </TableHead>
              <TableBody>
                {resumos.map(r => {
                  const { produtividade, valorPagar } = calcularValorDevido(r);
                  return (
                    <TableRow key={r.funcionarioId} hover>
                      <TableCell>{r.funcionarioNome}</TableCell>
                      <TableCell>{r.totalCupons}</TableCell>
                      <TableCell>{r.tempoPadraoProduzido}</TableCell>
                      <TableCell>
                        <TextField 
                          type="number" 
                          size="small" 
                          sx={{ width: 100 }}
                          value={ocorrencias[r.funcionarioId] !== undefined ? ocorrencias[r.funcionarioId] : 0}
                          onChange={(e) => setOcorrencias({...ocorrencias, [r.funcionarioId]: Number(e.target.value)})}
                        />
                      </TableCell>
                      <TableCell>
                        <Typography sx={{ color: produtividade >= 100 ? 'success.main' : (produtividade >= metaMinima ? 'warning.main' : 'error.main'), fontWeight: 'bold' }}>
                          {produtividade.toFixed(2)}%
                        </Typography>
                      </TableCell>
                      <TableCell sx={{ fontWeight: 'bold' }}>
                        {formatCurrency(valorPagar)}
                      </TableCell>
                    </TableRow>
                  );
                })}
              </TableBody>
            </Table>
          </TableContainer>
          
          {canEdit && (
            <Box sx={{ display: 'flex', justifyContent: 'flex-end', mt: 3 }}>
              <Button 
                variant="contained" 
                size="large" 
                startIcon={<Play size={20} />}
                onClick={handleGerarPagamentos}
                sx={{ background: 'var(--accent-gradient)' }}
              >
                Gerar Títulos de Pagamento
              </Button>
            </Box>
          )}
        </>
      )}
    </Box>
  );
};

export default Produtividade;
