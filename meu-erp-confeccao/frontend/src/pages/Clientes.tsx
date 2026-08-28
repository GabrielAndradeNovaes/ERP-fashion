import CrudPage from '../components/CrudPage';
import { TextField, Grid, MenuItem } from '@mui/material';

const Clientes = () => {
  return (
    <CrudPage
      title="Clientes"
      description="Gerencie os clientes do sistema."
      endpoint="/core/clientes"
      editPermission="CLIENTES_EDIT"
      columns={[
        { key: 'nome', label: 'Nome/Razão Social' },
        { key: 'documento', label: 'CPF/CNPJ' },
        { key: 'email', label: 'Email' },
        { key: 'telefone', label: 'Telefone' },
        { key: 'status', label: 'Status' },
        { key: 'limiteCredito', label: 'Lim. Crédito' }
      ]}
      emptyEntity={{ nome: '', documento: '', email: '', telefone: '', empresa: null, tipoPessoa: 'PF', razaoSocial: '', inscricaoEstadual: '', limiteCredito: 0, tabelaPrecoPadrao: '', status: 'ATIVO', endereco: '' }}
      renderForm={(entity, setEntity) => (
        <Grid container spacing={2}>
          <Grid item xs={12} sm={4}>
            <TextField
              select
              label="Tipo Pessoa"
              fullWidth
              value={entity.tipoPessoa || 'PF'}
              onChange={e => setEntity({ ...entity, tipoPessoa: e.target.value })}
            >
              <MenuItem value="PF">Física</MenuItem>
              <MenuItem value="PJ">Jurídica</MenuItem>
            </TextField>
          </Grid>
          <Grid item xs={12} sm={8}>
            <TextField
              label="Nome"
              variant="outlined"
              fullWidth
              required
              value={entity.nome}
              onChange={e => setEntity({ ...entity, nome: e.target.value })}
            />
          </Grid>
          <Grid item xs={12} sm={6}>
            <TextField
              label="Razão Social (PJ)"
              variant="outlined"
              fullWidth
              value={entity.razaoSocial || ''}
              onChange={e => setEntity({ ...entity, razaoSocial: e.target.value })}
            />
          </Grid>
          <Grid item xs={12} sm={6}>
            <TextField
              label="CPF/CNPJ"
              variant="outlined"
              fullWidth
              value={entity.documento}
              onChange={e => setEntity({ ...entity, documento: e.target.value })}
            />
          </Grid>
          <Grid item xs={12} sm={6}>
            <TextField
              label="Inscrição Estadual"
              variant="outlined"
              fullWidth
              value={entity.inscricaoEstadual || ''}
              onChange={e => setEntity({ ...entity, inscricaoEstadual: e.target.value })}
            />
          </Grid>
          <Grid item xs={12} sm={6}>
            <TextField
              label="Email"
              type="email"
              variant="outlined"
              fullWidth
              value={entity.email}
              onChange={e => setEntity({ ...entity, email: e.target.value })}
            />
          </Grid>
          <Grid item xs={12} sm={6}>
            <TextField
              label="Telefone"
              variant="outlined"
              fullWidth
              value={entity.telefone}
              onChange={e => setEntity({ ...entity, telefone: e.target.value })}
            />
          </Grid>
          <Grid item xs={12} sm={6}>
            <TextField
              label="Limite de Crédito"
              type="number"
              variant="outlined"
              fullWidth
              value={entity.limiteCredito || 0}
              onChange={e => setEntity({ ...entity, limiteCredito: e.target.value })}
            />
          </Grid>
          <Grid item xs={12} sm={6}>
            <TextField
              label="Tabela de Preço Padrão"
              variant="outlined"
              fullWidth
              value={entity.tabelaPrecoPadrao || ''}
              onChange={e => setEntity({ ...entity, tabelaPrecoPadrao: e.target.value })}
            />
          </Grid>
          <Grid item xs={12} sm={6}>
            <TextField
              select
              label="Status"
              fullWidth
              value={entity.status || 'ATIVO'}
              onChange={e => setEntity({ ...entity, status: e.target.value })}
            >
              <MenuItem value="ATIVO">Ativo</MenuItem>
              <MenuItem value="INATIVO">Inativo</MenuItem>
              <MenuItem value="BLOQUEADO">Bloqueado</MenuItem>
            </TextField>
          </Grid>
          <Grid item xs={12}>
            <TextField
              label="Endereço (JSON ou Texto)"
              variant="outlined"
              multiline
              rows={3}
              fullWidth
              value={entity.endereco || ''}
              onChange={e => setEntity({ ...entity, endereco: e.target.value })}
            />
          </Grid>
        </Grid>
      )}
    />
  );
};

export default Clientes;
