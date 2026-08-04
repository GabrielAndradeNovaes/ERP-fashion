import React, { useState, useEffect } from 'react';
import { Plus, Loader2, Info, Search, Edit2, Trash2 } from 'lucide-react';
import api from '../api/axios';
import Modal from './Modal';

interface CrudPageProps {
  title: string;
  description: string;
  endpoint: string;
  columns: { key: string; label: string; format?: (val: any) => string }[];
  emptyEntity: any;
  renderForm: (entity: any, setEntity: (val: any) => void) => React.ReactNode;
}

const CrudPage: React.FC<CrudPageProps> = ({ title, description, endpoint, columns, emptyEntity, renderForm }) => {
  const [data, setData] = useState<any[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const [isModalOpen, setIsModalOpen] = useState(false);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [currentEntity, setCurrentEntity] = useState<any>(emptyEntity);
  const [search, setSearch] = useState('');

  const fetchData = async () => {
    try {
      setLoading(true);
      const res = await api.get(endpoint);
      setData(res.data);
      setError(null);
    } catch (err: any) {
      setError(err.response?.data?.message || 'Erro ao carregar dados.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchData();
  }, [endpoint]);

  const handleSave = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      setIsSubmitting(true);
      if (currentEntity.id) {
        await api.put(`${endpoint}/${currentEntity.id}`, currentEntity);
      } else {
        await api.post(endpoint, currentEntity);
      }
      setIsModalOpen(false);
      fetchData();
    } catch (err: any) {
      alert(err.response?.data?.message || 'Erro ao salvar.');
    } finally {
      setIsSubmitting(false);
    }
  };

  const handleEdit = (item: any) => {
    setCurrentEntity(item);
    setIsModalOpen(true);
  };

  const handleDelete = async (id: string) => {
    if (!confirm('Deseja realmente excluir este registro?')) return;
    try {
      await api.delete(`${endpoint}/${id}`);
      fetchData();
    } catch (err: any) {
      alert(err.response?.data?.message || 'Erro ao excluir.');
    }
  };

  const openNewModal = () => {
    setCurrentEntity(emptyEntity);
    setIsModalOpen(true);
  };

  const filteredData = data.filter((item: any) => 
    Object.values(item).some(val => String(val).toLowerCase().includes(search.toLowerCase()))
  );

  return (
    <div className="animate-fade-in">
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '2rem' }}>
        <div>
          <h1 style={{ fontSize: '1.8rem', marginBottom: '0.5rem' }}>{title}</h1>
          <p style={{ color: 'var(--text-secondary)' }}>{description}</p>
        </div>
        <button className="btn-primary" onClick={openNewModal}>
          <Plus size={18} /> Novo Cadastro
        </button>
      </div>

      <div className="glass-card" style={{ padding: 0, overflow: 'hidden' }}>
        <div style={{ padding: '1.5rem', borderBottom: '1px solid var(--border-color)', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
          <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', background: 'var(--bg-secondary)', padding: '0.5rem 1rem', borderRadius: 'var(--radius-md)' }}>
            <Search size={18} color="var(--text-secondary)" />
            <input 
              type="text" 
              placeholder="Pesquisar..." 
              value={search} 
              onChange={e => setSearch(e.target.value)}
              style={{ border: 'none', background: 'transparent', outline: 'none', color: 'var(--text-primary)' }}
            />
          </div>
          <span className="badge badge-green">{filteredData.length} Itens</span>
        </div>
        
        {loading ? (
          <div style={{ padding: '3rem', textAlign: 'center', color: 'var(--text-muted)' }}>
            <Loader2 className="animate-spin" size={32} style={{ margin: '0 auto', marginBottom: '1rem' }} />
          </div>
        ) : error ? (
          <div style={{ padding: '2rem', textAlign: 'center', color: 'var(--danger)' }}>{error}</div>
        ) : filteredData.length === 0 ? (
          <div style={{ padding: '4rem 2rem', textAlign: 'center', color: 'var(--text-muted)' }}>
            <Info size={32} style={{ margin: '0 auto 1rem', opacity: 0.5 }} />
            <p>Nenhum registro encontrado.</p>
          </div>
        ) : (
          <div style={{ overflowX: 'auto' }}>
            <table className="premium-table">
              <thead>
                <tr>
                  {columns.map(col => <th key={col.key}>{col.label}</th>)}
                  <th style={{ width: '100px', textAlign: 'center' }}>Ações</th>
                </tr>
              </thead>
              <tbody>
                {filteredData.map((item: any) => (
                  <tr key={item.id}>
                    {columns.map(col => (
                      <td key={col.key}>
                        {col.format ? col.format(item[col.key]) : item[col.key]}
                      </td>
                    ))}
                    <td>
                      <div style={{ display: 'flex', gap: '0.5rem', justifyContent: 'center' }}>
                        <button onClick={() => handleEdit(item)} style={{ background: 'transparent', border: 'none', cursor: 'pointer', color: 'var(--accent-primary)' }}>
                          <Edit2 size={16} />
                        </button>
                        <button onClick={() => handleDelete(item.id)} style={{ background: 'transparent', border: 'none', cursor: 'pointer', color: 'var(--danger)' }}>
                          <Trash2 size={16} />
                        </button>
                      </div>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </div>

      <Modal isOpen={isModalOpen} onClose={() => setIsModalOpen(false)} title={currentEntity.id ? `Editar ${title}` : `Novo ${title}`} width="600px">
        <form onSubmit={handleSave} style={{ display: 'flex', flexDirection: 'column', gap: '1.25rem' }}>
          {renderForm(currentEntity, setCurrentEntity)}
          <button type="submit" className="btn-primary" disabled={isSubmitting} style={{ marginTop: '0.5rem' }}>
            {isSubmitting ? <Loader2 className="animate-spin" size={20} /> : 'Salvar'}
          </button>
        </form>
      </Modal>
    </div>
  );
};

export default CrudPage;
