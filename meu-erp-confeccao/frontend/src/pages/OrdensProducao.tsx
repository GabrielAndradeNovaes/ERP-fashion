import React, { useState, useEffect } from 'react';
import { Play, CheckCircle2, AlertCircle, Package, Edit, RotateCcw, ChevronRight } from 'lucide-react';
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
  IconButton,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Paper,
  Chip,
  Menu
} from '@mui/material';
import AddIcon from '@mui/icons-material/Add';
import MoreVertIcon from '@mui/icons-material/MoreVert';

interface ProdutoBase {
  id: string;
  codigo: string;
  nome: string;
}

interface OrdemProducao {
  id: string;
  numero: string;
  produtoBaseNome: string;
  produtoBaseId: string;
  fichaTecnicaVersao: string;
  quantidade: number;
  status: string;
  criadoEm: string;
}

const STATUS_COLORS: Record<string, { label: string, color: string, bgColor: string }> = {
  PENDENTE: { label: 'Pendente', color: 'var(--warning)', bgColor: 'rgba(245, 158, 11, 0.1)' },
  EM_ANDAMENTO: { label: 'Em Andamento', color: 'var(--accent-primary)', bgColor: 'rgba(99, 102, 241, 0.1)' },
  FACCAO: { label: 'Facção', color: '#8b5cf6', bgColor: 'rgba(139, 92, 246, 0.1)' },
  CONCLUIDA: { label: 'Concluída', color: 'var(--success)', bgColor: 'rgba(16, 185, 129, 0.1)' },
  CANCELADA: { label: 'Cancelada', color: 'var(--danger)', bgColor: 'rgba(239, 68, 68, 0.1)' }
};

