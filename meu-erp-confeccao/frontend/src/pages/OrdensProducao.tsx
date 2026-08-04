import React, { useState, useEffect } from 'react';
import { PackageSearch, Plus, Loader2, Info, CheckCircle, Play } from 'lucide-react';
import api from '../api/axios';

interface ProdutoBase {
  id: string;
  codigo: string;
  nome: string;
}

interface FichaTecnica {
  id: string;
  descricao: string;
}

interface OrdemProducao {
  id: string;
  numero: string;
  produtoBaseNome: string;
  fichaTecnicaVersao: string;
  quantidade: number;
  status: string;
  criadoEm: string;
}

const OrdensProducao = () => {
  const [ordens, setOrdens] = useState<OrdemProducao[]>([]);
  const [produtos, setProdutos] = useState<ProdutoBase[]>([]);
  const [fichas, setFichas] = useState<FichaTecnica[]>([]);
  const [loading, setLoading] = useState(true);
  
  const [numero, setNumero] = useState('');
  const [produtoBaseId, setProdutoBaseId] = useState('');
  const [fichaTecnicaId, setFichaTecnicaId] = useState('');
  const [quantidade, setQuantidade] = useState('100');
  const [isSubmitting, setIsSubmitting] = useState(false);

  useEffect(() => {
    fetchInitialData();
  }, []);

  const fetchInitialData = async () => {
    try {
      setLoading(true);
      const [ordensRes, produtosRes] = await Promise.all([
        api.get('/production/ordens'),
        api.get('/catalog/produtos')
      ]);
      setOrdens(ordensRes.data);
      setProdutos(produtosRes.data);
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const carregarFichas = async (produtoId: string) => {
    setProdutoBaseId(produtoId);
    setFichaTecnicaId('');
    if (!produtoId) {
      setFichas([]);
      return;
    }
    try {
      const res = await api.get(`/production/fichas-tecnicas/produto/${produtoId}`);
      setFichas(res.data);
    } catch (err) {
      console.error(err);
    }
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!numero || !produtoBaseId || !fichaTecnicaId || !quantidade) return;

    try {
      setIsSubmitting(true);
      await api.post('/production/ordens', {
        numero,
        produtoBaseId,
        fichaTecnicaId,
        quantidade: parseInt(quantidade)
      });
      setNumero('');
      fetchInitialData();
    } catch (err: any) {
      alert(err.response?.data?.message || 'Erro ao criar OP');
    } finally {
      setIsSubmitting(false);
    }
  };

  const handleIniciarProducao = async (id: string) => {
    if (!window.confirm("Iniciar a produção irá realizar a baixa dos materiais no estoque. Confirmar?")) return;
    
    try {
      await api.put(`/production/ordens/${id}/iniciar`);
      fetchInitialData();
      alert("Ordem de Produção iniciada e materiais deduzidos com sucesso!");
    } catch (err: any) {
      alert(err.response?.data?.message || 'Erro ao iniciar OP');
    }
  };

  const getStatusBadge = (status: string) => {
    switch (status) {
      case 'CADASTRADA': return <span className="badge badge-orange">Cadastrada</span>;
      case 'EM_ANDAMENTO': return <span className="badge badge-blue">Em Andamento</span>;
      case 'CONCLUIDA': return <span className="badge badge-green">Concluída</span>;
      default: return <span className="badge">{status}</span>;
    }
  };

  return (
    <div className="animate-fade-in">
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '2rem' }}>
        <div>
          <h1 style={{ fontSize: '1.8rem', marginBottom: '0.5rem' }}>Ordens de Produção (PCP)</h1>
          <p style={{ color: 'var(--text-secondary)' }}>Gerencie as OPs e baixe materiais automaticamente.</p>
        </div>
      </div>

      <div style={{ display: 'grid', gridTemplateColumns: '1fr 2fr', gap: '2rem' }}>
        <div className="glass-card">
          <h3 style={{ marginBottom: '1.5rem', display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
            <Plus size={18} className="text-accent" />
            Nova OP
          </h3>
          <form onSubmit={handleSubmit} style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}>
            <div>
              <label style={{ display: 'block', marginBottom: '0.5rem', fontSize: '0.9rem' }}>Número da OP</label>
              <input type="text" required value={numero} onChange={e => setNumero(e.target.value)} placeholder="OP-001" />
            </div>
            
            <div>
              <label style={{ display: 'block', marginBottom: '0.5rem', fontSize: '0.9rem' }}>Produto Base</label>
              <select required value={produtoBaseId} onChange={e => carregarFichas(e.target.value)}>
                <option value="">Selecione...</option>
                {produtos.map(p => <option key={p.id} value={p.id}>{p.codigo} - {p.nome}</option>)}
              </select>
            </div>

            <div>
              <label style={{ display: 'block', marginBottom: '0.5rem', fontSize: '0.9rem' }}>Ficha Técnica (BOM)</label>
              <select required value={fichaTecnicaId} onChange={e => setFichaTecnicaId(e.target.value)} disabled={!produtoBaseId}>
                <option value="">Selecione...</option>
                {fichas.map(f => <option key={f.id} value={f.id}>{f.descricao}</option>)}
              </select>
            </div>

            <div>
              <label style={{ display: 'block', marginBottom: '0.5rem', fontSize: '0.9rem' }}>Quantidade</label>
              <input type="number" required min="1" value={quantidade} onChange={e => setQuantidade(e.target.value)} />
            </div>

            <button type="submit" className="btn-primary" disabled={isSubmitting}>
              {isSubmitting ? <Loader2 className="animate-spin" size={20} /> : 'Criar OP'}
            </button>
          </form>
        </div>

        <div className="glass-card" style={{ padding: 0, overflow: 'hidden' }}>
          <div style={{ padding: '1.5rem', borderBottom: '1px solid var(--border-color)', display: 'flex', justifyContent: 'space-between' }}>
            <h3>Ordens Cadastradas</h3>
          </div>
          
          {loading ? (
            <div style={{ padding: '3rem', textAlign: 'center' }}><Loader2 className="animate-spin" size={32} /></div>
          ) : ordens.length === 0 ? (
            <div style={{ padding: '4rem 2rem', textAlign: 'center', color: 'var(--text-muted)' }}>
              <Info size={32} style={{ margin: '0 auto 1rem', opacity: 0.5 }} />
              <p>Nenhuma ordem de produção encontrada.</p>
            </div>
          ) : (
            <table className="premium-table">
              <thead>
                <tr>
                  <th>Nº</th>
                  <th>Produto</th>
                  <th>Quantidade</th>
                  <th>Status</th>
                  <th>Ações</th>
                </tr>
              </thead>
              <tbody>
                {ordens.map(op => (
                  <tr key={op.id}>
                    <td style={{ fontWeight: 600 }}>{op.numero}</td>
                    <td>
                      <div>{op.produtoBaseNome}</div>
                      <div style={{ fontSize: '0.8rem', color: 'var(--text-muted)' }}>BOM: {op.fichaTecnicaVersao}</div>
                    </td>
                    <td>{op.quantidade}</td>
                    <td>{getStatusBadge(op.status)}</td>
                    <td>
                      {op.status === 'CADASTRADA' && (
                        <button onClick={() => handleIniciarProducao(op.id)} className="btn-primary" style={{ padding: '0.5rem 1rem', display: 'flex', alignItems: 'center', gap: '0.5rem', fontSize: '0.8rem' }}>
                          <Play size={14} /> Iniciar Produção
                        </button>
                      )}
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          )}
        </div>
      </div>
    </div>
  );
};

export default OrdensProducao;
