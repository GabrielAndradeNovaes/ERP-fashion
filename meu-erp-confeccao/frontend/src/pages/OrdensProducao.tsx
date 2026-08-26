import React, { useState, useEffect } from 'react';
import { Play, CheckCircle2, AlertCircle } from 'lucide-react';
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

  const [loading, setLoading] = useState(true);
  
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [isConfirmModalOpen, setIsConfirmModalOpen] = useState(false);
  const [selectedOrdemParaIniciar, setSelectedOrdemParaIniciar] = useState<OrdemProducao | null>(null);
  
  const [numero, setNumero] = useState('');
  const [produtoBaseId, setProdutoBaseId] = useState('');

  const [quantidade, setQuantidade] = useState('100');
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [isStarting, setIsStarting] = useState(false);
  const [isGerarPacotesModalOpen, setIsGerarPacotesModalOpen] = useState(false);
  const [isGenerating, setIsGenerating] = useState(false);
  const [tamanhoPacote, setTamanhoPacote] = useState('20');
  const [selectedOrdem, setSelectedOrdem] = useState<OrdemProducao | null>(null);

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

  const carregarProduto = (produtoId: string) => {
    setProdutoBaseId(produtoId);
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!numero || !produtoBaseId || !quantidade) return;

    try {
      setIsSubmitting(true);
      await api.post('/production/ordens', {
        numero,
        produtoBaseId,
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

  const openConfirmModal = (ordem: OrdemProducao) => {
    setSelectedOrdemParaIniciar(ordem);
    setIsConfirmModalOpen(true);
  };

  const handleIniciarProducao = async () => {
    if (!selectedOrdemParaIniciar) return;
    
    try {
      setIsStarting(true);
      await api.post(`/production/ordens/${selectedOrdemParaIniciar.id}/iniciar`);
      setIsConfirmModalOpen(false);
      setSelectedOrdemParaIniciar(null);
      fetchInitialData();
    } catch (err: any) {
      alert(err.response?.data?.message || 'Erro ao iniciar OP');
    } finally {
      setIsStarting(false);
    }
  };

  const handleGerarPacotes = async () => {
    if (!selectedOrdem) return;
    try {
      setIsGenerating(true);
      await api.post(`/production/ordens/${selectedOrdem.id}/gerar-pacotes?tamanhoPacote=${tamanhoPacote}`);
      alert('Pacotes e cupons gerados com sucesso!');
      setIsGerarPacotesModalOpen(false);
      setSelectedOrdem(null);
    } catch (err: any) {
      alert(err.response?.data?.message || 'Erro ao gerar pacotes');
    } finally {
      setIsGenerating(false);
    }
  };

  const columns: ColumnDef<any, any, any>[] = React.useMemo(() => [
    {
      accessorKey: 'numero',
      header: 'Nº OP',
      cell: (info) => <Typography sx={{ fontWeight: 700, color: 'var(--accent-primary)' }}>#{info.getValue() as string}</Typography>
    },
    {
      id: 'produto',
      header: 'Produto Final',
      cell: (info) => (
        <Box>
          <Typography variant="body2" sx={{ fontWeight: 600 }}>{info.row.original.produtoBaseNome}</Typography>
          <Typography variant="caption" sx={{ color: 'var(--text-muted)' }}>
            Ficha: {info.row.original.fichaTecnicaVersao}
          </Typography>
        </Box>
      )
    },
    {
      accessorKey: 'quantidade',
      header: 'Quantidade',
      cell: (info) => <Typography sx={{ fontWeight: 500 }}>{info.getValue() as number} un</Typography>
    },
    {
      accessorKey: 'status',
      header: 'Status',
      cell: (info) => {
        const status = info.getValue() as string;
        if (status === 'CADASTRADA') {
          return <Chip label="Planejada" sx={{ bgcolor: 'rgba(245, 158, 11, 0.1)', color: 'var(--warning)', fontWeight: 600 }} size="small" />;
        } else if (status === 'EM_ANDAMENTO') {
          return <Chip label="Em Produção" sx={{ bgcolor: 'rgba(99, 102, 241, 0.1)', color: 'var(--accent-primary)', fontWeight: 600 }} size="small" />;
        } else if (status === 'CONCLUIDA') {
          return <Chip label="Concluída" sx={{ bgcolor: 'rgba(16, 185, 129, 0.1)', color: 'var(--success)', fontWeight: 600 }} size="small" />;
        }
        return <Chip label={status} size="small" />;
      }
    },
    {
      id: 'acoes',
      header: 'Ações',
      cell: (info) => {
        return (
          <Stack direction="row" spacing={1}>
            {info.row.original.status === 'CADASTRADA' && (
              <Button
                variant="contained"
                size="small"
                startIcon={<Play size={14} />}
                onClick={() => openConfirmModal(info.row.original)}
                sx={{ 
                  bgcolor: 'var(--accent-primary)', 
                  '&:hover': { bgcolor: 'var(--accent-hover)' },
                  borderRadius: 'var(--radius-sm)',
                  textTransform: 'none',
                  fontWeight: 600
                }}
                disableElevation
              >
                Iniciar
              </Button>
            )}
            {(info.row.original.status === 'EM_ANDAMENTO' || info.row.original.status === 'CADASTRADA') && (
              <Button
                variant="outlined"
                size="small"
                onClick={() => {
                  setSelectedOrdem(info.row.original);
                  setIsGerarPacotesModalOpen(true);
                }}
                sx={{ 
                  borderColor: 'var(--accent-primary)', 
                  color: 'var(--accent-primary)',
                  borderRadius: 'var(--radius-sm)',
                  textTransform: 'none',
                  fontWeight: 600
                }}
              >
                Gerar Pacotes
              </Button>
            )}
          </Stack>
        );
      }
    }
  ], []);

  return (
    <Box className="animate-fade-in-up">
      <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 4 }}>
        <Box>
          <Typography variant="h4" component="h1" gutterBottom sx={{ fontWeight: 800, color: 'var(--text-primary)', letterSpacing: '-0.5px' }}>
            Ordens de <span className="text-gradient">Produção</span>
          </Typography>
          <Typography variant="body1" sx={{ color: 'var(--text-secondary)' }}>
            Acompanhe o status e despache ordens para o chão de fábrica.
          </Typography>
        </Box>
        <Button 
          variant="contained" 
          startIcon={<AddIcon />}
          onClick={() => setIsModalOpen(true)}
          size="large"
          sx={{
            background: 'var(--accent-gradient)',
            borderRadius: 'var(--radius-md)',
            textTransform: 'none',
            fontWeight: 600,
            boxShadow: '0 4px 14px 0 rgba(99, 102, 241, 0.39)',
            '&:hover': {
              boxShadow: '0 6px 20px rgba(99, 102, 241, 0.23)'
            }
          }}
        >
          Nova Ordem
        </Button>
      </Box>

      <div className="premium-card">
        <Box sx={{ p: 3, borderBottom: '1px solid', borderColor: 'var(--border-color)' }}>
          <Typography variant="h6" sx={{ fontWeight: 600, color: 'var(--text-primary)' }}>Controle de PCP</Typography>
        </Box>
        
        {loading ? (
          <Box sx={{ p: 6, display: 'flex', justifyContent: 'center' }}>
            <CircularProgress sx={{ color: 'var(--accent-primary)' }} />
          </Box>
        ) : ordens.length === 0 ? (
          <Box sx={{ p: 8, display: 'flex', flexDirection: 'column', alignItems: 'center', color: 'var(--text-muted)' }}>
            <Typography>Nenhuma ordem de produção encontrada.</Typography>
          </Box>
        ) : (
          <Box sx={{ 
            '& th': { bgcolor: 'transparent', color: 'var(--text-secondary)', fontWeight: 600 },
            '& td': { borderColor: 'var(--border-color)', color: 'var(--text-primary)' },
            '& tbody tr:hover': { bgcolor: 'rgba(255,255,255,0.02)' }
          }}>
            <DataTable columns={columns} data={ordens} />
          </Box>
        )}
      </div>

      <Modal isOpen={isModalOpen} onClose={() => setIsModalOpen(false)} title="Nova OP" width="500px">
        <div className="glass-panel" style={{ padding: '24px', background: 'var(--bg-card)', border: 'none', boxShadow: 'none' }}>
          <form onSubmit={handleSubmit}>
            <Stack spacing={3}>
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
                  onChange={e => carregarProduto(e.target.value)}
                >
                  <MenuItem value=""><em>Selecione...</em></MenuItem>
                  {produtos.map(p => (
                    <MenuItem key={p.id} value={p.id}>{p.codigo} - {p.nome}</MenuItem>
                  ))}
                </Select>
              </FormControl>

              <TextField
                label="Quantidade"
                type="number"
                variant="outlined"
                fullWidth
                required
                slotProps={{ htmlInput: { min: 1 } }}
                value={quantidade}
                onChange={e => setQuantidade(e.target.value)}
              />

              <Box sx={{ display: 'flex', justifyContent: 'flex-end', pt: 2, gap: 2 }}>
                <Button onClick={() => setIsModalOpen(false)} sx={{ color: 'var(--text-secondary)' }}>Cancelar</Button>
                <Button 
                  type="submit" 
                  variant="contained" 
                  disabled={isSubmitting}
                  startIcon={isSubmitting && <CircularProgress size={20} color="inherit" />}
                  sx={{ bgcolor: 'var(--accent-primary)', '&:hover': { bgcolor: 'var(--accent-hover)' } }}
                  disableElevation
                >
                  Criar OP
                </Button>
              </Box>
            </Stack>
          </form>
        </div>
      </Modal>

      {/* Modal de Confirmação (Checklist) */}
      <Modal isOpen={isConfirmModalOpen} onClose={() => !isStarting && setIsConfirmModalOpen(false)} title="Checklist de Início" width="550px">
        {selectedOrdemParaIniciar && (
          <div className="glass-panel" style={{ padding: '32px', background: 'var(--bg-card)', border: 'none', boxShadow: 'none' }}>
            <Box sx={{ display: 'flex', alignItems: 'center', gap: 2, mb: 3 }}>
              <Box sx={{ p: 2, borderRadius: '50%', bgcolor: 'rgba(16, 185, 129, 0.1)', color: 'var(--success)' }}>
                <CheckCircle2 size={32} />
              </Box>
              <Box>
                <Typography variant="h5" sx={{ fontWeight: 700, color: 'var(--text-primary)' }}>
                  Iniciar OP #{selectedOrdemParaIniciar.numero}
                </Typography>
                <Typography variant="body2" sx={{ color: 'var(--text-muted)' }}>
                  Produto: {selectedOrdemParaIniciar.produtoBaseNome} ({selectedOrdemParaIniciar.quantidade} unidades)
                </Typography>
              </Box>
            </Box>

            <Box sx={{ bgcolor: 'rgba(245, 158, 11, 0.05)', border: '1px solid rgba(245, 158, 11, 0.2)', p: 3, borderRadius: 'var(--radius-md)', mb: 4 }}>
              <Typography variant="subtitle1" sx={{ color: 'var(--warning)', fontWeight: 600, display: 'flex', alignItems: 'center', gap: 1, mb: 1 }}>
                <AlertCircle size={18} /> Atenção ao Estoque
              </Typography>
              <Typography variant="body2" sx={{ color: 'var(--text-secondary)' }}>
                Ao confirmar o início desta Ordem de Produção, o sistema irá deduzir <strong>imediatamente</strong> as quantidades de materiais exigidas pela Ficha Técnica correspondente.
                Esta ação <strong>não</strong> pode ser desfeita automaticamente.
              </Typography>
            </Box>

            <Box sx={{ display: 'flex', justifyContent: 'flex-end', gap: 2 }}>
              <Button 
                onClick={() => setIsConfirmModalOpen(false)} 
                disabled={isStarting}
                sx={{ color: 'var(--text-muted)', fontWeight: 600 }}
              >
                Voltar
              </Button>
              <Button 
                onClick={handleIniciarProducao}
                variant="contained" 
                disabled={isStarting}
                startIcon={isStarting ? <CircularProgress size={20} color="inherit" /> : <Play size={18} />}
                sx={{ 
                  background: 'var(--success)', 
                  '&:hover': { filter: 'brightness(1.1)' },
                  textTransform: 'none',
                  fontWeight: 600,
                  px: 4
                }}
                disableElevation
              >
                {isStarting ? 'Iniciando...' : 'Confirmar e Iniciar'}
              </Button>
            </Box>
          </div>
        )}
      </Modal>

      {/* Modal de Gerar Pacotes */}
      <Modal isOpen={isGerarPacotesModalOpen} onClose={() => !isGenerating && setIsGerarPacotesModalOpen(false)} title="Gerar Pacotes Físicos" width="500px">
        {selectedOrdem && (
          <div className="glass-panel" style={{ padding: '24px', background: 'var(--bg-card)', border: 'none', boxShadow: 'none' }}>
            <Typography variant="body2" sx={{ mb: 3, color: 'var(--text-secondary)' }}>
              Defina o tamanho do pacote para a OP #{selectedOrdem.numero}. O sistema irá gerar pacotes respeitando as grades de SKUs automaticamente.
            </Typography>
            <Stack spacing={3}>
              <TextField
                label="Tamanho do Pacote (Peças)"
                type="number"
                variant="outlined"
                fullWidth
                required
                slotProps={{ htmlInput: { min: 1, max: 200 } }}
                value={tamanhoPacote}
                onChange={e => setTamanhoPacote(e.target.value)}
              />
              <Box sx={{ display: 'flex', justifyContent: 'flex-end', pt: 2, gap: 2 }}>
                <Button onClick={() => setIsGerarPacotesModalOpen(false)} sx={{ color: 'var(--text-secondary)' }}>Cancelar</Button>
                <Button 
                  onClick={handleGerarPacotes}
                  variant="contained" 
                  disabled={isGenerating}
                  startIcon={isGenerating && <CircularProgress size={20} color="inherit" />}
                  sx={{ bgcolor: 'var(--accent-primary)', '&:hover': { bgcolor: 'var(--accent-hover)' } }}
                  disableElevation
                >
                  Confirmar e Gerar
                </Button>
              </Box>
            </Stack>
          </div>
        )}
      </Modal>

    </Box>
  );
};

export default OrdensProducao;
