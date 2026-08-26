import React, { useEffect, useState } from 'react';
import { Box, Typography, Card, Table, TableBody, TableCell, TableContainer, TableHead, TableRow, Chip, FormControl, Select, MenuItem, Snackbar, Alert, Button, Dialog, DialogTitle, DialogContent, DialogActions, TextField, CircularProgress } from '@mui/material';
import { Building2, CheckCircle2, AlertCircle, XCircle, Plus, Loader2 } from 'lucide-react';
import api from '../../api/axios';

interface Tenant {
  id: string;
  nome_empresa: string;
  schema_name: string;
  cnpj: string;
  razao_social: string;
  status: string;
  criado_em: string;
}

const TenantsList = () => {
  const [tenants, setTenants] = useState<Tenant[]>([]);
  const [loading, setLoading] = useState(true);
  const [snackbar, setSnackbar] = useState({ open: false, message: '', severity: 'success' as 'success' | 'error' });
  
  // Modal State
  const [openModal, setOpenModal] = useState(false);
  const [newTenant, setNewTenant] = useState({
    nomeEmpresa: '', schemaName: '', adminNome: '', adminEmail: '', adminSenha: ''
  });
  const [submitting, setSubmitting] = useState(false);

  const fetchTenants = async () => {
    try {
      const response = await api.get('/admin/tenants');
      setTenants(response.data);
    } catch (error) {
      console.error('Erro ao buscar clientes:', error);
      setSnackbar({ open: true, message: 'Erro ao carregar a lista de clientes.', severity: 'error' });
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchTenants();
  }, []);

  // Polling Inteligente
  useEffect(() => {
    const hasPending = tenants.some(t => t.status === 'PENDENTE' || t.status === 'CRIANDO_INFRA');
    if (hasPending) {
      const interval = setInterval(() => {
        fetchTenants();
      }, 3000); // 3 segundos
      return () => clearInterval(interval);
    }
  }, [tenants]);

  const handleCreateTenant = async () => {
    try {
      setSubmitting(true);
      await api.post('/admin/tenants/provision', newTenant);
      setSnackbar({ open: true, message: 'Provisionamento iniciado com sucesso!', severity: 'success' });
      setOpenModal(false);
      setNewTenant({ nomeEmpresa: '', schemaName: '', adminNome: '', adminEmail: '', adminSenha: '' });
      fetchTenants();
    } catch (error: any) {
      setSnackbar({ open: true, message: error.response?.data?.message || 'Erro ao iniciar provisionamento.', severity: 'error' });
    } finally {
      setSubmitting(false);
    }
  };

  const handleStatusChange = async (schemaName: string, newStatus: string) => {
    try {
      await api.put(`/admin/tenants/${schemaName}/status`, { status: newStatus });
      setSnackbar({ open: true, message: 'Status atualizado com sucesso!', severity: 'success' });
      fetchTenants(); // Recarrega a lista
    } catch (error) {
      setSnackbar({ open: true, message: 'Erro ao atualizar status.', severity: 'error' });
    }
  };

  const handleCloseSnackbar = () => setSnackbar({ ...snackbar, open: false });

  const getStatusColor = (status: string) => {
    switch (status) {
      case 'ATIVO': return 'success';
      case 'INADIMPLENTE': return 'warning';
      case 'CANCELADO': return 'error';
      default: return 'default';
    }
  };

  const getStatusIcon = (status: string) => {
    switch (status) {
      case 'ATIVO': return <CheckCircle2 size={16} />;
      case 'INADIMPLENTE': return <AlertCircle size={16} />;
      case 'CANCELADO': return <XCircle size={16} />;
      case 'PENDENTE':
      case 'CRIANDO_INFRA': return <CircularProgress size={16} color="inherit" />;
      case 'FALHA': return <XCircle size={16} />;
      default: return undefined;
    }
  };

  const isProcessing = (status: string) => status === 'PENDENTE' || status === 'CRIANDO_INFRA';

  if (loading) return <Typography>Carregando...</Typography>;

  return (
    <Box sx={{ p: 3 }}>
      <Box sx={{ display: 'flex', alignItems: 'center', mb: 4, gap: 2 }}>
        <Box sx={{ p: 1.5, bgcolor: 'primary.main', borderRadius: 2, color: 'white', display: 'flex' }}>
          <Building2 size={28} />
        </Box>
        <Box>
          <Typography variant="h4" sx={{ fontWeight: 800, color: 'var(--text-primary)' }}>
            Gestão de Clientes (Tenants)
          </Typography>
          <Typography variant="body1" color="text.secondary">
            Visão administrativa do Control Plane (Mestre)
          </Typography>
        </Box>
        <Button 
          variant="contained" 
          startIcon={<Plus size={18} />}
          onClick={() => setOpenModal(true)}
          sx={{ borderRadius: 2, textTransform: 'none', fontWeight: 600 }}
        >
          Novo Cliente
        </Button>
      </Box>

      <Card sx={{ borderRadius: 3, boxShadow: '0 4px 20px rgba(0,0,0,0.05)', overflow: 'hidden' }}>
        <TableContainer>
          <Table>
            <TableHead sx={{ bgcolor: 'var(--bg-card)' }}>
              <TableRow>
                <TableCell sx={{ fontWeight: 600 }}>Nome Fantasia</TableCell>
                <TableCell sx={{ fontWeight: 600 }}>Schema ID</TableCell>
                <TableCell sx={{ fontWeight: 600 }}>CNPJ / Razão Social</TableCell>
                <TableCell sx={{ fontWeight: 600 }}>Criado Em</TableCell>
                <TableCell sx={{ fontWeight: 600, width: 250 }}>Status (Ação)</TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {tenants.map((tenant) => (
                <TableRow key={tenant.schema_name} hover>
                  <TableCell sx={{ fontWeight: 500 }}>{tenant.nome_empresa}</TableCell>
                  <TableCell>
                    <Chip label={tenant.schema_name} size="small" variant="outlined" />
                  </TableCell>
                  <TableCell>
                    <Typography variant="body2">{tenant.cnpj || 'Não informado'}</Typography>
                    <Typography variant="caption" color="text.secondary">
                      {tenant.razao_social || 'Não informada'}
                    </Typography>
                  </TableCell>
                  <TableCell>{new Date(tenant.criado_em).toLocaleDateString()}</TableCell>
                  <TableCell>
                    <Box sx={{ display: 'flex', alignItems: 'center', gap: 2 }}>
                      <Chip 
                        icon={getStatusIcon(tenant.status)} 
                        label={isProcessing(tenant.status) ? 'Criando Ambiente...' : tenant.status} 
                        color={getStatusColor(tenant.status) as any} 
                        size="small" 
                        sx={{ fontWeight: 'bold' }}
                      />
                      {!isProcessing(tenant.status) && tenant.status !== 'FALHA' && (
                        <FormControl size="small" sx={{ minWidth: 140 }}>
                          <Select
                            value={tenant.status}
                            onChange={(e) => handleStatusChange(tenant.schema_name, e.target.value)}
                            sx={{ fontSize: '0.875rem' }}
                          >
                            <MenuItem value="ATIVO">ATIVO</MenuItem>
                            <MenuItem value="INADIMPLENTE">INADIMPLENTE</MenuItem>
                            <MenuItem value="CANCELADO">CANCELADO</MenuItem>
                          </Select>
                        </FormControl>
                      )}
                    </Box>
                  </TableCell>
                </TableRow>
              ))}
              {tenants.length === 0 && (
                <TableRow>
                  <TableCell colSpan={5} align="center" sx={{ py: 3 }}>
                    Nenhum cliente cadastrado no sistema master.
                  </TableCell>
                </TableRow>
              )}
            </TableBody>
          </Table>
        </TableContainer>
      </Card>

      {/* Modal de Provisionamento */}
      <Dialog open={openModal} onClose={() => setOpenModal(false)} maxWidth="sm" fullWidth>
        <DialogTitle sx={{ fontWeight: 'bold' }}>Novo Cliente (Provisionamento)</DialogTitle>
        <DialogContent>
          <Box sx={{ display: 'flex', flexDirection: 'column', gap: 2, mt: 1 }}>
            <Typography variant="body2" color="text.secondary" sx={{ mb: 2 }}>
              Um novo ambiente isolado será criado para esta empresa. O processo pode levar alguns segundos.
            </Typography>
            <TextField label="Nome Fantasia" required fullWidth value={newTenant.nomeEmpresa} onChange={(e) => setNewTenant({...newTenant, nomeEmpresa: e.target.value})} />
            <TextField label="Identificador Único (Schema ID)" required fullWidth placeholder="Ex: tenant_2" helperText="Sem espaços ou caracteres especiais." value={newTenant.schemaName} onChange={(e) => setNewTenant({...newTenant, schemaName: e.target.value})} />
            <Typography variant="subtitle2" sx={{ mt: 2, fontWeight: 'bold' }}>Dados do Usuário Administrador (Seed)</Typography>
            <TextField label="Nome do Admin" required fullWidth value={newTenant.adminNome} onChange={(e) => setNewTenant({...newTenant, adminNome: e.target.value})} />
            <TextField label="E-mail do Admin" type="email" required fullWidth value={newTenant.adminEmail} onChange={(e) => setNewTenant({...newTenant, adminEmail: e.target.value})} />
            <TextField label="Senha Temporária" type="password" required fullWidth value={newTenant.adminSenha} onChange={(e) => setNewTenant({...newTenant, adminSenha: e.target.value})} />
          </Box>
        </DialogContent>
        <DialogActions sx={{ px: 3, pb: 3 }}>
          <Button onClick={() => setOpenModal(false)} disabled={submitting}>Cancelar</Button>
          <Button 
            variant="contained" 
            onClick={handleCreateTenant} 
            disabled={submitting || !newTenant.nomeEmpresa || !newTenant.schemaName || !newTenant.adminEmail}
          >
            {submitting ? <CircularProgress size={24} color="inherit" /> : 'Criar e Provisionar'}
          </Button>
        </DialogActions>
      </Dialog>

      <Snackbar 
        open={snackbar.open} 
        autoHideDuration={6000} 
        onClose={handleCloseSnackbar}
        anchorOrigin={{ vertical: 'bottom', horizontal: 'right' }}
      >
        <Alert onClose={handleCloseSnackbar} severity={snackbar.severity} sx={{ width: '100%' }}>
          {snackbar.message}
        </Alert>
      </Snackbar>
    </Box>
  );
};

export default TenantsList;
