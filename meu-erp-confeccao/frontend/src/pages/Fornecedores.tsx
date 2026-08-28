import CrudPage from '../components/CrudPage';
import { TextField, Grid, MenuItem } from '@mui/material';

const Fornecedores = () => {
  return (
    <CrudPage
      title="Fornecedores"
      description="Gerencie os fornecedores de matéria-prima."
      endpoint="/core/fornecedores"
      editPermission="CLIENTES_EDIT"
      columns={[
        { key: 'nome', label: 'Razão Social' },
        { key: 'cnpj', label: 'CNPJ' },
        { key: 'email', label: 'Email' },
        { key: 'telefone', label: 'Telefone' },
        { key: 'status', label: 'Status' }
      ]}
      emptyEntity={{ nome: '', cnpj: '', email: '', telefone: '', tipoPessoa: 'PJ', razaoSocial: '', inscricaoEstadual: '', categoriaFornecedor: '', prazoPagamentoPadrao: 0, contatoNome: '', status: 'ATIVO', endereco: '' }}
      renderForm={(entity, setEntity) => (
        <Grid container spacing={2}>
          <Grid item xs={12} sm={4}>
            <TextField
              select
              label="Tipo Pessoa"
              fullWidth
              value={entity.tipoPessoa || 'PJ'}
              onChange={e => setEntity({ ...entity, tipoPessoa: e.target.value })}
            >
              <MenuItem value="PF">Física</MenuItem>
              <MenuItem value="PJ">Jurídica</MenuItem>
            </TextField>
          </Grid>
          <Grid item xs={12} sm={8}>
            <TextField
              label="Nome Fantasia"
              variant="outlined"
              fullWidth
              required
              value={entity.nome}
              onChange={e => setEntity({ ...entity, nome: e.target.value })}
            />
          </Grid>
          <Grid item xs={12} sm={6}>
            <TextField
              label="Razão Social"
              variant="outlined"
              fullWidth
              value={entity.razaoSocial || ''}
              onChange={e => setEntity({ ...entity, razaoSocial: e.target.value })}
            />
          </Grid>
          <Grid item xs={12} sm={6}>
            <TextField
              label="CNPJ/CPF"
              variant="outlined"
              fullWidth
              value={entity.cnpj}
              onChange={e => setEntity({ ...entity, cnpj: e.target.value })}
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
              label="Contato (Nome)"
              variant="outlined"
              fullWidth
              value={entity.contatoNome || ''}
              onChange={e => setEntity({ ...entity, contatoNome: e.target.value })}
            />
          </Grid>
          <Grid item xs={12} sm={6}>
            <TextField
              label="Categoria de Fornecedor"
              variant="outlined"
              fullWidth
              value={entity.categoriaFornecedor || ''}
              onChange={e => setEntity({ ...entity, categoriaFornecedor: e.target.value })}
            />
          </Grid>
          <Grid item xs={12} sm={6}>
            <TextField
              label="Prazo Pagamento (Dias)"
              type="number"
              variant="outlined"
              fullWidth
              value={entity.prazoPagamentoPadrao || 0}
              onChange={e => setEntity({ ...entity, prazoPagamentoPadrao: e.target.value })}
            />
          </Grid>
          <Grid item xs={12} sm={12}>
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

export default Fornecedores;
