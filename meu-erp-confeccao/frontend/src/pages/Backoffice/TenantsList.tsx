import React, { useEffect, useState } from 'react';
import { Box, Typography, Card, Table, TableBody, TableCell, TableContainer, TableHead, TableRow, Chip, FormControl, Select, MenuItem, Snackbar, Alert, Button, Dialog, DialogTitle, DialogContent, DialogActions, TextField, CircularProgress, Tabs, Tab, Grid, InputAdornment, IconButton, Switch, FormControlLabel } from '@mui/material';
import { Building2, CheckCircle2, AlertCircle, XCircle, Plus, LogIn, Search, Settings2 } from 'lucide-react';
import api from '../../api/axios';
import { useAuth } from '../../contexts/AuthContext';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';

interface Tenant {
  id: string;
  nomeEmpresa: string;
  schemaName: string;
  slug: string;
  cnpj: string;
  razaoSocial: string;
  nomeFantasia: string;
  porte: string;
  naturezaJuridica: string;
  statusRfb: string;
  dataAbertura: string;
  emailPrincipal: string;
  telefone: string;
  cep: string;
  logradouro: string;
  numero: string;
  complemento: string;
  bairro: string;
  cidade: string;
  estado: string;
  cnaePrincipalCodigo: string;
  cnaePrincipalDescricao: string;
  simplesNacional: boolean;
  status: string;
  criadoEm: string;
}

interface TabPanelProps {
  children?: React.ReactNode;
  index: number;
  value: number;
}

function TabPanel(props: TabPanelProps) {
  const { children, value, index, ...other } = props;
  return (
    <div
      role="tabpanel"
      hidden={value !== index}
      id={`tenant-tabpanel-${index}`}
      aria-labelledby={`tenant-tab-${index}`}
      {...other}
    >
      {value === index && (
        <Box sx={{ p: 2 }}>
          {children}
        </Box>
      )}
    </div>
  );
}

