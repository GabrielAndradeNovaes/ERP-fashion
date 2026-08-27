import CrudPage from '../components/CrudPage';
import { TextField } from '@mui/material';

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
        { key: 'telefone', label: 'Telefone' }
      ]}
      emptyEntity={{ nome: '', cnpj: '', email: '', telefone: '' }}
      renderForm={(entity, setEntity) => (
        <>
          <TextField
            label="Razão Social"
            variant="outlined"
            fullWidth
            required
            value={entity.nome}
            onChange={e => setEntity({ ...entity, nome: e.target.value })}
          />
          <TextField
            label="CNPJ"
            variant="outlined"
            fullWidth
            value={entity.cnpj}
            onChange={e => setEntity({ ...entity, cnpj: e.target.value })}
          />
          <TextField
            label="Email"
            type="email"
            variant="outlined"
            fullWidth
            value={entity.email}
            onChange={e => setEntity({ ...entity, email: e.target.value })}
          />
          <TextField
            label="Telefone"
            variant="outlined"
            fullWidth
            value={entity.telefone}
            onChange={e => setEntity({ ...entity, telefone: e.target.value })}
          />
        </>
      )}
    />
  );
};

export default Fornecedores;
