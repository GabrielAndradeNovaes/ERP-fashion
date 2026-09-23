import React, { useState, useEffect } from 'react';
import {
  Box, Typography, Tabs, Tab, Button, TextField, InputAdornment,
  Stack, Chip, IconButton, CircularProgress, Tooltip, Switch, FormControlLabel,
  Select, MenuItem, FormControl, InputLabel
} from '@mui/material';
import { Info } from 'lucide-react';
import SearchIcon from '@mui/icons-material/Search';
import AddIcon from '@mui/icons-material/Add';
import EditIcon from '@mui/icons-material/Edit';
import DeleteIcon from '@mui/icons-material/Delete';
import api from '../api/axios';
import Modal from '../components/Modal';
import { EmpresaSelect } from '../components/EmpresaSelect';
import { useAuth } from '../contexts/AuthContext';
import { DataTable } from '../components/DataTable';
import PageHeader from '../components/PageHeader';
import PremiumCard from '../components/PremiumCard';
import { useToast } from '../contexts/ToastContext';

interface CrudTabProps {
  label: string;
  endpoint: string;
  columns: { key: string; label: string; format?: (val: any) => React.ReactNode }[];
  emptyEntity: any;
  renderForm: (entity: any, setEntity: (val: any) => void) => React.ReactNode;
  hideEmpresa?: boolean;
  editPermission?: string;
}

