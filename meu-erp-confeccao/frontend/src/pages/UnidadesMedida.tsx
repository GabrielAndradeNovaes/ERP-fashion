import CrudPage from '../components/CrudPage';
import { TextField } from '@mui/material';

const UnidadesMedida = () => {
  return (
    <CrudPage
      title="Unidades de Medida"
      description="Gerencie as unidades de medida."
      endpoint="/core/unidades-medida"
      editPermission="PRODUTOS_EDIT"
      columns={[
        { key: 'sigla', label: 'Sigla' },
        { key: 'nome', label: 'Nome' }
      ]}
      emptyEntity={{ sigla: '', nome: '' }}
      renderForm={(entity, setEntity) => (
        <>
          <TextField
            label="Sigla"
            variant="outlined"
            fullWidth
            required
            value={entity.sigla}
            onChange={e => setEntity({ ...entity, sigla: e.target.value })}
            placeholder="Ex: KG"
          />
          <TextField
            label="Nome"
            variant="outlined"
            fullWidth
            required
            value={entity.nome}
            onChange={e => setEntity({ ...entity, nome: e.target.value })}
            placeholder="Ex: Quilograma"
          />
        </>
      )}
    />
  );
};

export default UnidadesMedida;
