import React, { useState, useEffect } from 'react';
import { Info } from 'lucide-react';
import api from '../api/axios';
import Modal from './Modal';
import { EmpresaSelect } from './EmpresaSelect';
import { useAuth } from '../contexts/AuthContext';
import type { ColumnDef } from '@tanstack/react-table';
import { DataTable } from './DataTable';
import {
  Box,
  Typography,
  Button,
  Card,
  TextField,
  InputAdornment,
  Stack,
  Chip,
  IconButton,
  CircularProgress,
  Tooltip
} from '@mui/material';
import SearchIcon from '@mui/icons-material/Search';
import AddIcon from '@mui/icons-material/Add';
import EditIcon from '@mui/icons-material/Edit';
import DeleteIcon from '@mui/icons-material/Delete';

interface CrudPageProps {
  title: string;
  description: string;
  endpoint: string;
  columns: { key: string; label: string; format?: (val: any) => string }[];
  emptyEntity: any;
  renderForm: (entity: any, setEntity: (val: any) => void) => React.ReactNode;
  hideEmpresa?: boolean;
  editPermission?: string;
}

const CrudPage: React.FC<CrudPageProps> = ({ title, description, endpoint, columns, emptyEntity, renderForm, hideEmpresa = false, editPermission }) => {
  const [data, setData] = useState<any[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const [isModalOpen, setIsModalOpen] = useState(false);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [currentEntity, setCurrentEntity] = useState<any>(emptyEntity);
  const [search, setSearch] = useState('');
  const { user, hasPermission } = useAuth();
  
  const canEdit = hasPermission(editPermission);

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
      alert(err.response?.data?.message || 'Erro ao salvar.');
    } finally {
      setIsSubmitting(false);
    }
  };

  const handleEdit = (item: any) => {
    setCurrentEntity(item);
    setIsModalOpen(true);
  };

  const handleDelete = async (id: string) => {
    if (!confirm('Deseja realmente excluir este registro?')) return;
    try {
      await api.delete(`${endpoint}/${id}`);
      fetchData();
    } catch (err: any) {
      alert(err.response?.data?.message || 'Erro ao excluir.');
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

  const dataTableColumns: ColumnDef<any, any, any>[] = React.useMemo(() => {
    const cols: ColumnDef<any, any, any>[] = [];
    
    if (!hideEmpresa) {
      cols.push({
        accessorKey: 'empresa',
        header: 'Empresa',
        cell: (info) => {
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
      cell: (info) => col.format ? col.format(info.getValue()) : info.getValue(),
    })));

    if (canEdit) {
      cols.push({
        id: 'actions',
        header: 'Ações',
        cell: (info) => (
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
  }, [columns, canEdit, hideEmpresa]);

  return (
    <Box className="animate-fade-in">
      <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 4 }}>
        <Box>
          <Typography variant="h4" component="h1" gutterBottom sx={{ fontWeight: 'bold' }}>
            {title}
          </Typography>
          <Typography variant="body1" color="text.secondary">
            {description}
          </Typography>
        </Box>
        {canEdit && (
          <Button 
            variant="contained" 
            color="primary" 
            startIcon={<AddIcon />}
            onClick={openNewModal}
            size="large"
            disableElevation
          >
            Novo Cadastro
          </Button>
        )}
      </Box>

      <Card variant="outlined" sx={{ borderRadius: 2, overflow: 'hidden' }}>
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
          <Chip label={`${filteredData.length} Itens`} color="primary" variant="outlined" />
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
      </Card>

      <Modal isOpen={isModalOpen} onClose={() => setIsModalOpen(false)} title={currentEntity.id ? `Editar ${title}` : `Novo ${title}`} width="600px">
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
              <Button 
                onClick={() => setIsModalOpen(false)} 
                color="inherit" 
                sx={{ mr: 2 }}
              >
                Cancelar
              </Button>
              <Button 
                type="submit" 
                variant="contained" 
                color="primary"
                disabled={isSubmitting}
                startIcon={isSubmitting && <CircularProgress size={20} color="inherit" />}
                disableElevation
              >
                Salvar
              </Button>
            </Box>
          </Stack>
        </form>
      </Modal>
    </Box>
  );
};

export default CrudPage;
