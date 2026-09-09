import React, { useState, useEffect } from 'react';
import { Play, CheckCircle2, AlertCircle, Package, Edit, RotateCcw, ChevronRight, Eye } from 'lucide-react';
import api from '../api/axios';
import Modal from '../components/Modal';
import PageHeader from '../components/PageHeader';
import PremiumCard from '../components/PremiumCard';
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
  Menu,
  LinearProgress,
  Grid,
  Drawer,
  Tabs,
  Tab,
  Divider
} from '@mui/material';
import AddIcon from '@mui/icons-material/Add';
import MoreVertIcon from '@mui/icons-material/MoreVert';

interface ProdutoSku {
  id: string;
  tamanho: string;
  cor: string;
}

interface ProdutoBase {
  id: string;
  codigo: string;
  nome: string;
  skus?: ProdutoSku[];
}

interface OrdemProducaoItem {
  id: string;
  produtoSkuId: string;
  produtoSkuCor: string;
  produtoSkuTamanho: string;
  produtoSkuCodigoBarras: string;
  quantidade: number;
}

interface Cupom {
  id: string;
  ordemProducaoNumero: string;
  pacoteSequencial: number;
  operacaoNome: string;
  codigoBarras: string;
  tempoTotalCentesimal: number;
  quantidadePecas: number;
  status: string;
  pacoteCodigoBarras: string;
  produtoNome: string;
}

interface OrdemProducao {
  id: string;
  numero: string;
  produtoBaseNome: string;
  produtoBaseId: string;
  fichaTecnicaVersao: string;
  quantidade: number;
  quantidadeProduzida: number;
  status: string;
  criadoEm: string;
  itens?: OrdemProducaoItem[];
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
  
  const [isDetailsOpen, setIsDetailsOpen] = useState(false);
  const [activeTab, setActiveTab] = useState(0);
  const [opCupons, setOpCupons] = useState<Cupom[]>([]);
  const [loadingCupons, setLoadingCupons] = useState(false);

  const [isModalOpen, setIsModalOpen] = useState(false);
  const [isGerarPacotesModalOpen, setIsGerarPacotesModalOpen] = useState(false);
  const [isGenerating, setIsGenerating] = useState(false);
  const [tamanhoPacote, setTamanhoPacote] = useState('20');
  
  // States for form
  const [selectedOrdem, setSelectedOrdem] = useState<OrdemProducao | null>(null);
  const [numero, setNumero] = useState('');
  const [produtoBaseId, setProdutoBaseId] = useState('');
  const [quantidade, setQuantidade] = useState('100');
  const [skuQuantities, setSkuQuantities] = useState<Record<string, number>>({});
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

  const handleOpenDetails = (op: OrdemProducao) => {
    setSelectedOrdem(op);
    setIsDetailsOpen(true);
    setActiveTab(0);
    handleCloseMenu();
    fetchCupons(op.id);
  };

  const fetchCupons = async (opId: string) => {
    try {
      setLoadingCupons(true);
      const res = await api.get(`/production/cupons/ordem/${opId}`);
      setOpCupons(res.data);
    } catch (err) {
      console.error(err);
    } finally {
      setLoadingCupons(false);
    }
  };

  const handleOpenNewModal = () => {
    setSelectedOrdem(null);
    setNumero('');
    setProdutoBaseId('');
    setQuantidade('0');
    setSkuQuantities({});
    setIsModalOpen(true);
  };

  const handleOpenEditModal = (op: OrdemProducao) => {
    setSelectedOrdem(op);
    setNumero(op.numero);
    setProdutoBaseId(op.produtoBaseId);
    setQuantidade(op.quantidade.toString());
    const initialSkus: Record<string, number> = {};
    if (op.itens) {
      op.itens.forEach(item => {
        initialSkus[item.produtoSkuId] = item.quantidade;
      });
    }
    setSkuQuantities(initialSkus);
    setIsModalOpen(true);
    handleCloseMenu();
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!numero || !produtoBaseId) return;

    const totalQuantidade = Object.values(skuQuantities).reduce((a, b) => a + (Number(b) || 0), 0);
    if (totalQuantidade <= 0) {
      alert('Informe a quantidade de pelo menos um tamanho/cor.');
      return;
    }

