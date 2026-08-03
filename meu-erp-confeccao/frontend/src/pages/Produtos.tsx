import React, { useState, useEffect } from 'react';
import { Package, Plus, Loader2, Info } from 'lucide-react';
import api from '../api/axios';

interface ProdutoBase {
  id: string;
  codigo: string;
  nome: string;
  descricao: string;
  precoVenda: number;
  precoCusto: number;
}

const Produtos = () => {
  const [produtos, setProdutos] = useState<ProdutoBase[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  // Form State
  const [codigo, setCodigo] = useState('');
  const [nome, setNome] = useState('');
  const [descricao, setDescricao] = useState('');
  const [precoVenda, setPrecoVenda] = useState<string>('');
  const [isSubmitting, setIsSubmitting] = useState(false);

  const fetchProdutos = async () => {
    try {
      setLoading(true);
      const res = await api.get('/catalog/produtos');
      setProdutos(res.data);
      setError(null);
    } catch (err: any) {
      setError(err.response?.data?.message || 'Erro ao buscar produtos.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchProdutos();
  }, []);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!codigo || !nome) return;

    try {
      setIsSubmitting(true);
      await api.post('/catalog/produtos', {
        codigo,
        nome,
        descricao,
        precoVenda: parseFloat(precoVenda) || 0,
        precoCusto: 0, // Custo é preenchido pela ficha técnica
        skus: []
      });
      
      setCodigo('');
      setNome('');
      setDescricao('');
      setPrecoVenda('');
      fetchProdutos();
    } catch (err: any) {
      alert(err.response?.data?.message || 'Erro ao cadastrar produto.');
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
          <h1 style={{ fontSize: '1.8rem', marginBottom: '0.5rem' }}>Cadastro de Produtos</h1>
          <p style={{ color: 'var(--text-secondary)' }}>Cadastre os produtos base (Ex: Calcinha, Sutiã) para criar suas Fichas Técnicas.</p>
        </div>
      </div>

      <div style={{ display: 'grid', gridTemplateColumns: '1fr 2fr', gap: '2rem' }}>
        
        {/* Formulário */}
        <div className="glass-card">
          <h3 style={{ marginBottom: '1.5rem', display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
            <Plus size={18} className="text-accent" />
            Novo Produto
          </h3>
          
          <form onSubmit={handleSubmit} style={{ display: 'flex', flexDirection: 'column', gap: '1.25rem' }}>
            <div>
              <label style={{ display: 'block', marginBottom: '0.5rem', fontSize: '0.9rem', color: 'var(--text-secondary)' }}>
                Código (Referência)
              </label>
              <input 
                type="text" 
                required 
                value={codigo} 
                onChange={(e) => setCodigo(e.target.value)} 
                placeholder="Ex: REF-100"
              />
            </div>
            
            <div>
              <label style={{ display: 'block', marginBottom: '0.5rem', fontSize: '0.9rem', color: 'var(--text-secondary)' }}>
                Nome do Produto
              </label>
              <input 
                type="text" 
                required 
                value={nome} 
                onChange={(e) => setNome(e.target.value)} 
                placeholder="Ex: Calcinha Algodão"
              />
            </div>

            <div>
              <label style={{ display: 'block', marginBottom: '0.5rem', fontSize: '0.9rem', color: 'var(--text-secondary)' }}>
                Preço de Venda (R$)
              </label>
              <input 
                type="number" 
                step="0.01"
                min="0"
                required 
                value={precoVenda} 
                onChange={(e) => setPrecoVenda(e.target.value)} 
                placeholder="Ex: 29.90"
              />
            </div>

            <div>
              <label style={{ display: 'block', marginBottom: '0.5rem', fontSize: '0.9rem', color: 'var(--text-secondary)' }}>
                Descrição (Opcional)
              </label>
              <textarea 
                rows={2}
                value={descricao} 
                onChange={(e) => setDescricao(e.target.value)} 
                placeholder="Detalhes sobre a modelagem ou tecido..."
              />
            </div>

            <button type="submit" className="btn-primary" disabled={isSubmitting} style={{ marginTop: '0.5rem' }}>
              {isSubmitting ? <Loader2 className="animate-spin" size={20} /> : 'Salvar Produto'}
            </button>
          </form>
        </div>

        {/* Tabela */}
        <div className="glass-card" style={{ padding: 0, overflow: 'hidden' }}>
          <div style={{ padding: '1.5rem', borderBottom: '1px solid var(--border-color)', display: 'flex', justifyContent: 'space-between' }}>
            <h3>Produtos Cadastrados</h3>
            <span className="badge badge-green">{produtos.length} Itens</span>
          </div>
          
          {loading ? (
            <div style={{ padding: '3rem', textAlign: 'center', color: 'var(--text-muted)' }}>
              <Loader2 className="animate-spin" size={32} style={{ margin: '0 auto', marginBottom: '1rem' }} />
              Carregando dados...
            </div>
          ) : error ? (
            <div style={{ padding: '2rem', textAlign: 'center', color: 'var(--danger)' }}>{error}</div>
          ) : produtos.length === 0 ? (
            <div style={{ padding: '4rem 2rem', textAlign: 'center', color: 'var(--text-muted)' }}>
              <Info size={32} style={{ margin: '0 auto 1rem', opacity: 0.5 }} />
              <p>Nenhum produto cadastrado.</p>
              <p style={{ fontSize: '0.9rem', marginTop: '0.5rem' }}>Cadastre ao lado para que apareça nas Fichas Técnicas.</p>
            </div>
          ) : (
            <div style={{ overflowX: 'auto' }}>
              <table className="premium-table">
                <thead>
                  <tr>
                    <th>Ref</th>
                    <th>Produto</th>
                    <th>Preço Venda</th>
                    <th>Preço Custo (via Ficha)</th>
                  </tr>
                </thead>
                <tbody>
                  {produtos.map(p => (
                    <tr key={p.id}>
                      <td><span className="badge badge-orange">{p.codigo}</span></td>
                      <td style={{ fontWeight: 500 }}>{p.nome}</td>
                      <td style={{ color: 'var(--accent-primary)', fontWeight: 500 }}>{formatCurrency(p.precoVenda)}</td>
                      <td style={{ color: 'var(--warning)', fontWeight: 500 }}>
                        {p.precoCusto > 0 ? formatCurrency(p.precoCusto) : <span style={{ opacity: 0.5, fontStyle: 'italic' }}>Aguardando Ficha</span>}
                      </td>
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

export default Produtos;
