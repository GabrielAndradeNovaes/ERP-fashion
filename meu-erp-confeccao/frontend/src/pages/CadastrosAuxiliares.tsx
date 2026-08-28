import React, { useState, useEffect } from 'react';
import {
  Box,
  Typography,
  Tabs,
  Tab,
  Button,
  TextField,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Chip,
  IconButton
} from '@mui/material';
import { Edit2, Trash2, Plus } from 'lucide-react';
import api from '../api/axios';
import Modal from '../components/Modal';

interface Cor {
  id: string;
  nome: string;
  codigoHex?: string;
  ativo: boolean;
}

interface Tamanho {
  id: string;
  nome: string;
  sigla?: string;
  ativo: boolean;
}

interface UnidadeMedida {
  id: string;
  nome: string;
  sigla: string;
  ativo: boolean;
}

const CadastrosAuxiliares = () => {
  const [activeTab, setActiveTab] = useState(0);
  
  const [cores, setCores] = useState<Cor[]>([]);
  const [tamanhos, setTamanhos] = useState<Tamanho[]>([]);
  const [unidades, setUnidades] = useState<UnidadeMedida[]>([]);
  
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [editingItem, setEditingItem] = useState<any>(null);
  
  // Form states
  const [nome, setNome] = useState('');
  const [codigoHex, setCodigoHex] = useState('');
  const [sigla, setSigla] = useState('');

  const fetchData = async () => {
    try {
      const [resCores, resTamanhos, resUnidades] = await Promise.all([
        api.get('/catalog/cores'),
        api.get('/catalog/tamanhos'),
        api.get('/core/unidades-medida')
      ]);
      setCores(resCores.data);
      setTamanhos(resTamanhos.data);
      setUnidades(resUnidades.data);
    } catch (err) {
      console.error(err);
    }
  };

  useEffect(() => {
    fetchData();
  }, []);

  const handleTabChange = (_: React.SyntheticEvent, newValue: number) => {
    setActiveTab(newValue);
  };

  const openModal = (item?: any) => {
    setEditingItem(item || null);
    setNome(item?.nome || '');
    setCodigoHex(item?.codigoHex || '');
    setSigla(item?.sigla || '');
    setIsModalOpen(true);
  };

  const getEndpoint = () => {
    if (activeTab === 0) return '/catalog/cores';
    if (activeTab === 1) return '/catalog/tamanhos';
    return '/core/unidades-medida';
  };

  const handleSave = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      const payload: any = { nome, ativo: true };
      if (activeTab === 0) payload.codigoHex = codigoHex;
      if (activeTab === 1 || activeTab === 2) payload.sigla = sigla;

      if (editingItem?.id) {
        await api.put(`${getEndpoint()}/${editingItem.id}`, payload);
      } else {
        await api.post(getEndpoint(), payload);
      }
      setIsModalOpen(false);
      fetchData();
    } catch (err) {
      console.error(err);
      alert('Erro ao salvar.');
    }
  };

  const handleDelete = async (id: string) => {
    if (!window.confirm('Tem certeza que deseja excluir?')) return;
    try {
      await api.delete(`${getEndpoint()}/${id}`);
      fetchData();
    } catch (err) {
      console.error(err);
      alert('Erro ao excluir (pode estar em uso).');
    }
  };

  const getTabTitle = () => {
    if (activeTab === 0) return 'Cor';
    if (activeTab === 1) return 'Tamanho';
    return 'Unidade de Medida';
  };

  return (
    <Box className="animate-fade-in-up">
      <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 4 }}>
        <Box>
          <Typography variant="h4" component="h1" gutterBottom sx={{ fontWeight: 800, color: 'var(--text-primary)', letterSpacing: '-0.5px' }}>
            Cadastros <span className="text-gradient">Auxiliares</span>
          </Typography>
          <Typography variant="body1" sx={{ color: 'var(--text-secondary)' }}>
            Gerencie cores, tamanhos e unidades de medida padronizadas para o sistema.
          </Typography>
        </Box>
        <Button 
          variant="contained" 
          startIcon={<Plus size={20} />}
          onClick={() => openModal()}
          sx={{
            background: 'var(--accent-gradient)',
            borderRadius: 'var(--radius-md)',
            textTransform: 'none',
            fontWeight: 600,
            boxShadow: '0 4px 14px 0 rgba(99, 102, 241, 0.39)'
          }}
        >
          Novo {getTabTitle()}
        </Button>
      </Box>

      <div className="premium-card">
        <Box sx={{ borderBottom: 1, borderColor: 'var(--border-color)', px: 2 }}>
          <Tabs 
            value={activeTab} 
            onChange={handleTabChange}
            sx={{
              '& .MuiTab-root': { color: 'var(--text-secondary)', fontWeight: 600, textTransform: 'none' },
              '& .Mui-selected': { color: 'var(--accent-primary) !important' },
              '& .MuiTabs-indicator': { backgroundColor: 'var(--accent-primary)' }
            }}
          >
            <Tab label="Cores" />
            <Tab label="Tamanhos" />
            <Tab label="Unidades de Medida" />
          </Tabs>
        </Box>

        <TableContainer sx={{ p: 2 }}>
          <Table size="small">
            <TableHead sx={{ bgcolor: 'rgba(255,255,255,0.02)' }}>
              <TableRow>
                <TableCell sx={{ color: 'var(--text-secondary)', fontWeight: 600, borderColor: 'var(--border-color)' }}>Nome</TableCell>
                {activeTab === 0 && <TableCell sx={{ color: 'var(--text-secondary)', fontWeight: 600, borderColor: 'var(--border-color)' }}>Código Hex</TableCell>}
                {(activeTab === 1 || activeTab === 2) && <TableCell sx={{ color: 'var(--text-secondary)', fontWeight: 600, borderColor: 'var(--border-color)' }}>Sigla</TableCell>}
                <TableCell sx={{ color: 'var(--text-secondary)', fontWeight: 600, borderColor: 'var(--border-color)' }}>Status</TableCell>
                <TableCell align="right" sx={{ color: 'var(--text-secondary)', fontWeight: 600, borderColor: 'var(--border-color)' }}>Ações</TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {(activeTab === 0 ? cores : activeTab === 1 ? tamanhos : unidades).map((item: any) => (
                <TableRow key={item.id} sx={{ '&:hover': { bgcolor: 'rgba(255,255,255,0.02)' } }}>
                  <TableCell sx={{ borderColor: 'var(--border-color)', color: 'var(--text-primary)', fontWeight: 500 }}>
                    {activeTab === 0 && item.codigoHex && (
                      <span style={{ display: 'inline-block', width: 12, height: 12, backgroundColor: item.codigoHex, borderRadius: '50%', marginRight: 8 }}></span>
                    )}
                    {item.nome}
                  </TableCell>
                  {activeTab === 0 && <TableCell sx={{ borderColor: 'var(--border-color)', color: 'var(--text-muted)' }}>{item.codigoHex || '-'}</TableCell>}
                  {(activeTab === 1 || activeTab === 2) && <TableCell sx={{ borderColor: 'var(--border-color)', color: 'var(--text-muted)' }}>{item.sigla || '-'}</TableCell>}
                  <TableCell sx={{ borderColor: 'var(--border-color)' }}>
                    {item.ativo ? <Chip label="Ativo" color="success" size="small" /> : <Chip label="Inativo" color="default" size="small" />}
                  </TableCell>
                  <TableCell align="right" sx={{ borderColor: 'var(--border-color)' }}>
                    <IconButton size="small" onClick={() => openModal(item)} sx={{ color: 'var(--text-secondary)' }}>
                      <Edit2 size={16} />
                    </IconButton>
                    <IconButton size="small" onClick={() => handleDelete(item.id)} sx={{ color: 'var(--error)' }}>
                      <Trash2 size={16} />
                    </IconButton>
                  </TableCell>
                </TableRow>
              ))}
            </TableBody>
          </Table>
        </TableContainer>
      </div>

      <Modal isOpen={isModalOpen} onClose={() => setIsModalOpen(false)} title={`${editingItem ? 'Editar' : 'Novo'} ${getTabTitle()}`} width="400px">
        <div className="glass-panel" style={{ padding: '24px', background: 'var(--bg-card)', border: 'none', boxShadow: 'none' }}>
          <form onSubmit={handleSave}>
            <Box sx={{ display: 'flex', flexDirection: 'column', gap: 3 }}>
              <TextField 
                label={`Nome da ${getTabTitle()}`}
                fullWidth 
                required 
                value={nome} 
                onChange={e => setNome(e.target.value)} 
              />
              
              {activeTab === 0 && (
                <TextField 
                  label="Código HEX (Ex: #FF0000)" 
                  fullWidth 
                  value={codigoHex} 
                  onChange={e => setCodigoHex(e.target.value)} 
                />
              )}
              
              {(activeTab === 1 || activeTab === 2) && (
                <TextField 
                  label="Sigla" 
                  fullWidth 
                  required={activeTab === 2}
                  value={sigla} 
                  onChange={e => setSigla(e.target.value)} 
                />
              )}

              <Box sx={{ display: 'flex', justifyContent: 'flex-end', gap: 2, mt: 1 }}>
                <Button onClick={() => setIsModalOpen(false)} sx={{ color: 'var(--text-secondary)' }}>Cancelar</Button>
                <Button type="submit" variant="contained" sx={{ bgcolor: 'var(--accent-primary)', '&:hover': { bgcolor: 'var(--accent-hover)' } }} disableElevation>
                  Salvar
                </Button>
              </Box>
            </Box>
          </form>
        </div>
      </Modal>
    </Box>
  );
};

export default CadastrosAuxiliares;
