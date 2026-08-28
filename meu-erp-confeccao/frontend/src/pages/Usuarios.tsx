import React, { useState, useEffect } from 'react';
import CrudPage from '../components/CrudPage';
import api from '../api/axios';
import {
  TextField,
  FormControl,
  InputLabel,
  Select,
  MenuItem,
  Checkbox,
  ListItemText,
  Chip,
  Box,
  Typography,
  Grid,
  Card,
  CardContent,
  FormControlLabel,
  RadioGroup,
  Radio,
  FormLabel,
  Switch
} from '@mui/material';
import { Building2, ShieldCheck } from 'lucide-react';

const AVAILABLE_PERMISSIONS = [
  { id: 'PRODUTOS_VIEW', label: 'Ver Produtos', modulo: 'Cadastros Base' },
  { id: 'PRODUTOS_EDIT', label: 'Editar Produtos', modulo: 'Cadastros Base' },
  { id: 'CLIENTES_VIEW', label: 'Ver Clientes', modulo: 'Cadastros Base' },
  { id: 'CLIENTES_EDIT', label: 'Editar Clientes', modulo: 'Cadastros Base' },
  { id: 'ESTOQUE_VIEW', label: 'Ver Estoque', modulo: 'Estoque' },
  { id: 'ESTOQUE_EDIT', label: 'Editar Estoque', modulo: 'Estoque' },
  { id: 'PCP_VIEW', label: 'Ver Produção (PCP)', modulo: 'PCP' },
  { id: 'PCP_EDIT', label: 'Editar Produção (PCP)', modulo: 'PCP' },
  { id: 'FINANCEIRO_VIEW', label: 'Ver Financeiro', modulo: 'Financeiro' },
  { id: 'FINANCEIRO_EDIT', label: 'Editar Financeiro', modulo: 'Financeiro' },
  { id: 'USUARIOS_ADMIN', label: 'Administrar Sistema/Usuários', modulo: 'Segurança' },
];

