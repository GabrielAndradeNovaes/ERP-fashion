import React from 'react';
import CrudPage from '../components/CrudPage';

const Clientes = () => {
  return (
    <CrudPage
      title="Clientes"
      description="Gerencie os clientes do sistema."
      endpoint="/core/clientes"
      columns={[
        { key: 'nome', label: 'Nome/Razão Social' },
        { key: 'documento', label: 'CPF/CNPJ' },
        { key: 'email', label: 'Email' },
        { key: 'telefone', label: 'Telefone' }
      ]}
      emptyEntity={{ nome: '', documento: '', email: '', telefone: '' }}
      renderForm={(entity, setEntity) => (
        <>
          <div>
            <label style={{ display: 'block', marginBottom: '0.5rem', fontSize: '0.9rem' }}>Nome</label>
            <input type="text" required value={entity.nome} onChange={e => setEntity({ ...entity, nome: e.target.value })} />
          </div>
          <div>
            <label style={{ display: 'block', marginBottom: '0.5rem', fontSize: '0.9rem' }}>CPF/CNPJ</label>
            <input type="text" value={entity.documento} onChange={e => setEntity({ ...entity, documento: e.target.value })} />
          </div>
          <div>
            <label style={{ display: 'block', marginBottom: '0.5rem', fontSize: '0.9rem' }}>Email</label>
            <input type="email" value={entity.email} onChange={e => setEntity({ ...entity, email: e.target.value })} />
          </div>
          <div>
            <label style={{ display: 'block', marginBottom: '0.5rem', fontSize: '0.9rem' }}>Telefone</label>
            <input type="text" value={entity.telefone} onChange={e => setEntity({ ...entity, telefone: e.target.value })} />
          </div>
        </>
      )}
    />
  );
};

export default Clientes;
