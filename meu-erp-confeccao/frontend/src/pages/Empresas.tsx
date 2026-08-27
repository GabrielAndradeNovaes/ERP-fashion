import CrudPage from '../components/CrudPage';
import { TextField, Switch, FormControlLabel } from '@mui/material';

const Empresas = () => {
  return (
    <CrudPage
      title="Empresas/Filiais"
      description="Gerencie as empresas e filiais do grupo."
      endpoint="/empresas"
      hideEmpresa={true}
      editPermission="USUARIOS_ADMIN"
      columns={[
        { key: 'nomeFantasia', label: 'Nome Fantasia' },
        { key: 'razaoSocial', label: 'Razão Social' },
        { key: 'cnpj', label: 'CNPJ' },
        { key: 'ativo', label: 'Ativo', render: (val) => val ? 'Sim' : 'Não' }
      ]}
      emptyEntity={{ nomeFantasia: '', razaoSocial: '', cnpj: '', ativo: true }}
      renderForm={(entity, setEntity) => (
        <>
          <TextField
            label="Nome Fantasia"
            variant="outlined"
            fullWidth
            required
            value={entity.nomeFantasia}
            onChange={e => setEntity({ ...entity, nomeFantasia: e.target.value })}
          />
          <TextField
            label="Razão Social"
            variant="outlined"
            fullWidth
            value={entity.razaoSocial}
            onChange={e => setEntity({ ...entity, razaoSocial: e.target.value })}
          />
          <TextField
            label="CNPJ"
            variant="outlined"
            fullWidth
            value={entity.cnpj}
            onChange={e => setEntity({ ...entity, cnpj: e.target.value })}
          />
          <FormControlLabel
            control={
              <Switch
                checked={entity.ativo}
                onChange={e => setEntity({ ...entity, ativo: e.target.checked })}
              />
            }
            label="Ativo"
          />
        </>
      )}
    />
  );
};

export default Empresas;