const CrudTab: React.FC<CrudTabProps> = ({ endpoint, columns, emptyEntity, renderForm, hideEmpresa = false, editPermission }) => {
  const [data, setData] = useState<any[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const [isModalOpen, setIsModalOpen] = useState(false);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [currentEntity, setCurrentEntity] = useState<any>(emptyEntity);
  const [search, setSearch] = useState('');
  const { user, hasPermission } = useAuth();
  
  const { showToast } = useToast();
  
  const canEdit = !editPermission || hasPermission(editPermission);

  const fetchData = async () => {
    try {
      setLoading(true);
      const res = await api.get(endpoint);
      setData(res.data);
      setError(null);
    } catch (err: any) {
      setError(err.response?.data?.message || 'Erro ao carregar dados.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchData();
      // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [endpoint]);

  const handleSave = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      setIsSubmitting(true);
      if (currentEntity.id) {
        await api.put(`${endpoint}/${currentEntity.id}`, currentEntity);
      } else {
        await api.post(endpoint, currentEntity);
      }
      setIsModalOpen(false);
      fetchData();
    } catch (err: any) {
      showToast(err.response?.data?.message || 'Erro ao salvar.', 'error');
    } finally {
      setIsSubmitting(false);
    }
  };

  const handleEdit = (item: any) => {
    setCurrentEntity(item);
    setIsModalOpen(true);
  };

  const handleDelete = async (id: string) => {
    if (!window.confirm('Deseja realmente excluir este registro?')) return;
    try {
      await api.delete(`${endpoint}/${id}`);
      fetchData();
    } catch (err: any) {
      showToast(err.response?.data?.message || 'Erro ao excluir.', 'error');
    }
  };

  const openNewModal = () => {
    let initialEntity = { ...emptyEntity };
    if (!hideEmpresa && user?.filialPrincipalId) {
      initialEntity = { ...initialEntity, empresa: { id: user.filialPrincipalId } };
    }
    setCurrentEntity(initialEntity);
    setIsModalOpen(true);
  };

  const filteredData = data.filter((item: any) => 
    Object.values(item).some(val => String(val).toLowerCase().includes(search.toLowerCase()))
  );

  const dataTableColumns = React.useMemo(() => {
    const cols: any[] = [];
    
    if (!hideEmpresa) {
      cols.push({
        accessorKey: 'empresa',
        header: 'Empresa',
        cell: (info: any) => {
          const emp = info.getValue();
          if (!emp) return '-';
          return (
            <Chip 
              label={emp.nomeFantasia || emp.razaoSocial || '-'} 
              size="small" 
              variant="outlined"
              sx={{ borderColor: 'var(--accent-primary)', color: 'var(--accent-primary)', fontWeight: 600 }}
            />
          );
        },
      });
    }

    cols.push(...columns.map(col => ({
      accessorKey: col.key,
      header: col.label,
      cell: (info: any) => col.format ? col.format(info.getValue()) : info.getValue(),
    })));

    if (canEdit) {
      cols.push({
        id: 'actions',
        header: 'Ações',
        cell: (info: any) => (
          <Stack direction="row" spacing={1} sx={{ justifyContent: 'center' }}>
            <Tooltip title="Editar">
              <IconButton size="small" color="primary" onClick={() => handleEdit(info.row.original)}>
                <EditIcon fontSize="small" />
              </IconButton>
            </Tooltip>
            <Tooltip title="Excluir">
              <IconButton size="small" color="error" onClick={() => handleDelete(info.row.original.id)}>
                <DeleteIcon fontSize="small" />
              </IconButton>
            </Tooltip>
          </Stack>
        ),
      });
    }
    return cols;
      // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [columns, canEdit, hideEmpresa]);

  return (
    <Box>
      <Box sx={{ p: 2, borderBottom: '1px solid', borderColor: 'divider', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
        <TextField
          placeholder="Pesquisar..."
          variant="outlined"
          size="small"
          value={search}
          onChange={(e) => setSearch(e.target.value)}
          slotProps={{
            input: {
              startAdornment: (
                <InputAdornment position="start">
                  <SearchIcon color="action" />
                </InputAdornment>
              ),
            }
          }}
          sx={{ width: 300 }}
        />
        <Box sx={{ display: 'flex', gap: 2, alignItems: 'center' }}>
          <Chip label={`${filteredData.length} Itens`} color="primary" variant="outlined" />
          {canEdit && (
            <Button variant="contained" color="primary" startIcon={<AddIcon />} onClick={openNewModal} size="small" disableElevation>
              Novo
            </Button>
          )}
        </Box>
      </Box>
      
      {loading ? (
        <Box sx={{ p: 6, display: 'flex', justifyContent: 'center', alignItems: 'center' }}>
          <CircularProgress />
        </Box>
      ) : error ? (
        <Box sx={{ p: 4, textAlign: 'center' }}>
          <Typography color="error">{error}</Typography>
        </Box>
      ) : filteredData.length === 0 ? (
        <Box sx={{ p: 8, display: 'flex', flexDirection: 'column', alignItems: 'center', color: 'text.secondary' }}>
          <Info size={48} style={{ marginBottom: '16px', opacity: 0.5 }} />
          <Typography>Nenhum registro encontrado.</Typography>
        </Box>
      ) : (
        <DataTable columns={dataTableColumns} data={filteredData} />
      )}

      <Modal isOpen={isModalOpen} onClose={() => setIsModalOpen(false)} title={currentEntity.id ? `Editar Cadastro` : `Novo Cadastro`} width="600px">
        <form onSubmit={handleSave}>
          <Stack spacing={3} sx={{ mt: 1 }}>
            {!hideEmpresa && (
              <EmpresaSelect
                value={currentEntity.empresa?.id || ''}
                onChange={(val) => setCurrentEntity({ ...currentEntity, empresa: { id: val } })}
              />
            )}
            {renderForm(currentEntity, setCurrentEntity)}
            <Box sx={{ display: 'flex', justifyContent: 'flex-end', pt: 2 }}>
              <Button onClick={() => setIsModalOpen(false)} color="inherit" sx={{ mr: 2 }}>Cancelar</Button>
              <Button type="submit" variant="contained" color="primary" disabled={isSubmitting} startIcon={isSubmitting && <CircularProgress size={20} color="inherit" />} disableElevation>
                Salvar
              </Button>
            </Box>
          </Stack>
        </form>
      </Modal>
    </Box>
  );
};

// ====================== TABS CONFIGURATION ======================

const renderBasicForm = (entity: any, setEntity: (val: any) => void) => (
  <>
    <TextField label="Nome" fullWidth required value={entity.nome} onChange={e => setEntity({ ...entity, nome: e.target.value })} />
    <FormControlLabel control={<Switch checked={entity.ativo} onChange={e => setEntity({ ...entity, ativo: e.target.checked })} />} label="Ativo" />
  </>
);

const renderSiglaForm = (entity: any, setEntity: (val: any) => void) => (
  <>
    <TextField label="Nome" fullWidth required value={entity.nome} onChange={e => setEntity({ ...entity, nome: e.target.value })} />
    <TextField label="Sigla" fullWidth value={entity.sigla} onChange={e => setEntity({ ...entity, sigla: e.target.value })} />
    <FormControlLabel control={<Switch checked={entity.ativo} onChange={e => setEntity({ ...entity, ativo: e.target.checked })} />} label="Ativo" />
  </>
);

const tabsConfig = [
  {
    group: 'Empresa',
    items: [
      {
        label: 'Empresas e Filiais',
        endpoint: '/empresas',
        hideEmpresa: true,
        editPermission: 'USUARIOS_ADMIN',
        columns: [
          { key: 'nomeFantasia', label: 'Nome Fantasia' },
          { key: 'razaoSocial', label: 'Razão Social' },
          { key: 'cnpj', label: 'CNPJ' },
          { key: 'ativo', label: 'Ativo', format: (v: any) => v ? 'Sim' : 'Não' }
        ],
        emptyEntity: { nomeFantasia: '', razaoSocial: '', cnpj: '', ativo: true },
        renderForm: (entity: any, setEntity: any) => (
          <>
            <TextField label="Nome Fantasia" fullWidth required value={entity.nomeFantasia} onChange={e => setEntity({ ...entity, nomeFantasia: e.target.value })} />
            <TextField label="Razão Social" fullWidth value={entity.razaoSocial} onChange={e => setEntity({ ...entity, razaoSocial: e.target.value })} />
            <TextField label="CNPJ" fullWidth value={entity.cnpj} onChange={e => setEntity({ ...entity, cnpj: e.target.value })} />
            <FormControlLabel control={<Switch checked={entity.ativo} onChange={e => setEntity({ ...entity, ativo: e.target.checked })} />} label="Ativo" />
          </>
        )
      },
      {
        label: 'Departamentos',
        endpoint: '/core/departamentos',
        editPermission: 'USUARIOS_ADMIN',
        columns: [
          { key: 'nome', label: 'Nome' },
          { key: 'ativo', label: 'Ativo', format: (v: any) => v ? 'Sim' : 'Não' }
        ],
        emptyEntity: { nome: '', ativo: true },
        renderForm: renderBasicForm
      }
    ]
  },
  {
    group: 'Produtos',
    items: [
      {
        label: 'Categorias',
        endpoint: '/core/categorias',
        columns: [
          { key: 'nome', label: 'Nome' },
          { key: 'ativo', label: 'Ativo', format: (v: any) => v ? 'Sim' : 'Não' }
        ],
        emptyEntity: { nome: '', ativo: true },
        renderForm: renderBasicForm
      },
      {
        label: 'Cores',
        endpoint: '/catalog/cores',
        columns: [
          { key: 'nome', label: 'Nome' },
          { key: 'codigoHex', label: 'Código Hex', format: (v: any) => (v ? <Box sx={{display:'flex', alignItems:'center', gap:1}}><Box sx={{width:16,height:16,borderRadius:'50%',bgcolor:v}}/>{v}</Box> : '-') },
          { key: 'ativo', label: 'Ativo', format: (v: any) => v ? 'Sim' : 'Não' }
        ],
        emptyEntity: { nome: '', codigoHex: '', ativo: true },
        renderForm: (entity: any, setEntity: any) => (
          <>
            <TextField label="Nome" fullWidth required value={entity.nome} onChange={e => setEntity({ ...entity, nome: e.target.value })} />
            <TextField label="Código Hex (Ex: #FFFFFF)" fullWidth value={entity.codigoHex} onChange={e => setEntity({ ...entity, codigoHex: e.target.value })} />
            <FormControlLabel control={<Switch checked={entity.ativo} onChange={e => setEntity({ ...entity, ativo: e.target.checked })} />} label="Ativo" />
          </>
        )
      },
      {
        label: 'Tamanhos',
        endpoint: '/catalog/tamanhos',
        columns: [
          { key: 'nome', label: 'Nome' },
          { key: 'sigla', label: 'Sigla' },
          { key: 'ativo', label: 'Ativo', format: (v: any) => v ? 'Sim' : 'Não' }
        ],
        emptyEntity: { nome: '', sigla: '', ativo: true },
        renderForm: renderSiglaForm
      },
      {
        label: 'Unidades de Medida',
        endpoint: '/core/unidades-medida',
        columns: [
          { key: 'nome', label: 'Nome' },
          { key: 'sigla', label: 'Sigla' },
          { key: 'ativo', label: 'Ativo', format: (v: any) => v ? 'Sim' : 'Não' }
        ],
        emptyEntity: { nome: '', sigla: '', ativo: true },
        renderForm: renderSiglaForm
      }
    ]
  },
  {
    group: 'Produção',
    items: [
      {
        label: 'Grupos de Funcionário',
        endpoint: '/grupos-funcionarios',
        columns: [
          { key: 'nome', label: 'Nome' },
          { key: 'ativo', label: 'Ativo', format: (v: any) => v ? 'Sim' : 'Não' }
        ],
        emptyEntity: { nome: '', ativo: true },
        renderForm: renderBasicForm
      }
    ]
  },
  {
    group: 'Estoque',
    items: [
      {
        label: 'Localizações',
        endpoint: '/inventory/localizacoes',
        columns: [
          { key: 'nome', label: 'Nome' },
          { key: 'tipo', label: 'Tipo' },
          { key: 'ativo', label: 'Ativo', format: (v: any) => v ? 'Sim' : 'Não' }
        ],
        emptyEntity: { nome: '', tipo: 'MATERIA_PRIMA', ativo: true },
        renderForm: (entity: any, setEntity: any) => (
          <>
            <TextField label="Nome" fullWidth required value={entity.nome} onChange={e => setEntity({ ...entity, nome: e.target.value })} />
            <FormControl fullWidth>
              <InputLabel>Tipo</InputLabel>
              <Select value={entity.tipo} label="Tipo" onChange={e => setEntity({ ...entity, tipo: e.target.value })}>
                <MenuItem value="MATERIA_PRIMA">Matéria Prima</MenuItem>
                <MenuItem value="PRODUTO_ACABADO">Produto Acabado</MenuItem>
                <MenuItem value="EXPEDICAO">Expedição / Estoque Central</MenuItem>
                <MenuItem value="DEFEITO">Defeitos / Perdas</MenuItem>
              </Select>
            </FormControl>
            <FormControlLabel control={<Switch checked={entity.ativo} onChange={e => setEntity({ ...entity, ativo: e.target.checked })} />} label="Ativo" />
          </>
        )
      }
    ]
  }
];

const flattenTabs = tabsConfig.flatMap(group => group.items as any) as CrudTabProps[];

const CadastrosBase = () => {
  const [activeTab, setActiveTab] = useState(0);

  const handleTabChange = (event: React.SyntheticEvent, newValue: number) => {
    setActiveTab(newValue);
  };

  return (
    <Box className="animate-fade-in-up" sx={{ p: 2 }}>
      <PageHeader 
        title="Central de Cadastros Base"
        subtitle="Gerencie todas as entidades auxiliares, agrupadas por módulo."
        icon={<Info size={28} />}
      />

      <PremiumCard>
        <Box sx={{ display: 'flex', flexDirection: { xs: 'column', md: 'row' }, minHeight: 600 }}>
          {/* Vertical Tabs Sidebar */}
          <Box sx={{ 
            width: { xs: '100%', md: 240 }, 
            borderRight: { xs: 'none', md: '1px solid' }, 
            borderBottom: { xs: '1px solid', md: 'none' }, 
            borderColor: 'divider',
            pt: 2,
            pr: 1
          }}>
            <Tabs
              orientation="vertical"
              variant="scrollable"
              value={activeTab}
              onChange={handleTabChange}
              sx={{ 
                borderRight: 1, borderColor: 'divider',
                '& .MuiTab-root': { alignItems: 'flex-start', textAlign: 'left', minHeight: 48, fontWeight: 600 },
                '& .Mui-selected': { background: 'rgba(99, 102, 241, 0.08)' }
              }}
            >
              {tabsConfig.map((group) => [
                <Tab 
                  key={`group-${group.group}`} 
                  disabled
                  label={group.group}
                  sx={{ 
                    opacity: 1, 
                    minHeight: 32, 
                    padding: '16px 16px 4px 16px', 
                    fontSize: '0.75rem', 
                    fontWeight: 800, 
                    color: 'text.secondary', 
                    alignItems: 'flex-start',
                    letterSpacing: '0.08333em',
                    textTransform: 'uppercase'
                  }} 
                />,
                group.items.map((item) => {
                  const globalIndex = flattenTabs.findIndex(t => t.label === item.label);
                  return <Tab key={item.label} label={item.label} value={globalIndex} sx={{ ml: 1, borderRadius: '8px 0 0 8px' }} />;
                })
              ])}
            </Tabs>
          </Box>

          {/* Tab Content */}
          <Box sx={{ flex: 1, bgcolor: 'var(--bg-paper)' }}>
            {flattenTabs.map((tab, index) => (
              <div key={tab.label} role="tabpanel" hidden={activeTab !== index}>
                {activeTab === index && (
                  <Box>
                    <Typography variant="h6" sx={{ p: 2, pb: 0, fontWeight: 700 }}>
                      {tab.label}
                    </Typography>
                    <CrudTab {...tab} />
                  </Box>
                )}
              </div>
            ))}
          </Box>
        </Box>
      </PremiumCard>
    </Box>
  );
};

export default CadastrosBase;
