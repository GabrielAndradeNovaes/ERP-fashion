import React, { useState, useEffect } from 'react';
import { Play, CheckCircle2, AlertCircle, Package } from 'lucide-react';
import api from '../api/axios';
import Modal from '../components/Modal';
import { useAuth } from '../contexts/AuthContext';
import {
  Box,
  Typography,
  Button,
  TextField,
  FormControl,
  InputLabel,
  Select,
  MenuItem,
  Stack,
  CircularProgress,
  IconButton
} from '@mui/material';
import AddIcon from '@mui/icons-material/Add';

interface ProdutoBase {
  id: string;
  codigo: string;
  nome: string;
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

const COLUMNS = [
  { id: 'CADASTRADA', title: 'Planejamento', color: 'var(--warning)', bgColor: 'rgba(245, 158, 11, 0.05)' },
  { id: 'CORTE', title: 'Corte', color: '#ec4899', bgColor: 'rgba(236, 72, 153, 0.05)' },
  { id: 'COSTURA', title: 'Costura', color: 'var(--accent-primary)', bgColor: 'rgba(99, 102, 241, 0.05)' },
  { id: 'FACCAO', title: 'Facção Externa', color: '#8b5cf6', bgColor: 'rgba(139, 92, 246, 0.05)' },
  { id: 'CONCLUIDA', title: 'Concluída', color: 'var(--success)', bgColor: 'rgba(16, 185, 129, 0.05)' }
];

const OrdensProducao = () => {
  const [ordens, setOrdens] = useState<OrdemProducao[]>([]);
  const [produtos, setProdutos] = useState<ProdutoBase[]>([]);
  const [loading, setLoading] = useState(true);
  
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [isGerarPacotesModalOpen, setIsGerarPacotesModalOpen] = useState(false);
  const [isGenerating, setIsGenerating] = useState(false);
  const [tamanhoPacote, setTamanhoPacote] = useState('20');
  const [selectedOrdem, setSelectedOrdem] = useState<OrdemProducao | null>(null);

  const [numero, setNumero] = useState('');
  const [produtoBaseId, setProdutoBaseId] = useState('');
  const [quantidade, setQuantidade] = useState('100');
  const [isSubmitting, setIsSubmitting] = useState(false);

  const { hasPermission } = useAuth();
  const canEdit = hasPermission('PCP_EDIT');

  // Drag and drop state
  const [draggedItem, setDraggedItem] = useState<string | null>(null);

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

  const handleDragStart = (e: React.DragEvent, id: string) => {
    setDraggedItem(id);
    e.dataTransfer.effectAllowed = 'move';
    // Transparent drag image offset
    e.dataTransfer.setDragImage(e.target as Element, 20, 20);
  };

  const handleDragOver = (e: React.DragEvent) => {
    e.preventDefault(); // Necessary to allow dropping
    e.dataTransfer.dropEffect = 'move';
  };

  const handleDrop = async (e: React.DragEvent, newStatus: string) => {
    e.preventDefault();
    if (!draggedItem || !canEdit) return;

    const opToMove = ordens.find(o => o.id === draggedItem);
    if (!opToMove || opToMove.status === newStatus) return;

    // Optimistic update
    const previousOrdens = [...ordens];
    setOrdens(prev => prev.map(o => o.id === draggedItem ? { ...o, status: newStatus } : o));

    try {
      await api.put(`/production/ordens/${draggedItem}/status`, { status: newStatus });
    } catch (err: any) {
      alert(err.response?.data?.message || 'Erro ao mover OP');
      // Revert on error
      setOrdens(previousOrdens);
    } finally {
      setDraggedItem(null);
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

  const getOrdensByStatus = (statusId: string) => {
    return ordens.filter(o => {
      // Map old status to new if exists
      let currentStatus = o.status;
      if (currentStatus === 'EM_ANDAMENTO') currentStatus = 'CORTE';
      return currentStatus === statusId;
    });
  };

  return (
    <Box className="animate-fade-in-up" sx={{ height: '100%', display: 'flex', flexDirection: 'column' }}>
      <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 4, flexShrink: 0 }}>
        <Box>
          <Typography variant="h4" component="h1" gutterBottom sx={{ fontWeight: 800, color: 'var(--text-primary)', letterSpacing: '-0.5px' }}>
            Kanban de <span className="text-gradient">Produção</span>
          </Typography>
          <Typography variant="body1" sx={{ color: 'var(--text-secondary)' }}>
            Arraste as ordens de produção (O.P.) entre as fases para atualizar seu status no chão de fábrica.
          </Typography>
        </Box>
        {canEdit && (
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
              boxShadow: '0 4px 14px 0 rgba(99, 102, 241, 0.39)'
            }}
          >
            Nova Ordem
          </Button>
        )}
      </Box>

