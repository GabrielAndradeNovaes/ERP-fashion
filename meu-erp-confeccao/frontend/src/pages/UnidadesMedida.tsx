import React from 'react';
import CrudPage from '../components/CrudPage';

const UnidadesMedida = () => {
  return (
    <CrudPage
      title="Unidades de Medida"
      description="Gerencie as unidades de medida (KG, Metro, Litro, etc)."
      endpoint="/core/unidades-medida"
      columns={[
        { key: 'sigla', label: 'Sigla' },
        { key: 'nome', label: 'Nome' }
      ]}
      emptyEntity={{ sigla: '', nome: '' }}
      renderForm={(entity, setEntity) => (
        <>
          <div>
            <label style={{ display: 'block', marginBottom: '0.5rem', fontSize: '0.9rem' }}>Sigla</label>
            <input type="text" required value={entity.sigla} onChange={e => setEntity({ ...entity, sigla: e.target.value })} placeholder="Ex: KG" />
          </div>
          <div>
            <label style={{ display: 'block', marginBottom: '0.5rem', fontSize: '0.9rem' }}>Nome</label>
            <input type="text" required value={entity.nome} onChange={e => setEntity({ ...entity, nome: e.target.value })} placeholder="Ex: Quilograma" />
          </div>
        </>
      )}
    />
  );
};

export default UnidadesMedida;