    try {
      setIsSubmitting(true);
      const payload = {
        numero,
        produtoBaseId,
        quantidade: totalQuantidade,
        itens: Object.entries(skuQuantities)
          .filter(([_, qtd]) => qtd > 0)
          .map(([skuId, qtd]) => ({ produtoSkuId: skuId, quantidade: Number(qtd) }))
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
      <PageHeader 
        title="Ordens de Produção"
        subtitle="Gerencie todas as ordens, acompanhe o status e realize edições ou estornos se necessário."
        icon={<Package size={28} />}
        action={
          canEdit && (
            <Button 
              variant="contained" 
              startIcon={<AddIcon />}
              onClick={handleOpenNewModal}
              size="large"
              disableElevation
            >
              Nova Ordem
            </Button>
          )
        }
      />

      <PremiumCard>
        {loading ? (
          <Box sx={{ p: 6, display: 'flex', justifyContent: 'center', alignItems: 'center' }}>
            <CircularProgress sx={{ color: 'var(--accent-primary)' }} />
          </Box>
        ) : (
          <TableContainer>
          <Table sx={{ minWidth: 650 }} aria-label="tabela de ordens">
            <TableHead>
              <TableRow sx={{ '& th': { borderBottom: '1px solid var(--border-color)', color: 'var(--text-secondary)', fontWeight: 600 } }}>
                <TableCell>Número</TableCell>
                <TableCell>Produto Base</TableCell>
                <TableCell align="center">Andamento (Peças)</TableCell>
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
                  <TableCell align="center" sx={{ color: 'var(--text-primary)', minWidth: 150 }}>
                    <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 1 }}>
                      <Typography variant="caption" sx={{ fontWeight: 600 }}>{op.quantidadeProduzida || 0} prod.</Typography>
                      <Typography variant="caption" sx={{ color: 'var(--text-secondary)' }}>{op.quantidade} total</Typography>
                    </Box>
                    <LinearProgress 
                      variant="determinate" 
                      value={op.quantidade > 0 ? Math.min(100, ((op.quantidadeProduzida || 0) / op.quantidade) * 100) : 0} 
                      sx={{ 
                        height: 8, 
                        borderRadius: 4,
                        bgcolor: 'rgba(0,0,0,0.05)',
                        '& .MuiLinearProgress-bar': {
                          borderRadius: 4,
                          backgroundImage: 'var(--accent-gradient)'
                        }
                      }} 
                    />
                  </TableCell>
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
      </PremiumCard>

      <Menu
        anchorEl={anchorEl}
        open={Boolean(anchorEl)}
        onClose={handleCloseMenu}
        sx={{
          '& .MuiPaper-root': {
            bgcolor: 'var(--bg-card)',
            border: '1px solid var(--border-color)',
            backgroundImage: 'none',
            color: 'var(--text-primary)',
            boxShadow: '0 10px 30px rgba(0,0,0,0.5)'
          },
          '& .MuiMenuItem-root': {
            fontSize: '0.875rem',
            gap: 1.5,
            py: 1.5
          }
        }}
      >
        <MenuItem onClick={() => handleOpenDetails(menuOrdem!)}>
          <Eye size={16} color="var(--accent-primary)" /> Ver Detalhes
        </MenuItem>

        {menuOrdem?.status === 'PENDENTE' && canEdit && (
          <MenuItem onClick={() => handleOpenEditModal(menuOrdem)}>
            <Edit size={16} /> Editar OP
          </MenuItem>
        )}
        {menuOrdem?.status === 'PENDENTE' && canEdit && (
          <MenuItem onClick={() => handleAlterarStatus(menuOrdem.id, 'EM_ANDAMENTO')}>
            <Play size={16} color="var(--success)" /> Iniciar Produção
          </MenuItem>
        )}
        
        {menuOrdem?.status === 'EM_ANDAMENTO' && canEdit && (
          <MenuItem onClick={() => handleAlterarStatus(menuOrdem.id, 'FACCAO')}>
            <ChevronRight size={16} /> Enviar p/ Facção
          </MenuItem>
        )}
        {menuOrdem?.status === 'EM_ANDAMENTO' && canEdit && (
          <MenuItem onClick={() => handleAlterarStatus(menuOrdem.id, 'CONCLUIDA')}>
            <CheckCircle2 size={16} color="var(--success)" /> Concluir Produção
          </MenuItem>
        )}
        {menuOrdem?.status === 'EM_ANDAMENTO' && canEdit && (
          <MenuItem onClick={() => {
            setSelectedOrdem(menuOrdem);
            setIsGerarPacotesModalOpen(true);
            handleCloseMenu();
          }}>
            <Package size={16} color="var(--accent-primary)" /> Gerar Pacotes Físicos
          </MenuItem>
        )}

        {menuOrdem?.status === 'FACCAO' && canEdit && (
           <MenuItem onClick={() => handleAlterarStatus(menuOrdem.id, 'CONCLUIDA')}>
            <CheckCircle2 size={16} color="var(--success)" /> Concluir Produção
          </MenuItem>
        )}

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
                  onChange={e => {
                    setProdutoBaseId(e.target.value);
                    setSkuQuantities({});
                  }}
                >
                  <MenuItem value=""><em>Selecione...</em></MenuItem>
                  {produtos.map(p => (
                    <MenuItem key={p.id} value={p.id}>{p.codigo} - {p.nome}</MenuItem>
                  ))}
                </Select>
              </FormControl>

              {produtoBaseId && (
                <Box>
                  <Typography variant="subtitle2" sx={{ mb: 1, color: 'var(--text-secondary)' }}>Grade do Produto (Qtd. por SKU)</Typography>
                  <Grid container spacing={2}>
                    {produtos.find(p => p.id === produtoBaseId)?.skus?.map(sku => (
                      <Grid size={{ xs: 6, sm: 4 }} key={sku.id}>
                        <TextField
                          label={`${sku.cor} - ${sku.tamanho}`}
                          type="number"
                          variant="outlined"
                          fullWidth
                          size="small"
                          slotProps={{ htmlInput: { min: 0 } }}
                          value={skuQuantities[sku.id] || ''}
                          onChange={e => {
                            const val = e.target.value;
                            setSkuQuantities(prev => ({ ...prev, [sku.id]: val === '' ? 0 : parseInt(val) }));
                          }}
                        />
                      </Grid>
                    ))}
                    {(!produtos.find(p => p.id === produtoBaseId)?.skus || produtos.find(p => p.id === produtoBaseId)?.skus?.length === 0) && (
                      <Grid size={{ xs: 12 }}>
                        <Typography variant="body2" color="error">Este produto não possui grade (SKUs) cadastrada. Crie a grade antes de gerar a OP.</Typography>
                      </Grid>
                    )}
                  </Grid>
                </Box>
              )}

              <TextField
                label="Quantidade Total"
                type="number"
                variant="outlined"
                fullWidth
                disabled
                value={Object.values(skuQuantities).reduce((a, b) => a + (Number(b) || 0), 0)}
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
      {/* Drawer de Detalhes da OP */}
      {/* @ts-ignore - MUI Drawer Types in this version are complaining but it works */}
      <Drawer
        anchor="right"
        open={isDetailsOpen}
        onClose={() => setIsDetailsOpen(false)}
        PaperProps={{ sx: { width: { xs: '100%', md: '600px' }, bgcolor: 'var(--bg-default)', borderLeft: '1px solid var(--border-color)' } } as any}
      >
        {selectedOrdem && (
          <Box sx={{ display: 'flex', flexDirection: 'column', height: '100%' }}>
            <Box sx={{ p: 3, borderBottom: '1px solid var(--border-color)', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
              <Typography variant="h6" sx={{ color: 'var(--text-primary)', fontWeight: 600 }}>
                Detalhes da OP #{selectedOrdem.numero}
              </Typography>
              <Chip 
                label={STATUS_COLORS[selectedOrdem.status]?.label || selectedOrdem.status}
                size="small"
                sx={{
                  bgcolor: STATUS_COLORS[selectedOrdem.status]?.bgColor || 'transparent',
                  color: STATUS_COLORS[selectedOrdem.status]?.color || 'var(--text-primary)',
                  fontWeight: 600
                }}
              />
            </Box>
            
            <Tabs value={activeTab} onChange={(_, v) => setActiveTab(v)} sx={{ borderBottom: '1px solid var(--border-color)', px: 2 }}>
              <Tab label="Resumo" />
              <Tab label="Grade (SKUs)" />
              <Tab label="Rastreamento (Pacotes)" />
            </Tabs>
            
            <Box sx={{ p: 3, overflowY: 'auto', flex: 1 }}>
              {activeTab === 0 && (
                <Stack spacing={3}>
                  <Box>
                    <Typography variant="caption" sx={{ color: 'var(--text-muted)' }}>Produto Base</Typography>
                    <Typography variant="body1" sx={{ color: 'var(--text-primary)', fontWeight: 500 }}>{selectedOrdem.produtoBaseNome}</Typography>
                  </Box>
                  <Box>
                    <Typography variant="caption" sx={{ color: 'var(--text-muted)' }}>Ficha Técnica</Typography>
                    <Typography variant="body1" sx={{ color: 'var(--text-primary)' }}>Versão {selectedOrdem.fichaTecnicaVersao}</Typography>
                  </Box>
                  <Box sx={{ display: 'flex', gap: 4 }}>
                    <Box>
                      <Typography variant="caption" sx={{ color: 'var(--text-muted)' }}>Qtd. Solicitada</Typography>
                      <Typography variant="h6" sx={{ color: 'var(--text-primary)' }}>{selectedOrdem.quantidade} un</Typography>
                    </Box>
                    <Box>
                      <Typography variant="caption" sx={{ color: 'var(--text-muted)' }}>Qtd. Produzida</Typography>
                      <Typography variant="h6" sx={{ color: 'var(--success)' }}>{selectedOrdem.quantidadeProduzida || 0} un</Typography>
                    </Box>
                  </Box>
                  <Box>
                    <Typography variant="caption" sx={{ color: 'var(--text-muted)' }}>Progresso</Typography>
                    <Box sx={{ display: 'flex', alignItems: 'center', mt: 1 }}>
                      <Box sx={{ flex: 1, mr: 2 }}>
                        <LinearProgress 
                          variant="determinate" 
                          value={selectedOrdem.quantidade > 0 ? ((selectedOrdem.quantidadeProduzida || 0) / selectedOrdem.quantidade) * 100 : 0} 
                          sx={{ height: 8, borderRadius: 4, bgcolor: 'var(--border-color)', '& .MuiLinearProgress-bar': { bgcolor: 'var(--success)' } }} 
                        />
                      </Box>
                      <Typography variant="body2" sx={{ color: 'var(--text-secondary)' }}>
                        {selectedOrdem.quantidade > 0 ? Math.round(((selectedOrdem.quantidadeProduzida || 0) / selectedOrdem.quantidade) * 100) : 0}%
                      </Typography>
                    </Box>
                  </Box>
                </Stack>
              )}
              
              {activeTab === 1 && (
                <TableContainer component={Paper} elevation={0} sx={{ border: '1px solid var(--border-color)', background: 'transparent' }}>
                  <Table size="small">
                    <TableHead>
                      <TableRow sx={{ bgcolor: 'rgba(255,255,255,0.02)' }}>
                        <TableCell>Cor</TableCell>
                        <TableCell>Tamanho</TableCell>
                        <TableCell align="right">Qtd.</TableCell>
                      </TableRow>
                    </TableHead>
                    <TableBody>
                      {selectedOrdem.itens && selectedOrdem.itens.length > 0 ? selectedOrdem.itens.map(item => (
                        <TableRow key={item.id}>
                          <TableCell>{item.produtoSkuCor || '-'}</TableCell>
                          <TableCell>{item.produtoSkuTamanho || '-'}</TableCell>
                          <TableCell align="right" sx={{ fontWeight: 500 }}>{item.quantidade}</TableCell>
                        </TableRow>
                      )) : (
                        <TableRow>
                          <TableCell colSpan={3} align="center" sx={{ py: 3, color: 'var(--text-muted)' }}>Nenhuma grade detalhada para esta OP.</TableCell>
                        </TableRow>
                      )}
                    </TableBody>
                  </Table>
                </TableContainer>
              )}
              
              {activeTab === 2 && (
                <Box>
                  {loadingCupons ? (
                    <Box sx={{ display: 'flex', justifyContent: 'center', p: 4 }}><CircularProgress size={32} sx={{ color: 'var(--accent-primary)' }} /></Box>
                  ) : opCupons.length === 0 ? (
                    <Box sx={{ textAlign: 'center', p: 4, color: 'var(--text-muted)' }}>
                      <Package size={48} style={{ opacity: 0.2, margin: '0 auto 16px' }} />
                      <Typography>Nenhum pacote/cupom gerado ainda.</Typography>
                    </Box>
                  ) : (
                    <TableContainer component={Paper} elevation={0} sx={{ border: '1px solid var(--border-color)', background: 'transparent' }}>
                      <Table size="small">
                        <TableHead>
                          <TableRow sx={{ bgcolor: 'rgba(255,255,255,0.02)' }}>
                            <TableCell>Pacote</TableCell>
                            <TableCell>Operação</TableCell>
                            <TableCell align="right">Qtd.</TableCell>
                            <TableCell>Status</TableCell>
                          </TableRow>
                        </TableHead>
                        <TableBody>
                          {opCupons.map(c => (
                            <TableRow key={c.id}>
                              <TableCell sx={{ fontSize: '0.85rem' }}>Pkt {c.pacoteSequencial}</TableCell>
                              <TableCell sx={{ fontSize: '0.85rem' }}>{c.operacaoNome}</TableCell>
                              <TableCell align="right" sx={{ fontSize: '0.85rem' }}>{c.quantidadePecas}</TableCell>
                              <TableCell>
                                <Chip 
                                  label={c.status === 'LIDO' ? 'Baixado' : 'Pendente'} 
                                  size="small" 
                                  sx={{ 
                                    height: 20, 
                                    fontSize: '0.7rem', 
                                    bgcolor: c.status === 'LIDO' ? 'rgba(16, 185, 129, 0.1)' : 'rgba(245, 158, 11, 0.1)',
                                    color: c.status === 'LIDO' ? 'var(--success)' : 'var(--warning)'
                                  }} 
                                />
                              </TableCell>
                            </TableRow>
                          ))}
                        </TableBody>
                      </Table>
                    </TableContainer>
                  )}
                </Box>
              )}
            </Box>
          </Box>
        )}
      </Drawer>
    </Box>
  );
};

export default OrdensProducao;
