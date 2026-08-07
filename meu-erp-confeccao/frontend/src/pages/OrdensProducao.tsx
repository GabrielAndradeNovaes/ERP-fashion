import React, { useState, useEffect } from 'react';
import { Play } from 'lucide-react';
import api from '../api/axios';
import Modal from '../components/Modal';
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
} from '@mui/material';
import AddIcon from '@mui/icons-material/Add';
import type { ColumnDef } from '@tanstack/react-table';
import { DataTable } from '../components/DataTable';

interface ProdutoBase {
  id: string;
  codigo: string;
  nome: string;
}

interface FichaTecnica {
  id: string;
  descricao: string;
}

interface OrdemProducao {
  id: string;
  numero: string;
  produtoBaseNome: string;
  fichaTecnicaVersao: string;
  quantidade: number;
  status: string;
  criadoEm: string;
}

const OrdensProducao = () => {
  const [ordens, setOrdens] = useState<OrdemProducao[]>([]);
  const [produtos, setProdutos] = useState<ProdutoBase[]>([]);
  const [fichas, setFichas] = useState<FichaTecnica[]>([]);
  const [loading, setLoading] = useState(true);
  
  const [isModalOpen, setIsModalOpen] = useState(false);
  
  const [numero, setNumero] = useState('');
  const [produtoBaseId, setProdutoBaseId] = useState('');
  const [fichaTecnicaId, setFichaTecnicaId] = useState('');
  const [quantidade, setQuantidade] = useState('100');
  const [isSubmitting, setIsSubmitting] = useState(false);

  useEffect(() => {
    fetchInitialData();
  }, []);

  const fetchInitialData = async () => {
    try {
      setLoading(true);
      const [ordensRes, produtosRes] = await Promise.all([
        api.get('/production/ordens'),
        api.get('/catalog/produtos')
      ]);
      setOrdens(ordensRes.data);
      setProdutos(produtosRes.data);
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const carregarFichas = async (produtoId: string) => {
    setProdutoBaseId(produtoId);
    setFichaTecnicaId('');
    if (!produtoId) {
      setFichas([]);
      return;
    }
    try {
      const res = await api.get(`/production/fichas-tecnicas/produto/${produtoId}`);
      setFichas(res.data);
    } catch (err) {
      console.error(err);
    }
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!numero || !produtoBaseId || !fichaTecnicaId || !quantidade) return;

    try {
      setIsSubmitting(true);
      await api.post('/production/ordens', {
        numero,
        produtoBaseId,
        fichaTecnicaId,
        quantidade: parseInt(quantidade)
      });
      setNumero('');
      setIsModalOpen(false);
      fetchInitialData();
    } catch (err: any) {
      alert(err.response?.data?.message || 'Erro ao criar OP');
    } finally {
      setIsSubmitting(false);
    }
  };

  const handleIniciarProducao = async (id: string) => {
    if (!window.confirm("Iniciar a produção irá realizar a baixa dos materiais no estoque. Confirmar?")) return;
    
    try {
      await api.put(`/production/ordens/${id}/iniciar`);
      fetchInitialData();
      alert("Ordem de Produção iniciada e materiais deduzidos com sucesso!");
    } catch (err: any) {
      alert(err.response?.data?.message || 'Erro ao iniciar OP');
    }
  };

  const columns: ColumnDef<OrdemProducao, any, any>[] = React.useMemo(() => [
    {
      accessorKey: 'numero',
      header: 'Nº',
      cell: (info) => <Typography fontWeight={600}>{info.getValue()}</Typography>
    },
    {
      id: 'produto',
      header: 'Produto',
      cell: (info) => (
        <Box>
          <Typography variant="body2">{info.row.original.produtoBaseNome}</Typography>
          <Typography variant="caption" color="text.secondary">
            BOM: {info.row.original.fichaTecnicaVersao}
          </Typography>
        </Box>
      )
    },
    {
      accessorKey: 'quantidade',
      header: 'Quantidade'
    },
    {
      accessorKey: 'status',
      header: 'Status',
      cell: (info) => {
        const status = info.getValue() as string;
        let color: 'default' | 'primary' | 'success' | 'warning' = 'default';
        let label = status;
        if (status === 'CADASTRADA') {
          color = 'warning';
          label = 'Cadastrada';
        } else if (status === 'EM_ANDAMENTO') {
          color = 'primary';
          label = 'Em Andamento';
        } else if (status === 'CONCLUIDA') {
          color = 'success';
          label = 'Concluída';
        }
        return <Chip label={label} color={color} size="small" />;
      }
    },
    {
      id: 'acoes',
      header: 'Ações',
      cell: (info) => {
        if (info.row.original.status === 'CADASTRADA') {
          return (
            <Button
              variant="contained"
              size="small"
              startIcon={<Play size={14} />}
              onClick={() => handleIniciarProducao(info.row.original.id)}
              disableElevation
            >
              Iniciar Produção
            </Button>
          );
        }
        return null;
      }
    }
  ], []);

  return (
    <Box className="animate-fade-in">
      <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 4 }}>
        <Box>
          <Typography variant="h4" component="h1" gutterBottom fontWeight="bold">
            Ordens de Produção (PCP)
          </Typography>
          <Typography variant="body1" color="text.secondary">
            Gerencie as OPs e baixe materiais automaticamente.
          </Typography>
        </Box>
        <Button 
          variant="contained" 
          color="primary" 
          startIcon={<AddIcon />}
          onClick={() => setIsModalOpen(true)}
          size="large"
          disableElevation
        >
          Nova OP
        </Button>
      </Box>

      <Card variant="outlined" sx={{ borderRadius: 2, overflow: 'hidden' }}>
        <Box sx={{ p: 2.5, borderBottom: '1px solid', borderColor: 'divider' }}>
          <Typography variant="h6">Ordens Cadastradas</Typography>
        </Box>
        
        {loading ? (
          <Box sx={{ p: 6, display: 'flex', justifyContent: 'center' }}>
            <CircularProgress />
          </Box>
        ) : ordens.length === 0 ? (
          <Box sx={{ p: 8, display: 'flex', flexDirection: 'column', alignItems: 'center', color: 'text.secondary' }}>
            <Typography>Nenhuma ordem de produção encontrada.</Typography>
          </Box>
        ) : (
          <DataTable columns={columns} data={ordens} />
        )}
      </Card>

      <Modal isOpen={isModalOpen} onClose={() => setIsModalOpen(false)} title="Nova OP" width="500px">
        <form onSubmit={handleSubmit}>
          <Stack spacing={3} sx={{ mt: 1 }}>
            <TextField
              label="Número da OP"
              variant="outlined"
              fullWidth
              required
              value={numero}
              onChange={e => setNumero(e.target.value)}
              placeholder="OP-001"
            />
            
            <FormControl fullWidth required>
              <InputLabel id="produto-label">Produto Base</InputLabel>
              <Select
                labelId="produto-label"
                value={produtoBaseId}
                label="Produto Base"
                onChange={e => carregarFichas(e.target.value)}
              >
                <MenuItem value=""><em>Selecione...</em></MenuItem>
                {produtos.map(p => (
                  <MenuItem key={p.id} value={p.id}>{p.codigo} - {p.nome}</MenuItem>
                ))}
              </Select>
            </FormControl>

            <FormControl fullWidth required disabled={!produtoBaseId}>
              <InputLabel id="ficha-label">Ficha Técnica (BOM)</InputLabel>
              <Select
                labelId="ficha-label"
                value={fichaTecnicaId}
                label="Ficha Técnica (BOM)"
                onChange={e => setFichaTecnicaId(e.target.value)}
              >
                <MenuItem value=""><em>Selecione...</em></MenuItem>
                {fichas.map(f => (
                  <MenuItem key={f.id} value={f.id}>{f.descricao}</MenuItem>
                ))}
              </Select>
            </FormControl>

            <TextField
              label="Quantidade"
              type="number"
              variant="outlined"
              fullWidth
              required
              inputProps={{ min: 1 }}
              value={quantidade}
              onChange={e => setQuantidade(e.target.value)}
            />

            <Box sx={{ display: 'flex', justifyContent: 'flex-end', pt: 2 }}>
              <Button onClick={() => setIsModalOpen(false)} sx={{ mr: 2 }}>Cancelar</Button>
              <Button 
                type="submit" 
                variant="contained" 
                color="primary"
                disabled={isSubmitting}
                startIcon={isSubmitting && <CircularProgress size={20} color="inherit" />}
                disableElevation
              >
                Criar OP
              </Button>
            </Box>
          </Stack>
        </form>
      </Modal>

    </Box>
  );
};

export default OrdensProducao;
