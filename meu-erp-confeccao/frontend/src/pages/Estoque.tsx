import React, { useState, useEffect } from 'react';
import { PackageSearch, Plus, Trash2, Loader2, Info } from 'lucide-react';
import api from '../api/axios';

interface Material {
  id: string;
  codigo: string;
  nome: string;
  descricao: string;
  unidadeMedida: string;
  custoUnitario: number;
}

const Estoque = () => {
  const [materiais, setMateriais] = useState<Material[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  // Form State
  const [codigo, setCodigo] = useState('');
  const [nome, setNome] = useState('');
  const [descricao, setDescricao] = useState('');
  const [unidadeMedida, setUnidadeMedida] = useState('KG');
  const [custoUnitario, setCustoUnitario] = useState<string>('');
  const [isSubmitting, setIsSubmitting] = useState(false);

  const fetchMateriais = async () => {
    try {
      setLoading(true);
      const res = await api.get('/inventory/materiais');
      setMateriais(res.data);
      setError(null);
    } catch (err: any) {
      setError(err.response?.data?.message || 'Erro ao buscar materiais.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchMateriais();
  }, []);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!codigo || !nome) return;

    try {
      setIsSubmitting(true);
      await api.post('/inventory/materiais', {
        codigo,
        nome,
        descricao,
        unidadeMedida,
        custoUnitario: parseFloat(custoUnitario) || 0
      });
      
      setCodigo('');
      setNome('');
      setDescricao('');
      setCustoUnitario('');
      fetchMateriais();
    } catch (err: any) {
      alert(err.response?.data?.message || 'Erro ao cadastrar material.');
    } finally {
      setIsSubmitting(false);
    }
  };

  const formatCurrency = (value: number) => {
    if (value === undefined || value === null) return 'R$ 0,00';
    return new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' }).format(value);
  };

  return (
    <div className="animate-fade-in">
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '2rem' }}>
        <div>
          <h1 style={{ fontSize: '1.8rem', marginBottom: '0.5rem' }}>Estoque de Matérias Primas</h1>
          <p style={{ color: 'var(--text-secondary)' }}>Cadastre os tecidos, aviamentos e custos unitários.</p>
        </div>
      </div>

      <div style={{ display: 'grid', gridTemplateColumns: '1fr 2fr', gap: '2rem' }}>
        
        {/* Formulário */}
        <div className="glass-card">
          <h3 style={{ marginBottom: '1.5rem', display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
            <Plus size={18} className="text-accent" />
            Novo Material
          </h3>
          
          <form onSubmit={handleSubmit} style={{ display: 'flex', flexDirection: 'column', gap: '1.25rem' }}>
            <div>
              <label style={{ display: 'block', marginBottom: '0.5rem', fontSize: '0.9rem', color: 'var(--text-secondary)' }}>
                Código
              </label>
              <input 
                type="text" 
                required 
                value={codigo} 
                onChange={(e) => setCodigo(e.target.value)} 
                placeholder="Ex: TEC-ROM-01"
              />
            </div>
            
            <div>
              <label style={{ display: 'block', marginBottom: '0.5rem', fontSize: '0.9rem', color: 'var(--text-secondary)' }}>
                Nome do Material
              </label>
              <input 
                type="text" 
                required 
                value={nome} 
                onChange={(e) => setNome(e.target.value)} 
                placeholder="Ex: Tecido Romantic"
              />
            </div>

            <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '1rem' }}>
              <div>
                <label style={{ display: 'block', marginBottom: '0.5rem', fontSize: '0.9rem', color: 'var(--text-secondary)' }}>Medida</label>
                <select value={unidadeMedida} onChange={e => setUnidadeMedida(e.target.value)}>
                  <option value="KG">Quilo (kg)</option>
                  <option value="METRO">Metro (m)</option>
                  <option value="UNIDADE">Unidade (un)</option>
                  <option value="GRAMA">Grama (g)</option>
                </select>
              </div>

              <div>
                <label style={{ display: 'block', marginBottom: '0.5rem', fontSize: '0.9rem', color: 'var(--text-secondary)' }}>
                  Custo (R$)
                </label>
                <input 
                  type="number" 
                  step="0.01"
                  min="0"
                  required 
                  value={custoUnitario} 
                  onChange={(e) => setCustoUnitario(e.target.value)} 
                  placeholder="Ex: 40.00"
                />
              </div>
            </div>

            <div>
              <label style={{ display: 'block', marginBottom: '0.5rem', fontSize: '0.9rem', color: 'var(--text-secondary)' }}>
                Descrição (Opcional)
              </label>
              <textarea 
                rows={2}
                value={descricao} 
                onChange={(e) => setDescricao(e.target.value)} 
                placeholder="Detalhes adicionais..."
              />
            </div>

            <button type="submit" className="btn-primary" disabled={isSubmitting} style={{ marginTop: '0.5rem' }}>
              {isSubmitting ? <Loader2 className="animate-spin" size={20} /> : 'Salvar Material'}
            </button>
          </form>
        </div>

        {/* Tabela */}
        <div className="glass-card" style={{ padding: 0, overflow: 'hidden' }}>
          <div style={{ padding: '1.5rem', borderBottom: '1px solid var(--border-color)', display: 'flex', justifyContent: 'space-between' }}>
            <h3>Materiais Cadastrados</h3>
            <span className="badge badge-green">{materiais.length} Itens</span>
          </div>
          
          {loading ? (
            <div style={{ padding: '3rem', textAlign: 'center', color: 'var(--text-muted)' }}>
              <Loader2 className="animate-spin" size={32} style={{ margin: '0 auto', marginBottom: '1rem' }} />
              Carregando dados...
            </div>
          ) : error ? (
            <div style={{ padding: '2rem', textAlign: 'center', color: 'var(--danger)' }}>{error}</div>
          ) : materiais.length === 0 ? (
            <div style={{ padding: '4rem 2rem', textAlign: 'center', color: 'var(--text-muted)' }}>
              <Info size={32} style={{ margin: '0 auto 1rem', opacity: 0.5 }} />
              <p>O estoque está vazio.</p>
              <p style={{ fontSize: '0.9rem', marginTop: '0.5rem' }}>Utilize o formulário ao lado para começar.</p>
            </div>
          ) : (
            <div style={{ overflowX: 'auto' }}>
              <table className="premium-table">
                <thead>
                  <tr>
                    <th>Código</th>
                    <th>Material</th>
                    <th>Unidade</th>
                    <th>Custo Unitário</th>
                  </tr>
                </thead>
                <tbody>
                  {materiais.map(m => (
                    <tr key={m.id}>
                      <td><span className="badge badge-orange">{m.codigo}</span></td>
                      <td style={{ fontWeight: 500 }}>{m.nome}</td>
                      <td style={{ color: 'var(--text-secondary)' }}>{m.unidadeMedida}</td>
                      <td style={{ fontWeight: 600, color: 'var(--success)' }}>{formatCurrency(m.custoUnitario)}</td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}
        </div>
      </div>
    </div>
  );
};

export default Estoque;
