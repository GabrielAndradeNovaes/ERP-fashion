import React, { useState, useEffect, useMemo } from 'react';
import { Trash2 } from 'lucide-react';
import api from '../api/axios';
import { DataTable } from '../components/DataTable';
import type { ColumnDef } from '@tanstack/react-table';
import {
  Box,
  Typography,
  Button,
  Card,
  TextField,
  FormControl,
  InputLabel,
  Select,
  MenuItem,
  Stack,
  Chip,
  CircularProgress,
  Grid
} from '@mui/material';
import AddIcon from '@mui/icons-material/Add';

interface TabelaTempo {
  id: string;
  indice: number;
  grauDificuldade: string;
  faixaComprimento: string;
  tempoCentesimal: number;
}

const TabelaTempos = () => {
  const [tempos, setTempos] = useState<TabelaTempo[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  // Form State
  const [indice, setIndice] = useState<number | ''>('');
  const [grauDificuldade, setGrauDificuldade] = useState('MEDIO');
  const [faixaComprimento, setFaixaComprimento] = useState('DE_0_A_60');
  const [tempo, setTempo] = useState<string>('');
  const [isSubmitting, setIsSubmitting] = useState(false);

  const fetchTempos = async () => {
    try {
      setLoading(true);
      const res = await api.get('/production/tempos-padrao');
      setTempos(res.data);
      setError(null);
    } catch (err: any) {
      setError(err.response?.data?.message || 'Erro ao buscar tabela de tempos.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchTempos();
  }, []);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!indice || !tempo) return;

    try {
      setIsSubmitting(true);
      await api.post('/production/tempos-padrao', {
        indice: Number(indice),
        grauDificuldade,
        faixaComprimento,
        tempoCentesimal: parseFloat(tempo)
      });
      // Reset form & reload
      setIndice('');
      setTempo('');
      fetchTempos();
    } catch (err: any) {
      alert(err.response?.data?.message || 'Erro ao cadastrar tempo padrão.');
    } finally {
      setIsSubmitting(false);
    }
  };

  const handleDelete = async (id: string) => {
    if (!window.confirm('Deseja realmente remover este tempo?')) return;
    try {
      await api.delete(`/production/tempos-padrao/${id}`);
      fetchTempos();
    } catch (err) {
      alert('Erro ao excluir registro.');
    }
  };

  // Utility to show user-friendly format (ex: 1.50 -> "1m 30s")
  const formatTime = (centesimal: number) => {
    const minutes = Math.floor(centesimal);
    const seconds = Math.round((centesimal - minutes) * 100 * 0.6); // 100 centesimals = 60 seconds
    if (minutes === 0) return `${seconds}s`;
    return `${minutes}m ${seconds}s`;
  };

  const columns = useMemo<ColumnDef<any, any, any>[]>(() => [
    {
      accessorKey: 'indice',
      header: 'Índice',
      cell: (info) => <Chip label={`Idx ${info.getValue()}`} color="primary" variant="outlined" size="small" />
    },
    {
      accessorKey: 'grauDificuldade',
      header: 'Dificuldade',
      cell: (info) => <Typography>{(info.getValue() as string).replace('_', ' ')}</Typography>
    },
    {
      accessorKey: 'faixaComprimento',
      header: 'Comprimento',
      cell: (info) => <Typography>{(info.getValue() as string).replace(/_/g, ' ')}</Typography>
    },
    {
      accessorKey: 'tempoCentesimal',
      header: 'Centesimal',
      cell: (info) => <Typography color="primary" sx={{ fontWeight: 'bold' }}>{(info.getValue() as number).toFixed(2)}</Typography>
    },
    {
      id: 'formatoLeitura',
      header: 'Formato Leitura',
      cell: (info) => <Typography color="text.secondary">{formatTime(info.row.original.tempoCentesimal)}</Typography>
    },
    {
      id: 'acoes',
      header: 'Ações',
      cell: (info) => (
        <Button 
          variant="outlined" 
          color="error" 
          size="small" 
          onClick={() => handleDelete(info.row.original.id)}
          sx={{ minWidth: 'auto', p: 1 }}
        >
          <Trash2 size={16} />
        </Button>
      )
    }
  ], []);

  return (
    <Box className="animate-fade-in">
      <Box sx={{ mb: 4 }}>
        <Typography variant="h4" component="h1" gutterBottom sx={{ fontWeight: 'bold' }}>
          Gestão de Tempos (TPP)
        </Typography>
        <Typography variant="body1" color="text.secondary">
          Cadastre a matriz de tempos centesimais para operações.
        </Typography>
      </Box>

      <Grid container spacing={3}>
        
        {/* Formulário */}
        <Grid size={{ xs: 12, md: 4 }}>
          <Card variant="outlined" sx={{ p: 3, borderRadius: 2 }}>
            <Typography variant="h6" sx={{ mb: 3, display: 'flex', alignItems: 'center', gap: 1 }}>
              <AddIcon color="primary" /> Novo Registro
            </Typography>
            
            <form onSubmit={handleSubmit}>
              <Stack spacing={2.5}>
                <TextField
                  label="Índice (Folhas + Paradas)"
                  type="number"
                  variant="outlined"
                  fullWidth
                  required
                  slotProps={{ htmlInput: { min: "0" } }}
                  value={indice}
                  onChange={(e) => setIndice(e.target.value === '' ? '' : Number(e.target.value))}
                  placeholder="Ex: 3"
                />
                
                <FormControl fullWidth required>
                  <InputLabel id="dificuldade-label">Grau de Dificuldade</InputLabel>
                  <Select
                    labelId="dificuldade-label"
                    value={grauDificuldade}
                    label="Grau de Dificuldade"
                    onChange={e => setGrauDificuldade(e.target.value)}
                  >
                    <MenuItem value="MUITO_FACIL">Muito Fácil</MenuItem>
                    <MenuItem value="FACIL">Fácil</MenuItem>
                    <MenuItem value="MEDIO">Médio</MenuItem>
                    <MenuItem value="MEDIO_DIFICIL">Médio-Difícil</MenuItem>
                    <MenuItem value="DIFICIL">Difícil</MenuItem>
                  </Select>
                </FormControl>

                <FormControl fullWidth required>
                  <InputLabel id="comprimento-label">Comprimento da Costura</InputLabel>
                  <Select
                    labelId="comprimento-label"
                    value={faixaComprimento}
                    label="Comprimento da Costura"
                    onChange={e => setFaixaComprimento(e.target.value)}
                  >
                    <MenuItem value="DE_0_A_60">0 a 60 cm</MenuItem>
                    <MenuItem value="DE_61_A_90">61 a 90 cm</MenuItem>
                    <MenuItem value="ACIMA_DE_91">Acima de 91 cm</MenuItem>
                  </Select>
                </FormControl>

                <TextField
                  label="Tempo Centesimal (Minutos)"
                  type="number"
                  variant="outlined"
                  fullWidth
                  required
                  slotProps={{ htmlInput: { step: "0.01", min: "0" } }}
                  value={tempo}
                  onChange={(e) => setTempo(e.target.value)}
                  placeholder="Ex: 1.50 (1m 30s)"
                />

                <Button
                  type="submit"
                  variant="contained"
                  color="primary"
                  size="large"
                  disabled={isSubmitting}
                  startIcon={isSubmitting && <CircularProgress size={20} color="inherit" />}
                  disableElevation
                  fullWidth
                >
                  Cadastrar Tempo
                </Button>
              </Stack>
            </form>
          </Card>
        </Grid>

        {/* Tabela */}
        <Grid size={{ xs: 12, md: 8 }}>
          <Card variant="outlined" sx={{ borderRadius: 2, overflow: 'hidden' }}>
            <Box sx={{ p: 2.5, borderBottom: '1px solid', borderColor: 'divider', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
              <Typography variant="h6">Matriz Cadastrada</Typography>
              <Chip label={`${tempos.length} Registros`} color="primary" size="small" />
            </Box>
            
            {loading ? (
              <Box sx={{ p: 6, display: 'flex', justifyContent: 'center' }}>
                <CircularProgress />
              </Box>
            ) : error ? (
              <Box sx={{ p: 4, textAlign: 'center' }}>
                <Typography color="error">{error}</Typography>
              </Box>
            ) : tempos.length === 0 ? (
              <Box sx={{ p: 8, display: 'flex', flexDirection: 'column', alignItems: 'center', color: 'text.secondary' }}>
                <Typography gutterBottom>Nenhum tempo padrão cadastrado.</Typography>
                <Typography variant="caption">Utilize o formulário ao lado para iniciar a matriz.</Typography>
              </Box>
            ) : (
              <DataTable columns={columns} data={tempos} />
            )}
          </Card>
        </Grid>
      </Grid>
    </Box>
  );
};

export default TabelaTempos;