const Usuarios = () => {
  const [empresas, setEmpresas] = useState<any[]>([]);

  useEffect(() => {
    const fetchEmpresas = async () => {
      try {
        const res = await api.get('/empresas');
        setEmpresas(res.data);
      } catch (err) {
        console.error("Erro ao carregar empresas", err);
      }
    };
    fetchEmpresas();
  }, []);

  const columns = [
    { key: 'nome', label: 'Nome' },
    { key: 'email', label: 'E-mail' },
    { key: 'telefone', label: 'Telefone' },
    { key: 'cargo', label: 'Cargo' },
    {
      key: 'filialPrincipalId',
      label: 'Filial Principal',
      format: (val: string) => {
        const emp = empresas.find(e => e.id === val);
        return emp ? <Chip label={emp.nomeFantasia || emp.razaoSocial} size="small" color="primary" /> : '-';
      }
    },
    {
      key: 'empresas',
      label: 'Acesso',
      format: (val: any[]) => {
        if (!val || val.length === 0) return '-';
        return (
          <Box sx={{ display: 'flex', gap: 0.5, flexWrap: 'wrap' }}>
            {val.map(e => <Chip key={e.id} label={e.nomeFantasia} size="small" variant="outlined" color="primary" />)}
          </Box>
        );
      }
    }
  ];

  const emptyUsuario = {
    nome: '',
    email: '',
    senha: '',
    role: 'USER',
    empresaIds: [],
    filialPrincipalId: '',
    permissoes: [],
    cpf: '',
    telefone: '',
    cargo: '',
    dataNascimento: '',
    departamento: '',
    fotoUrl: ''
  };

  const renderForm = (entity: any, setEntity: (val: any) => void) => {
    const handleChange = (e: any) => {
      setEntity({ ...entity, [e.target.name]: e.target.value });
    };

    const handleEmpresaToggle = (empId: string) => {
      const current = entity.empresaIds || [];
      const isSelected = current.includes(empId);
      const newIds = isSelected ? current.filter((id: string) => id !== empId) : [...current, empId];
      
      let newPrincipal = entity.filialPrincipalId;
      if (isSelected && newPrincipal === empId) {
        newPrincipal = newIds.length > 0 ? newIds[0] : '';
      } else if (!isSelected && newIds.length === 1) {
        newPrincipal = empId;
      }
      
      setEntity({ ...entity, empresaIds: newIds, filialPrincipalId: newPrincipal });
    };

    const handlePermissaoToggle = (permId: string) => {
      const current = entity.permissoes || [];
      const isSelected = current.includes(permId);
      const newPerms = isSelected ? current.filter((id: string) => id !== permId) : [...current, permId];
      setEntity({ ...entity, permissoes: newPerms });
    };

    useEffect(() => {
      if (entity.empresas && !entity.empresaIds) {
        setEntity({
          ...entity,
          empresaIds: entity.empresas.map((e: any) => e.id),
          permissoes: entity.permissoes || []
        });
      }
    }, [entity.empresas]);

    const modulos = Array.from(new Set(AVAILABLE_PERMISSIONS.map(p => p.modulo)));

    return (
      <Grid container spacing={3}>
        <Grid item xs={12} md={6}>
          <Typography variant="h6" sx={{ mb: 2, display: 'flex', alignItems: 'center', gap: 1 }}>
            <ShieldCheck size={20} /> Dados do Usuário
          </Typography>
          <TextField
            label="Nome"
            name="nome"
            value={entity.nome || ''}
            onChange={handleChange}
            fullWidth
            required
            sx={{ mb: 2 }}
          />
          <TextField
            label="E-mail"
            name="email"
            type="email"
            value={entity.email || ''}
            onChange={handleChange}
            fullWidth
            required
            sx={{ mb: 2 }}
          />
          <TextField
            label={entity.id ? "Nova Senha (deixe em branco para manter)" : "Senha"}
            name="senha"
            type="password"
            value={entity.senha || ''}
            onChange={handleChange}
            fullWidth
            required={!entity.id}
            sx={{ mb: 2 }}
          />
          <TextField
            label="CPF"
            name="cpf"
            value={entity.cpf || ''}
            onChange={handleChange}
            fullWidth
            sx={{ mb: 2 }}
          />
          <TextField
            label="Telefone"
            name="telefone"
            value={entity.telefone || ''}
            onChange={handleChange}
            fullWidth
            sx={{ mb: 2 }}
          />
          <TextField
            label="Cargo"
            name="cargo"
            value={entity.cargo || ''}
            onChange={handleChange}
            fullWidth
            sx={{ mb: 2 }}
          />
          <TextField
            label="Departamento"
            name="departamento"
            value={entity.departamento || ''}
            onChange={handleChange}
            fullWidth
            sx={{ mb: 2 }}
          />
          <TextField
            label="Data de Nascimento"
            name="dataNascimento"
            type="date"
            InputLabelProps={{ shrink: true }}
            value={entity.dataNascimento || ''}
            onChange={handleChange}
            fullWidth
            sx={{ mb: 2 }}
          />
          <TextField
            label="URL da Foto"
            name="fotoUrl"
            value={entity.fotoUrl || ''}
            onChange={handleChange}
            fullWidth
          />
        </Grid>

        <Grid item xs={12} md={6}>
          <Typography variant="h6" sx={{ mb: 2, display: 'flex', alignItems: 'center', gap: 1 }}>
            <Building2 size={20} /> Acesso às Filiais
          </Typography>
          <Card variant="outlined" sx={{ mb: 2 }}>
            <CardContent sx={{ p: 2 }}>
              {(empresas.length === 0) ? (
                <Typography color="text.secondary">Nenhuma filial cadastrada.</Typography>
              ) : (
                <Grid container spacing={2}>
                  {empresas.map(emp => (
                    <Grid item xs={12} key={emp.id} sx={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
                      <FormControlLabel
                        control={
                          <Checkbox 
                            checked={(entity.empresaIds || []).includes(emp.id)}
                            onChange={() => handleEmpresaToggle(emp.id)}
                          />
                        }
                        label={emp.nomeFantasia || emp.razaoSocial}
                      />
                      {(entity.empresaIds || []).includes(emp.id) && (
                        <Radio
                          checked={entity.filialPrincipalId === emp.id}
                          onChange={() => setEntity({ ...entity, filialPrincipalId: emp.id })}
                          value={emp.id}
                          name="filialPrincipalId"
                          size="small"
                          inputProps={{ 'aria-label': 'Filial Principal' }}
                        />
                      )}
                    </Grid>
                  ))}
                  <Grid item xs={12}>
                    <Typography variant="caption" color="text.secondary">
                      * O botão redondo define a <b>Filial Principal</b> do usuário.
                    </Typography>
                  </Grid>
                </Grid>
              )}
            </CardContent>
          </Card>
        </Grid>

        <Grid item xs={12}>
          <Typography variant="h6" sx={{ mb: 2, display: 'flex', alignItems: 'center', gap: 1 }}>
            <ShieldCheck size={20} /> Permissões do Sistema
          </Typography>
          
          <Grid container spacing={2}>
            {modulos.map(modulo => (
              <Grid item xs={12} sm={6} md={4} key={modulo}>
                <Card variant="outlined" sx={{ height: '100%' }}>
                  <CardContent>
                    <Typography variant="subtitle2" sx={{ mb: 1, fontWeight: 'bold', color: 'primary.main' }}>
                      {modulo}
                    </Typography>
                    <Box sx={{ display: 'flex', flexDirection: 'column' }}>
                      {AVAILABLE_PERMISSIONS.filter(p => p.modulo === modulo).map(perm => (
                        <FormControlLabel
                          key={perm.id}
                          control={
                            <Switch 
                              size="small"
                              checked={(entity.permissoes || []).includes(perm.id)}
                              onChange={() => handlePermissaoToggle(perm.id)}
                            />
                          }
                          label={<Typography variant="body2">{perm.label}</Typography>}
                        />
                      ))}
                    </Box>
                  </CardContent>
                </Card>
              </Grid>
            ))}
          </Grid>

        </Grid>
      </Grid>
    );
  };

  return (
    <CrudPage
      title="Usuários e Permissões"
      description="Gerencie os acessos, a filial principal e os privilégios dos usuários."
      endpoint="/usuarios"
      columns={columns}
      emptyEntity={emptyUsuario}
      renderForm={renderForm}
      hideEmpresa={true}
      editPermission="USUARIOS_ADMIN"
    />
  );
};

export default Usuarios;