      {loading ? (
        <Box sx={{ flexGrow: 1, display: 'flex', justifyContent: 'center', alignItems: 'center' }}>
          <CircularProgress sx={{ color: 'var(--accent-primary)' }} />
        </Box>
      ) : (
        <Box 
          sx={{ 
            display: 'flex', 
            gap: 3, 
            flexGrow: 1, 
            overflowX: 'auto', 
            pb: 2,
            '&::-webkit-scrollbar': { height: 8 },
            '&::-webkit-scrollbar-thumb': { bgcolor: 'rgba(255,255,255,0.1)', borderRadius: 4 }
          }}
        >
          {COLUMNS.map(col => {
            const columnOrdens = getOrdensByStatus(col.id);
            return (
              <Box 
                key={col.id}
                onDragOver={handleDragOver}
                onDrop={(e) => handleDrop(e, col.id)}
                sx={{
                  minWidth: 320,
                  maxWidth: 320,
                  bgcolor: 'rgba(20,20,20,0.4)',
                  border: '1px solid var(--border-color)',
                  borderRadius: 'var(--radius-md)',
                  display: 'flex',
                  flexDirection: 'column',
                  overflow: 'hidden'
                }}
              >
                <Box sx={{ p: 2, borderBottom: '1px solid var(--border-color)', bgcolor: col.bgColor, display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
                  <Typography sx={{ fontWeight: 700, color: col.color, fontSize: '0.9rem', textTransform: 'uppercase' }}>
                    {col.title}
                  </Typography>
                  <Typography sx={{ bgcolor: 'rgba(255,255,255,0.1)', px: 1, borderRadius: 2, fontSize: '0.75rem', fontWeight: 600 }}>
                    {columnOrdens.length}
                  </Typography>
                </Box>
                
                <Box sx={{ p: 2, flexGrow: 1, overflowY: 'auto', display: 'flex', flexDirection: 'column', gap: 2 }}>
                  {columnOrdens.map(op => (
                    <Box 
                      key={op.id}
                      draggable={canEdit}
                      onDragStart={(e) => handleDragStart(e, op.id)}
                      sx={{
                        bgcolor: 'var(--bg-card)',
                        p: 2,
                        borderRadius: 'var(--radius-sm)',
                        border: '1px solid var(--border-color)',
                        cursor: canEdit ? 'grab' : 'default',
                        boxShadow: '0 2px 4px rgba(0,0,0,0.1)',
                        transition: 'transform 0.2s',
                        '&:active': { cursor: 'grabbing', transform: 'scale(0.98)' },
                        opacity: draggedItem === op.id ? 0.5 : 1
                      }}
                    >
                      <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 1 }}>
                        <Typography sx={{ fontWeight: 700, color: 'var(--accent-primary)' }}>#{op.numero}</Typography>
                        <Typography sx={{ fontSize: '0.8rem', color: 'var(--text-muted)' }}>{op.quantidade} un</Typography>
                      </Box>
                      <Typography variant="body2" sx={{ fontWeight: 600, color: 'var(--text-primary)', mb: 0.5 }}>
                        {op.produtoBaseNome}
                      </Typography>
                      <Typography variant="caption" sx={{ color: 'var(--text-muted)' }}>
                        Ficha: {op.fichaTecnicaVersao}
                      </Typography>
                      
                      {canEdit && (op.status === 'CADASTRADA' || op.status === 'CORTE') && (
                        <Box sx={{ mt: 2, pt: 2, borderTop: '1px dashed var(--border-color)', display: 'flex', justifyContent: 'flex-end' }}>
                          <Button
                            size="small"
                            variant="outlined"
                            startIcon={<Package size={14} />}
                            onClick={() => {
                              setSelectedOrdem(op);
                              setIsGerarPacotesModalOpen(true);
                            }}
                            sx={{ fontSize: '0.7rem', textTransform: 'none', py: 0.5 }}
                          >
                            Gerar Pacotes
                          </Button>
                        </Box>
                      )}
                    </Box>
                  ))}
                </Box>
              </Box>
            );
          })}
        </Box>
      )}

      {/* Modals remain the same */}
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
