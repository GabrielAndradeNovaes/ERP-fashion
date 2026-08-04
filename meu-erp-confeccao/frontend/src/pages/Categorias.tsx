import React from 'react';
import CrudPage from '../components/CrudPage';

const Categorias = () => {
  return (
    <CrudPage
      title="Categorias"
      description="Gerencie as categorias de produtos e materiais."
      endpoint="/core/categorias"
      columns={[
        { key: 'nome', label: 'Nome' },
        { key: 'tipo', label: 'Tipo' }
      ]}
      emptyEntity={{ nome: '', tipo: 'PRODUTO' }}
      renderForm={(entity, setEntity) => (
        <>
          <div>
            <label style={{ display: 'block', marginBottom: '0.5rem', fontSize: '0.9rem' }}>Nome</label>
            <input type="text" required value={entity.nome} onChange={e => setEntity({ ...entity, nome: e.target.value })} />
          </div>
          <div>
            <label style={{ display: 'block', marginBottom: '0.5rem', fontSize: '0.9rem' }}>Tipo</label>
            <select value={entity.tipo} onChange={e => setEntity({ ...entity, tipo: e.target.value })} required>
              <option value="PRODUTO">Produto Acabado</option>
              <option value="MATERIAL">Matéria-Prima</option>
              <option value="SERVICO">Serviço</option>
            </select>
          </div>
        </>
      )}
    />
  );
};

export default Categorias;
