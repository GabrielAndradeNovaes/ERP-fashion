import React from 'react';
import CrudPage from '../components/CrudPage';
import { TextField } from '@mui/material';

const Categorias = () => {
  return (
    <CrudPage
      title="Categorias"
      description="Categorias de produtos."
      endpoint="/core/categorias"
      columns={[
        { key: 'nome', label: 'Nome da Categoria' }
      ]}
      emptyEntity={{ nome: '' }}
      renderForm={(entity, setEntity) => (
        <>
          <TextField
            label="Nome da Categoria"
            variant="outlined"
            fullWidth
            required
            value={entity.nome}
            onChange={e => setEntity({ ...entity, nome: e.target.value })}
            placeholder="Ex: Camisetas"
          />
        </>
      )}
    />
  );
};

export default Categorias;
