import React, { useState, useEffect } from 'react';
import { 
  Box, Typography, Paper, FormControl, InputLabel, Select, MenuItem, Button, 
  CircularProgress, Card, CardContent, TextField
} from '@mui/material';
import { BarChart2, Clock, AlertTriangle, CheckCircle } from 'lucide-react';
import api from '../../api/axios';

interface Funcionario {
  id: string;
  nome: string;
  matricula: string;
}

interface ProdutividadeData {
  funcionarioId: string;
  funcionarioNome: string;
  mes: number;
  ano: number;
  tempoProduzidoCentesimal: number;
  cargaHorariaMensal: number;
  tempoOcorrenciasCentesimal: number;
  eficienciaPercentual: number;
}

const Produtividade: React.FC = () => {
  const [funcionarios, setFuncionarios] = useState<Funcionario[]>([]);
  const [selectedFuncionario, setSelectedFuncionario] = useState<string>('');
  const [mes, setMes] = useState<number>(new Date().getMonth() + 1);
  const [ano, setAno] = useState<number>(new Date().getFullYear());
  const [loading, setLoading] = useState(false);
  const [dados, setDados] = useState<ProdutividadeData | null>(null);

  useEffect(() => {
    api.get('/funcionarios')
      .then(res => setFuncionarios(res.data))
      .catch(console.error);
  }, []);

  const handleCalcular = () => {
    if (!selectedFuncionario) return;
    setLoading(true);
    api.get(`/pcp/produtividade?funcionarioId=${selectedFuncionario}&mes=${mes}&ano=${ano}`)
      .then(res => setDados(res.data))
      .catch(console.error)
      .finally(() => setLoading(false));
  };

  const meses = [
    { value: 1, label: 'Janeiro' }, { value: 2, label: 'Fevereiro' }, { value: 3, label: 'Março' },
    { value: 4, label: 'Abril' }, { value: 5, label: 'Maio' }, { value: 6, label: 'Junho' },
    { value: 7, label: 'Julho' }, { value: 8, label: 'Agosto' }, { value: 9, label: 'Setembro' },
    { value: 10, label: 'Outubro' }, { value: 11, label: 'Novembro' }, { value: 12, label: 'Dezembro' }
  ];

  const getEfficiencyColor = (eff: number) => {
    if (eff >= 90) return '#10b981'; // Green
    if (eff >= 70) return '#f59e0b'; // Yellow
    return '#ef4444'; // Red
  };

  return (
    <Box sx={{ p: 4, height: '100%' }}>
      <Typography variant="h4" gutterBottom sx={{ fontWeight: 'bold' }}>Dashboard de Produtividade</Typography>
      
      <Paper elevation={4} sx={{ p: 3, mb: 4, borderRadius: 2 }} className="premium-card">
        <Box sx={{ display: 'grid', gridTemplateColumns: { xs: '1fr', md: 'repeat(12, 1fr)' }, gap: 3, alignItems: 'center' }}>
          <Box sx={{ gridColumn: { md: 'span 4' } }}>
            <FormControl fullWidth>
              <InputLabel>Funcionário</InputLabel>
              <Select value={selectedFuncionario} onChange={e => setSelectedFuncionario(e.target.value)} label="Funcionário">
                {funcionarios.map(f => <MenuItem key={f.id} value={f.id}>{f.nome}</MenuItem>)}
              </Select>
            </FormControl>
          </Box>
          <Box sx={{ gridColumn: { md: 'span 3' } }}>
            <FormControl fullWidth>
              <InputLabel>Mês</InputLabel>
              <Select value={mes} onChange={e => setMes(Number(e.target.value))} label="Mês">
                {meses.map(m => <MenuItem key={m.value} value={m.value}>{m.label}</MenuItem>)}
              </Select>
            </FormControl>
          </Box>
          <Box sx={{ gridColumn: { md: 'span 2' } }}>
            <TextField fullWidth label="Ano" type="number" value={ano} onChange={e => setAno(Number(e.target.value))} />
          </Box>
          <Box sx={{ gridColumn: { md: 'span 3' } }}>
            <Button 
              fullWidth variant="contained" size="large" onClick={handleCalcular}
              disabled={!selectedFuncionario || loading}
              sx={{ background: 'var(--accent-gradient)', height: 56 }}
              startIcon={loading ? <CircularProgress size={20} color="inherit" /> : <BarChart2 />}
            >
              Calcular
            </Button>
          </Box>
        </Box>
      </Paper>

      {dados && (
        <Box sx={{ display: 'grid', gridTemplateColumns: { xs: '1fr', md: 'repeat(2, 1fr)', lg: 'repeat(4, 1fr)' }, gap: 4 }}>
          <Card className="premium-card">
            <CardContent sx={{ textAlign: 'center' }}>
              <Clock size={40} color="var(--text-secondary)" style={{ marginBottom: 10 }} />
              <Typography variant="h6" color="textSecondary">Carga Horária Base</Typography>
              <Typography variant="h3" sx={{ fontWeight: 'bold' }}>{dados.cargaHorariaMensal}h</Typography>
            </CardContent>
          </Card>

          <Card className="premium-card">
            <CardContent sx={{ textAlign: 'center' }}>
              <AlertTriangle size={40} color="#f59e0b" style={{ marginBottom: 10 }} />
              <Typography variant="h6" color="textSecondary">Tempo Ocorrências</Typography>
              <Typography variant="h3" sx={{ fontWeight: 'bold', color: '#f59e0b' }}>{dados.tempoOcorrenciasCentesimal}h</Typography>
              <Typography variant="caption" color="textSecondary">Tempo descontado da meta</Typography>
            </CardContent>
          </Card>

          <Card className="premium-card">
            <CardContent sx={{ textAlign: 'center' }}>
              <CheckCircle size={40} color="#3b82f6" style={{ marginBottom: 10 }} />
              <Typography variant="h6" color="textSecondary">Tempo Produzido</Typography>
              <Typography variant="h3" sx={{ fontWeight: 'bold', color: '#3b82f6' }}>{dados.tempoProduzidoCentesimal}h</Typography>
              <Typography variant="caption" color="textSecondary">Total apontado nos cupons</Typography>
            </CardContent>
          </Card>

          <Card className="premium-card" sx={{ background: getEfficiencyColor(dados.eficienciaPercentual) + '22' }}>
            <CardContent sx={{ textAlign: 'center' }}>
              <BarChart2 size={40} color={getEfficiencyColor(dados.eficienciaPercentual)} style={{ marginBottom: 10 }} />
              <Typography variant="h6" color="textSecondary">Eficiência Final</Typography>
              <Typography variant="h2" sx={{ fontWeight: 900, color: getEfficiencyColor(dados.eficienciaPercentual) }}>
                {dados.eficienciaPercentual}%
              </Typography>
              <Typography variant="caption" color="textSecondary">Meta recomendada: {'>'} 85%</Typography>
            </CardContent>
          </Card>
        </Box>
      )}
    </Box>
  );
};

export default Produtividade;