const OrdensProducao = () => {
  const [ordens, setOrdens] = useState<OrdemProducao[]>([]);
  const [produtos, setProdutos] = useState<ProdutoBase[]>([]);
  const [loading, setLoading] = useState(true);
  
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [isGerarPacotesModalOpen, setIsGerarPacotesModalOpen] = useState(false);
  const [isGenerating, setIsGenerating] = useState(false);
  const [tamanhoPacote, setTamanhoPacote] = useState('20');
  
  // States for form
  const [selectedOrdem, setSelectedOrdem] = useState<OrdemProducao | null>(null);
  const [numero, setNumero] = useState('');
  const [produtoBaseId, setProdutoBaseId] = useState('');
  const [quantidade, setQuantidade] = useState('100');
  const [isSubmitting, setIsSubmitting] = useState(false);

  // States for Actions Menu
  const [anchorEl, setAnchorEl] = useState<null | HTMLElement>(null);
  const [menuOrdem, setMenuOrdem] = useState<OrdemProducao | null>(null);

  const { hasPermission } = useAuth();
  const canEdit = hasPermission('PCP_EDIT');

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

  const handleOpenNewModal = () => {
    setSelectedOrdem(null);
    setNumero('');
    setProdutoBaseId('');
    setQuantidade('100');
    setIsModalOpen(true);
  };

  const handleOpenEditModal = (op: OrdemProducao) => {
    setSelectedOrdem(op);
    setNumero(op.numero);
    setProdutoBaseId(op.produtoBaseId);
    setQuantidade(op.quantidade.toString());
    setIsModalOpen(true);
    handleCloseMenu();
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!numero || !produtoBaseId || !quantidade) return;

    try {
      setIsSubmitting(true);
      const payload = {
        numero,
        produtoBaseId,
        quantidade: parseInt(quantidade)
      };

      if (selectedOrdem) {
        await api.put(`/production/ordens/${selectedOrdem.id}`, payload);
      } else {
        await api.post('/production/ordens', payload);
      }
      
      setIsModalOpen(false);
      fetchInitialData();
    } catch (err: any) {
      alert(err.response?.data?.message || 'Erro ao salvar OP');
    } finally {
      setIsSubmitting(false);
    }
  };

  const handleAlterarStatus = async (opId: string, novoStatus: string) => {
    try {
      await api.put(`/production/ordens/${opId}/status`, { status: novoStatus });
      fetchInitialData();
    } catch (err: any) {
      alert(err.response?.data?.message || 'Erro ao alterar status');
    }
    handleCloseMenu();
  };

  const handleEstornar = async (opId: string) => {
    if (!window.confirm('Tem certeza que deseja estornar esta OP? Isso reverterá os materiais para o estoque e mudará o status para PENDENTE.')) return;
    
    try {
      await api.post(`/production/ordens/${opId}/estornar`);
      alert('Ordem estornada com sucesso.');
      fetchInitialData();
    } catch (err: any) {
      alert(err.response?.data?.message || 'Erro ao estornar OP');
    }
    handleCloseMenu();
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

  const handleOpenMenu = (event: React.MouseEvent<HTMLButtonElement>, op: OrdemProducao) => {
    setAnchorEl(event.currentTarget);
    setMenuOrdem(op);
  };

  const handleCloseMenu = () => {
    setAnchorEl(null);
    setMenuOrdem(null);
  };

  return (
    <Box className="animate-fade-in-up" sx={{ height: '100%', display: 'flex', flexDirection: 'column' }}>
      <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 4 }}>
        <Box>
          <Typography variant="h4" component="h1" gutterBottom sx={{ fontWeight: 800, color: 'var(--text-primary)', letterSpacing: '-0.5px' }}>
            Ordens de <span className="text-gradient">Produção</span>
          </Typography>
          <Typography variant="body1" sx={{ color: 'var(--text-secondary)' }}>
            Gerencie todas as ordens, acompanhe o status e realize edições ou estornos se necessário.
          </Typography>
        </Box>
        {canEdit && (
          <Button 
            variant="contained" 
            startIcon={<AddIcon />}
            onClick={handleOpenNewModal}
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
        <TableContainer component={Paper} sx={{ bgcolor: 'var(--bg-card)', backgroundImage: 'none', borderRadius: 'var(--radius-lg)' }}>
          <Table sx={{ minWidth: 650 }} aria-label="tabela de ordens">
            <TableHead>
              <TableRow sx={{ '& th': { borderBottom: '1px solid var(--border-color)', color: 'var(--text-secondary)', fontWeight: 600 } }}>
                <TableCell>Número</TableCell>
                <TableCell>Produto Base</TableCell>
                <TableCell align="right">Qtd</TableCell>
                <TableCell>Data de Criação</TableCell>
                <TableCell>Status</TableCell>
                <TableCell align="right">Ações</TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {ordens.map((op) => (
                <TableRow
                  key={op.id}
                  sx={{ '&:last-child td, &:last-child th': { border: 0 }, '& td': { borderBottom: '1px solid var(--border-color)' }, '&:hover': { bgcolor: 'rgba(255,255,255,0.02)' } }}
                >
                  <TableCell component="th" scope="row" sx={{ fontWeight: 700, color: 'var(--accent-primary)' }}>
                    #{op.numero}
                  </TableCell>
                  <TableCell>
                    <Typography variant="body2" sx={{ fontWeight: 600, color: 'var(--text-primary)' }}>
                      {op.produtoBaseNome}
                    </Typography>
                    <Typography variant="caption" sx={{ color: 'var(--text-muted)' }}>
                      Ficha: {op.fichaTecnicaVersao}
                    </Typography>
                  </TableCell>
                  <TableCell align="right" sx={{ color: 'var(--text-primary)' }}>{op.quantidade} un</TableCell>
                  <TableCell sx={{ color: 'var(--text-primary)' }}>{new Date(op.criadoEm).toLocaleDateString()}</TableCell>
                  <TableCell>
                    {STATUS_COLORS[op.status] ? (
                      <Chip 
                        label={STATUS_COLORS[op.status].label} 
                        size="small" 
                        sx={{ 
                          bgcolor: STATUS_COLORS[op.status].bgColor, 
                          color: STATUS_COLORS[op.status].color,
                          fontWeight: 600,
                          borderRadius: '6px'
                        }} 
                      />
                    ) : (
                      <Chip label={op.status} size="small" />
                    )}
                  </TableCell>
                  <TableCell align="right">
                    <IconButton onClick={(e) => handleOpenMenu(e, op)} sx={{ color: 'var(--text-secondary)' }}>
                      <MoreVertIcon />
                    </IconButton>
                  </TableCell>
                </TableRow>
              ))}
              {ordens.length === 0 && (
                <TableRow>
                  <TableCell colSpan={6} align="center" sx={{ py: 6, color: 'var(--text-muted)' }}>
                    Nenhuma ordem de produção cadastrada.
                  </TableCell>
                </TableRow>
              )}
            </TableBody>
          </Table>
        </TableContainer>
      )}

      {/* Menu de Ações */}
      <Menu
        anchorEl={anchorEl}
        open={Boolean(anchorEl)}
        onClose={handleCloseMenu}
        PaperProps={{
          sx: {
            bgcolor: 'var(--bg-card)',
            border: '1px solid var(--border-color)',
            backgroundImage: 'none',
            color: 'var(--text-primary)',
            boxShadow: '0 10px 30px rgba(0,0,0,0.5)',
            '& .MuiMenuItem-root': {
              fontSize: '0.875rem',
              gap: 1.5,
              py: 1.5
            }
          }
        }}
      >
        {menuOrdem?.status === 'PENDENTE' && canEdit && [
          <MenuItem key="edit" onClick={() => handleOpenEditModal(menuOrdem)}>
            <Edit size={16} /> Editar OP
          </MenuItem>,
          <MenuItem key="start" onClick={() => handleAlterarStatus(menuOrdem.id, 'EM_ANDAMENTO')}>
            <Play size={16} color="var(--success)" /> Iniciar Produção
          </MenuItem>
        ]}
        
        {menuOrdem?.status === 'EM_ANDAMENTO' && canEdit && [
          <MenuItem key="faccao" onClick={() => handleAlterarStatus(menuOrdem.id, 'FACCAO')}>
            <ChevronRight size={16} /> Enviar p/ Facção
          </MenuItem>,
          <MenuItem key="concluir" onClick={() => handleAlterarStatus(menuOrdem.id, 'CONCLUIDA')}>
            <CheckCircle2 size={16} color="var(--success)" /> Concluir Produção
          </MenuItem>,
          <MenuItem key="pacotes" onClick={() => {
            setSelectedOrdem(menuOrdem);
            setIsGerarPacotesModalOpen(true);
            handleCloseMenu();
          }}>
            <Package size={16} color="var(--accent-primary)" /> Gerar Pacotes Físicos
          </MenuItem>
        ]}

        {menuOrdem?.status === 'FACCAO' && canEdit && [
           <MenuItem key="concluir_faccao" onClick={() => handleAlterarStatus(menuOrdem.id, 'CONCLUIDA')}>
            <CheckCircle2 size={16} color="var(--success)" /> Concluir Produção
          </MenuItem>
        ]}

        {(menuOrdem?.status === 'EM_ANDAMENTO' || menuOrdem?.status === 'FACCAO' || menuOrdem?.status === 'CONCLUIDA') && canEdit && (
          <MenuItem onClick={() => handleEstornar(menuOrdem.id)} sx={{ color: 'var(--danger) !important' }}>
            <RotateCcw size={16} /> Estornar para Pendente
          </MenuItem>
        )}
      </Menu>

      {/* Modal de Criar/Editar */}
      <Modal isOpen={isModalOpen} onClose={() => setIsModalOpen(false)} title={selectedOrdem ? 'Editar OP' : 'Nova OP'} width="500px">
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
                  onChange={e => setProdutoBaseId(e.target.value)}
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
                  {selectedOrdem ? 'Salvar Alterações' : 'Criar OP'}
                </Button>
              </Box>
            </Stack>
          </form>
        </div>
      </Modal>

      {/* Modal Gerar Pacotes */}
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