const TenantsList = () => {
  const [tenants, setTenants] = useState<Tenant[]>([]);
  const [loading, setLoading] = useState(true);
  const [snackbar, setSnackbar] = useState({ open: false, message: '', severity: 'success' as 'success' | 'error' });
  
  // Modal State
  const [openModal, setOpenModal] = useState(false);
  const [tabValue, setTabValue] = useState(0);
  const [fetchingCnpj, setFetchingCnpj] = useState(false);

  // Modules Modal State
  const [modulesModalOpen, setModulesModalOpen] = useState(false);
  const [selectedTenantForModules, setSelectedTenantForModules] = useState<string | null>(null);
  const [tenantModules, setTenantModules] = useState<{moduleName: string, active: boolean}[]>([]);
  const [savingModules, setSavingModules] = useState(false);

  const initialTenantState = {
    nomeEmpresa: '', adminNome: '', adminEmail: '', adminSenha: '',
    cnpj: '', razaoSocial: '', nomeFantasia: '', porte: '', naturezaJuridica: '', statusRfb: '', dataAbertura: '',
    emailPrincipal: '', telefone: '', cep: '', logradouro: '', numero: '', complemento: '', bairro: '', cidade: '', estado: '',
    cnaePrincipalCodigo: '', cnaePrincipalDescricao: '', simplesNacional: false, receitaFederalRawData: ''
  };
  const [newTenant, setNewTenant] = useState(initialTenantState);
  const [editingSchema, setEditingSchema] = useState<string | null>(null);
  
  const { setImpersonatedTenant } = useAuth();
  const navigate = useNavigate();
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

  const handleSaveTenant = async () => {
    try {
      setSubmitting(true);
      if (editingSchema) {
        await api.put(`/admin/tenants/${editingSchema}`, newTenant);
        setSnackbar({ open: true, message: 'Cliente atualizado com sucesso!', severity: 'success' });
      } else {
        await api.post('/admin/tenants/provision', newTenant);
        setSnackbar({ open: true, message: 'Provisionamento iniciado com sucesso!', severity: 'success' });
      }
      setOpenModal(false);
      setNewTenant(initialTenantState);
      setEditingSchema(null);
      fetchTenants();
    } catch (error: any) {
      setSnackbar({ open: true, message: error.response?.data?.message || 'Erro ao salvar cliente.', severity: 'error' });
    } finally {
      setSubmitting(false);
    }
  };

  const handleOpenModules = async (schemaName: string) => {
    try {
      setSelectedTenantForModules(schemaName);
      const response = await api.get(`/admin/tenants/${schemaName}/modules`);
      setTenantModules(response.data);
      setModulesModalOpen(true);
    } catch (error) {
      setSnackbar({ open: true, message: 'Erro ao carregar módulos do cliente.', severity: 'error' });
    }
  };

  const handleSaveModules = async () => {
    if (!selectedTenantForModules) return;
    try {
      setSavingModules(true);
      await api.post(`/admin/tenants/${selectedTenantForModules}/modules`, { modules: tenantModules });
      setSnackbar({ open: true, message: 'Módulos atualizados com sucesso!', severity: 'success' });
      setModulesModalOpen(false);
    } catch (error) {
      setSnackbar({ open: true, message: 'Erro ao salvar módulos.', severity: 'error' });
    } finally {
      setSavingModules(false);
    }
  };

  const handleToggleModule = (moduleName: string) => {
    if (moduleName === 'CORE') return; // CORE cannot be toggled
    setTenantModules(prev => 
      prev.map(m => m.moduleName === moduleName ? { ...m, active: !m.active } : m)
    );
  };

  const handleEditClick = (tenant: Tenant) => {
    setEditingSchema(tenant.schemaName);
    
    // Convert all null values to empty strings to avoid React 'value prop on input should not be null' warning
    const sanitizedTenant = Object.fromEntries(
      Object.entries(tenant).map(([k, v]) => [k, v === null ? '' : v])
    );
    
    setNewTenant({
      ...initialTenantState,
      ...sanitizedTenant,
      adminNome: '', // Not editable
      adminEmail: '',
      adminSenha: ''
    } as any);
    setTabValue(0);
    setOpenModal(true);
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

  const fetchCnpjData = async () => {
    const cleanCnpj = newTenant.cnpj.replace(/\D/g, '');
    if (cleanCnpj.length !== 14) {
      setSnackbar({ open: true, message: 'CNPJ inválido para busca.', severity: 'error' });
      return;
    }

    setFetchingCnpj(true);
    try {
      const { data } = await axios.get(`https://publica.cnpj.ws/cnpj/${cleanCnpj}`);
      
      setNewTenant(prev => ({
        ...prev,
        razaoSocial: data.razao_social || '',
        nomeFantasia: data.estabelecimento?.nome_fantasia || data.razao_social || '',
        nomeEmpresa: data.estabelecimento?.nome_fantasia || data.razao_social || '',
        porte: data.porte?.descricao || '',
        naturezaJuridica: data.natureza_juridica?.descricao || '',
        statusRfb: data.estabelecimento?.situacao_cadastral || '',
        dataAbertura: data.estabelecimento?.data_inicio_atividade || '',
        emailPrincipal: data.estabelecimento?.email || '',
        telefone: `${data.estabelecimento?.ddd1 || ''} ${data.estabelecimento?.telefone1 || ''}`.trim(),
        cep: data.estabelecimento?.cep || '',
        logradouro: data.estabelecimento?.logradouro || '',
        numero: data.estabelecimento?.numero || '',
        complemento: data.estabelecimento?.complemento || '',
        bairro: data.estabelecimento?.bairro || '',
        cidade: data.estabelecimento?.cidade?.nome || '',
        estado: data.estabelecimento?.estado?.sigla || '',
        cnaePrincipalCodigo: data.estabelecimento?.atividade_principal?.classificacao || '',
        cnaePrincipalDescricao: data.estabelecimento?.atividade_principal?.descricao || '',
        simplesNacional: data.simples?.simples === 'S',
        receitaFederalRawData: JSON.stringify(data)
      }));
      setSnackbar({ open: true, message: 'Dados da Receita Federal importados com sucesso!', severity: 'success' });
    } catch (error) {
      console.error(error);
      setSnackbar({ open: true, message: 'Erro ao buscar dados do CNPJ na Receita Federal.', severity: 'error' });
    } finally {
      setFetchingCnpj(false);
    }
  };

  const handleCloseSnackbar = () => setSnackbar({ ...snackbar, open: false });

  const handleImpersonate = (tenant: Tenant) => {
    // Navigate to tenant subdomain
    const protocol = window.location.protocol;
    let rootDomain = window.location.hostname;
    // se estiver acessando localhost:3010, queremos manter a porta
    const port = window.location.port ? ':' + window.location.port : '';
    
    // Se estiver usando algo como admin.localhost, temos que tirar o "admin." para obter o root domain
    if (rootDomain.startsWith('admin.')) {
        rootDomain = rootDomain.replace('admin.', '');
    } else if (rootDomain.startsWith('www.')) {
        rootDomain = rootDomain.replace('www.', '');
    }
    
    const url = `${protocol}//${tenant.slug}.${rootDomain}${port}/`;
    window.open(url, '_blank');
  };

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
          onClick={() => { setOpenModal(true); setTabValue(0); setNewTenant(initialTenantState); setEditingSchema(null); }}
          sx={{ borderRadius: 2, textTransform: 'none', fontWeight: 600, ml: 'auto' }}
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
                <TableCell sx={{ fontWeight: 600, width: 300 }}>Status (Ação)</TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {tenants.map((tenant) => (
                <TableRow key={tenant.schemaName} hover>
                  <TableCell sx={{ fontWeight: 500 }}>{tenant.nomeEmpresa}</TableCell>
                  <TableCell>
                    <Chip label={tenant.schemaName} size="small" variant="outlined" />
                  </TableCell>
                  <TableCell>
                    <Typography variant="body2">{tenant.cnpj || 'Não informado'}</Typography>
                    <Typography variant="caption" color="text.secondary">
                      {tenant.razaoSocial || 'Não informada'}
                    </Typography>
                  </TableCell>
                  <TableCell>{tenant.criadoEm ? new Date(tenant.criadoEm).toLocaleDateString() : ''}</TableCell>
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
                            value={tenant.status || 'ATIVO'}
                            onChange={(e) => handleStatusChange(tenant.schemaName, e.target.value)}
                            sx={{ fontSize: '0.875rem' }}
                          >
                            <MenuItem value="ATIVO">ATIVO</MenuItem>
                            <MenuItem value="INADIMPLENTE">INADIMPLENTE</MenuItem>
                            <MenuItem value="CANCELADO">CANCELADO</MenuItem>
                          </Select>
                        </FormControl>
                      )}
                      {tenant.status === 'ATIVO' && (
                        <>
                          <Button 
                            size="small" 
                            variant="outlined" 
                            color="info"
                            startIcon={<Settings2 size={16} />}
                            onClick={() => handleOpenModules(tenant.schemaName)}
                          >
                            Módulos
                          </Button>
                          <Button 
                            size="small" 
                            variant="outlined" 
                            color="primary"
                            onClick={() => handleEditClick(tenant)}
                          >
                            Editar
                          </Button>
                          <Button 
                            size="small" 
                            variant="outlined" 
                            startIcon={<LogIn size={16} />}
                            onClick={() => handleImpersonate(tenant)}
                          >
                            Acessar
                          </Button>
                        </>
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

      {/* Modal de Provisionamento / Edição */}
      <Dialog open={openModal} onClose={() => setOpenModal(false)} maxWidth="md" fullWidth>
        <DialogTitle sx={{ fontWeight: 'bold' }}>
          {editingSchema ? 'Editar Cliente' : 'Novo Cliente (Provisionamento Corporativo)'}
        </DialogTitle>
        <DialogContent dividers sx={{ p: 0 }}>
          <Box sx={{ borderBottom: 1, borderColor: 'divider' }}>
            <Tabs value={tabValue} onChange={(e, val) => setTabValue(val)}>
              <Tab label="Dados Gerais" />
              <Tab label="Endereço" />
              <Tab label="Dados Fiscais" />
            </Tabs>
          </Box>
          
          <TabPanel value={tabValue} index={0}>
            {editingSchema && (
              <Box sx={{ mb: 2, p: 2, bgcolor: 'primary.50', borderRadius: 2, border: '1px solid', borderColor: 'primary.100' }}>
                <Typography variant="body2" color="primary.800" sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                  <strong>Link de Acesso (Subdomínio):</strong> 
                  <a 
                    href={`${window.location.protocol}//${newTenant.slug}.${window.location.hostname.replace('admin.', '').replace('www.', '')}${window.location.port ? ':' + window.location.port : ''}/`} 
                    target="_blank" 
                    rel="noreferrer"
                  >
                    {`${window.location.protocol}//${newTenant.slug}.${window.location.hostname.replace('admin.', '').replace('www.', '')}${window.location.port ? ':' + window.location.port : ''}/`}
                  </a>
                </Typography>
              </Box>
            )}
            <Grid container spacing={2}>
              <Grid xs={12} sm={6}>
                <TextField 
                  label="CNPJ" 
                  fullWidth 
                  required 
                  value={newTenant.cnpj} 
                  onChange={(e) => setNewTenant({...newTenant, cnpj: e.target.value})}
                  onBlur={fetchCnpjData}
                  slotProps={{
                    input: {
                      endAdornment: (
                        <InputAdornment position="end">
                          <IconButton onClick={fetchCnpjData} edge="end" disabled={fetchingCnpj}>
                            {fetchingCnpj ? <CircularProgress size={24} /> : <Search size={20} />}
                          </IconButton>
                        </InputAdornment>
                      )
                    }
                  }}
                  helperText="Digite o CNPJ e saia do campo para buscar dados"
                />
              </Grid>
              <Grid xs={12} sm={6}>
                <TextField label="Nome Fantasia / Empresa" required fullWidth value={newTenant.nomeEmpresa} onChange={(e) => setNewTenant({...newTenant, nomeEmpresa: e.target.value})} />
              </Grid>
              <Grid xs={12}>
                <TextField label="Razão Social" fullWidth value={newTenant.razaoSocial} onChange={(e) => setNewTenant({...newTenant, razaoSocial: e.target.value})} />
              </Grid>
              <Grid xs={12} sm={4}>
                <TextField label="Porte" fullWidth value={newTenant.porte} onChange={(e) => setNewTenant({...newTenant, porte: e.target.value})} />
              </Grid>
              <Grid xs={12} sm={4}>
                <TextField label="Natureza Jurídica" fullWidth value={newTenant.naturezaJuridica} onChange={(e) => setNewTenant({...newTenant, naturezaJuridica: e.target.value})} />
              </Grid>
              <Grid xs={12} sm={4}>
                <TextField label="Status RFB" fullWidth value={newTenant.statusRfb} onChange={(e) => setNewTenant({...newTenant, statusRfb: e.target.value})} />
              </Grid>
              <Grid xs={12} sm={6}>
                <TextField label="E-mail Principal" type="email" fullWidth value={newTenant.emailPrincipal} onChange={(e) => setNewTenant({...newTenant, emailPrincipal: e.target.value})} />
              </Grid>
              <Grid xs={12} sm={6}>
                <TextField label="Telefone" fullWidth value={newTenant.telefone} onChange={(e) => setNewTenant({...newTenant, telefone: e.target.value})} />
              </Grid>

              <Grid xs={12}>
                <Typography variant="subtitle2" sx={{ mt: 2, fontWeight: 'bold' }}>Dados do Usuário Administrador (Seed)</Typography>
              </Grid>
              <Grid xs={12} sm={4}>
                <TextField label="Nome do Admin" required={!editingSchema} disabled={!!editingSchema} fullWidth value={newTenant.adminNome} onChange={(e) => setNewTenant({...newTenant, adminNome: e.target.value})} />
              </Grid>
              <Grid xs={12} sm={4}>
                <TextField label="E-mail do Admin" type="email" required={!editingSchema} disabled={!!editingSchema} fullWidth value={newTenant.adminEmail} onChange={(e) => setNewTenant({...newTenant, adminEmail: e.target.value})} />
              </Grid>
              <Grid xs={12} sm={4}>
                <TextField label="Senha Temporária" type="password" required={!editingSchema} disabled={!!editingSchema} fullWidth value={newTenant.adminSenha} onChange={(e) => setNewTenant({...newTenant, adminSenha: e.target.value})} />
              </Grid>
            </Grid>
          </TabPanel>

          <TabPanel value={tabValue} index={1}>
             <Grid container spacing={2}>
              <Grid xs={12} sm={4}>
                <TextField label="CEP" fullWidth value={newTenant.cep} onChange={(e) => setNewTenant({...newTenant, cep: e.target.value})} />
              </Grid>
              <Grid xs={12} sm={8}>
                <TextField label="Logradouro" fullWidth value={newTenant.logradouro} onChange={(e) => setNewTenant({...newTenant, logradouro: e.target.value})} />
              </Grid>
              <Grid xs={12} sm={4}>
                <TextField label="Número" fullWidth value={newTenant.numero} onChange={(e) => setNewTenant({...newTenant, numero: e.target.value})} />
              </Grid>
              <Grid xs={12} sm={8}>
                <TextField label="Complemento" fullWidth value={newTenant.complemento} onChange={(e) => setNewTenant({...newTenant, complemento: e.target.value})} />
              </Grid>
              <Grid xs={12} sm={5}>
                <TextField label="Bairro" fullWidth value={newTenant.bairro} onChange={(e) => setNewTenant({...newTenant, bairro: e.target.value})} />
              </Grid>
              <Grid xs={12} sm={5}>
                <TextField label="Cidade" fullWidth value={newTenant.cidade} onChange={(e) => setNewTenant({...newTenant, cidade: e.target.value})} />
              </Grid>
              <Grid xs={12} sm={2}>
                <TextField label="UF" fullWidth value={newTenant.estado} onChange={(e) => setNewTenant({...newTenant, estado: e.target.value})} />
              </Grid>
             </Grid>
          </TabPanel>

          <TabPanel value={tabValue} index={2}>
             <Grid container spacing={2}>
              <Grid xs={12} sm={4}>
                <TextField label="CNAE Principal" fullWidth value={newTenant.cnaePrincipalCodigo} onChange={(e) => setNewTenant({...newTenant, cnaePrincipalCodigo: e.target.value})} />
              </Grid>
              <Grid xs={12} sm={8}>
                <TextField label="Descrição CNAE" fullWidth value={newTenant.cnaePrincipalDescricao} onChange={(e) => setNewTenant({...newTenant, cnaePrincipalDescricao: e.target.value})} />
              </Grid>
              <Grid xs={12}>
                <FormControl fullWidth>
                  <Select
                    value={newTenant.simplesNacional ? 'sim' : 'nao'}
                    onChange={(e) => setNewTenant({...newTenant, simplesNacional: e.target.value === 'sim'})}
                  >
                    <MenuItem value="sim">Optante pelo Simples Nacional</MenuItem>
                    <MenuItem value="nao">Não Optante</MenuItem>
                  </Select>
                </FormControl>
              </Grid>
             </Grid>
          </TabPanel>
        </DialogContent>
        <DialogActions sx={{ px: 3, pb: 3, pt: 2 }}>
          <Button onClick={() => setOpenModal(false)} disabled={submitting}>Cancelar</Button>
          <Button 
            variant="contained" 
            onClick={handleSaveTenant} 
            disabled={submitting || !newTenant.nomeEmpresa || (!editingSchema && !newTenant.adminEmail) || !newTenant.cnpj}
          >
            {submitting ? <CircularProgress size={24} color="inherit" /> : (editingSchema ? 'Salvar Alterações' : 'Salvar e Provisionar')}
          </Button>
        </DialogActions>
      </Dialog>

      {/* Modal de Módulos */}
      <Dialog open={modulesModalOpen} onClose={() => setModulesModalOpen(false)} maxWidth="sm" fullWidth>
        <DialogTitle sx={{ display: 'flex', alignItems: 'center', gap: 1, pb: 2, borderBottom: '1px solid var(--border-color)' }}>
          <Settings2 size={24} color="var(--primary-main)" />
          Gerenciar Módulos
        </DialogTitle>
        <DialogContent sx={{ pt: '24px !important' }}>
          <Typography variant="body2" color="text.secondary" sx={{ mb: 3 }}>
            Ative ou desative os módulos do sistema para este cliente. O módulo CORE é obrigatório.
          </Typography>
          <Grid container spacing={3}>
            {tenantModules.map(mod => (
              <Grid item xs={12} sm={6} key={mod.moduleName}>
                <Card variant="outlined" sx={{ p: 2, display: 'flex', alignItems: 'center', justifyContent: 'space-between', borderRadius: 2 }}>
                  <Typography variant="subtitle2" sx={{ fontWeight: 600 }}>
                    {mod.moduleName}
                  </Typography>
                  <FormControlLabel
                    control={
                      <Switch 
                        checked={mod.active} 
                        onChange={() => handleToggleModule(mod.moduleName)}
                        disabled={mod.moduleName === 'CORE'}
                        color="primary"
                      />
                    }
                    label=""
                    sx={{ m: 0 }}
                  />
                </Card>
              </Grid>
            ))}
          </Grid>
        </DialogContent>
        <DialogActions sx={{ px: 3, pb: 3, pt: 2 }}>
          <Button onClick={() => setModulesModalOpen(false)} disabled={savingModules}>Cancelar</Button>
          <Button 
            variant="contained" 
            onClick={handleSaveModules} 
            disabled={savingModules}
          >
            {savingModules ? <CircularProgress size={24} color="inherit" /> : 'Salvar Módulos'}
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
