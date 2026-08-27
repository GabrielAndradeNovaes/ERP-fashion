import CrudPage from '../components/CrudPage';
import { TextField } from '@mui/material';

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
        { key: 'telefone', label: 'Telefone' }
      ]}
      emptyEntity={{ nome: '', documento: '', email: '', telefone: '', empresa: null }}
      renderForm={(entity, setEntity) => (
        <>
          <TextField
            label="Nome"
            variant="outlined"
            fullWidth
            required
            value={entity.nome}
            onChange={e => setEntity({ ...entity, nome: e.target.value })}
          />
          <TextField
            label="CPF/CNPJ"
            variant="outlined"
            fullWidth
            value={entity.documento}
            onChange={e => setEntity({ ...entity, documento: e.target.value })}
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

export default Clientes;
