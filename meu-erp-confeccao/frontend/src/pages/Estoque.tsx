import React, { useState, useEffect } from 'react';
import { PackageSearch, Plus, Loader2, Info, ArrowUpRight, ArrowDownRight } from 'lucide-react';
import api from '../api/axios';
import Modal from '../components/Modal';

interface Material {
  id: string;
  codigo: string;
  nome: string;
  descricao: string;
  unidadeMedida: string;
  custoUnitario: number;
  quantidadeAtual: number;
}

interface Sku {
  id: string;
  cor: string;
  tamanho: string;
  codigoBarras: string;
  precoVenda: number;
  quantidadeAtual: number;
}

interface ProdutoBase {
  id: string;
  codigo: string;
  nome: string;
  skus: Sku[];
}

const Estoque = () => {
  const [activeTab, setActiveTab] = useState<'MATERIAIS' | 'PRODUTOS'>('MATERIAIS');
  
  // Data
  const [materiais, setMateriais] = useState<Material[]>([]);
  const [produtos, setProdutos] = useState<ProdutoBase[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  // Modals
  const [isMaterialModalOpen, setIsMaterialModalOpen] = useState(false);
  const [isMovimentacaoModalOpen, setIsMovimentacaoModalOpen] = useState(false);

  // Material Form
  const [codigo, setCodigo] = useState('');
  const [nome, setNome] = useState('');
  const [descricao, setDescricao] = useState('');
  const [unidadeMedida, setUnidadeMedida] = useState('KG');
  const [custoUnitario, setCustoUnitario] = useState<string>('');
  const [isSubmitting, setIsSubmitting] = useState(false);

  // Movimentacao Form
  const [movType, setMovType] = useState<'ENTRADA' | 'SAIDA'>('ENTRADA');
  const [movItemId, setMovItemId] = useState(''); // ID do Material ou do SKU
  const [movQtd, setMovQtd] = useState('');
  const [movDoc, setMovDoc] = useState('');

  const fetchData = async () => {
    try {
      setLoading(true);
      const [matRes, prodRes] = await Promise.all([
        api.get('/inventory/materiais'),
        api.get('/catalog/produtos')
      ]);
      setMateriais(matRes.data);
      setProdutos(prodRes.data);
      setError(null);
    } catch (err: any) {
      setError(err.response?.data?.message || 'Erro ao buscar dados.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchData();
  }, []);

  const handleAddMaterial = async (e: React.FormEvent) => {
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
      setIsMaterialModalOpen(false);
      fetchData();
    } catch (err: any) {
      alert(err.response?.data?.message || 'Erro ao cadastrar material.');
    } finally {
      setIsSubmitting(false);
    }
  };

  const handleMovimentacao = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!movItemId || !movQtd) return;

    try {
      setIsSubmitting(true);
      if (activeTab === 'MATERIAIS') {
        await api.post(`/inventory/materiais/${movItemId}/movimentacoes`, {
          tipo: movType,
          quantidade: parseFloat(movQtd),
          documentoReferencia: movDoc
        });
      } else {
        await api.post(`/inventory/produtos-skus/${movItemId}/movimentacoes`, {
          tipo: movType,
          quantidade: parseInt(movQtd, 10),
          documentoReferencia: movDoc
        });
      }
      
      setMovItemId('');
      setMovQtd('');
      setMovDoc('');
      setIsMovimentacaoModalOpen(false);
      fetchData();
    } catch (err: any) {
      alert(err.response?.data?.message || 'Erro ao registrar movimentação.');
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
          <h1 style={{ fontSize: '1.8rem', marginBottom: '0.5rem' }}>Estoque</h1>
          <p style={{ color: 'var(--text-secondary)' }}>Gerencie o estoque de Matérias-Primas e Produtos Acabados (SKUs).</p>
        </div>
        <div style={{ display: 'flex', gap: '1rem' }}>
          <button className="btn-secondary" onClick={() => setIsMovimentacaoModalOpen(true)}>
            <ArrowUpRight size={18} /> Movimentar
          </button>
          {activeTab === 'MATERIAIS' && (
            <button className="btn-primary" onClick={() => setIsMaterialModalOpen(true)}>
              <Plus size={18} /> Novo Material
            </button>
          )}
        </div>
      </div>

      <div style={{ display: 'flex', gap: '1rem', borderBottom: '1px solid var(--border-color)', marginBottom: '2rem' }}>
        <button 
          onClick={() => setActiveTab('MATERIAIS')}
          style={{
            background: 'none', border: 'none', padding: '0.5rem 1rem', fontSize: '1rem', cursor: 'pointer',
            fontWeight: activeTab === 'MATERIAIS' ? 600 : 400,
            color: activeTab === 'MATERIAIS' ? 'var(--accent-primary)' : 'var(--text-secondary)',
            borderBottom: activeTab === 'MATERIAIS' ? '2px solid var(--accent-primary)' : 'none'
          }}
        >
          Matéria-Prima
        </button>
        <button 
          onClick={() => setActiveTab('PRODUTOS')}
          style={{
            background: 'none', border: 'none', padding: '0.5rem 1rem', fontSize: '1rem', cursor: 'pointer',
            fontWeight: activeTab === 'PRODUTOS' ? 600 : 400,
            color: activeTab === 'PRODUTOS' ? 'var(--accent-primary)' : 'var(--text-secondary)',
            borderBottom: activeTab === 'PRODUTOS' ? '2px solid var(--accent-primary)' : 'none'
          }}
        >
          Produto Acabado (SKUs)
        </button>
      </div>

      {/* Tabelas */}
      <div className="glass-card" style={{ padding: 0, overflow: 'hidden' }}>
        {loading ? (
          <div style={{ padding: '3rem', textAlign: 'center', color: 'var(--text-muted)' }}>
            <Loader2 className="animate-spin" size={32} style={{ margin: '0 auto', marginBottom: '1rem' }} />
            Carregando...
          </div>
        ) : error ? (
          <div style={{ padding: '2rem', textAlign: 'center', color: 'var(--danger)' }}>{error}</div>
        ) : activeTab === 'MATERIAIS' ? (
          <div style={{ overflowX: 'auto' }}>
            <table className="premium-table">
              <thead>
                <tr>
                  <th>Código</th>
                  <th>Material</th>
                  <th>Unidade</th>
                  <th>Qtd Atual</th>
                  <th>Custo Unitário</th>
                </tr>
              </thead>
              <tbody>
                {materiais.map(m => (
                  <tr key={m.id}>
                    <td><span className="badge badge-orange">{m.codigo}</span></td>
                    <td style={{ fontWeight: 500 }}>{m.nome}</td>
                    <td style={{ color: 'var(--text-secondary)' }}>{m.unidadeMedida}</td>
                    <td style={{ fontWeight: 600, color: 'var(--accent-primary)' }}>{m.quantidadeAtual || 0}</td>
                    <td style={{ fontWeight: 600, color: 'var(--success)' }}>{formatCurrency(m.custoUnitario)}</td>
                  </tr>
                ))}
                {materiais.length === 0 && (
                  <tr><td colSpan={5} style={{ textAlign: 'center', padding: '2rem' }}>Nenhum material.</td></tr>
                )}
              </tbody>
            </table>
          </div>
        ) : (
          <div style={{ overflowX: 'auto' }}>
            <table className="premium-table">
              <thead>
                <tr>
                  <th>Produto Base</th>
                  <th>SKU (Cor/Tamanho)</th>
                  <th>Código Barras</th>
                  <th>Qtd Atual</th>
                </tr>
              </thead>
              <tbody>
                {produtos.flatMap(p => p.skus.map(sku => (
                  <tr key={sku.id}>
                    <td style={{ fontWeight: 500 }}>{p.codigo} - {p.nome}</td>
                    <td>
                      <span className="badge badge-orange" style={{ marginRight: '0.5rem' }}>{sku.cor}</span>
                      <span className="badge">{sku.tamanho}</span>
                    </td>
                    <td style={{ color: 'var(--text-secondary)' }}>{sku.codigoBarras || '-'}</td>
                    <td style={{ fontWeight: 600, color: 'var(--accent-primary)' }}>{sku.quantidadeAtual || 0}</td>
                  </tr>
                )))}
                {produtos.flatMap(p => p.skus).length === 0 && (
                  <tr><td colSpan={4} style={{ textAlign: 'center', padding: '2rem' }}>Nenhum SKU encontrado. Você precisa cadastrar SKUs nos produtos.</td></tr>
                )}
              </tbody>
            </table>
          </div>
        )}
      </div>

      {/* Modal Cadastro de Material */}
      <Modal isOpen={isMaterialModalOpen} onClose={() => setIsMaterialModalOpen(false)} title="Novo Material" width="500px">
        <form onSubmit={handleAddMaterial} style={{ display: 'flex', flexDirection: 'column', gap: '1.25rem' }}>
          <div>
            <label style={{ display: 'block', marginBottom: '0.5rem', fontSize: '0.9rem', color: 'var(--text-secondary)' }}>Código</label>
            <input type="text" required value={codigo} onChange={(e) => setCodigo(e.target.value)} placeholder="Ex: TEC-01" />
          </div>
          <div>
            <label style={{ display: 'block', marginBottom: '0.5rem', fontSize: '0.9rem', color: 'var(--text-secondary)' }}>Nome do Material</label>
            <input type="text" required value={nome} onChange={(e) => setNome(e.target.value)} placeholder="Ex: Tecido Malha" />
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
              <label style={{ display: 'block', marginBottom: '0.5rem', fontSize: '0.9rem', color: 'var(--text-secondary)' }}>Custo (R$)</label>
              <input type="number" step="0.01" min="0" required value={custoUnitario} onChange={(e) => setCustoUnitario(e.target.value)} />
            </div>
          </div>
          <div>
            <label style={{ display: 'block', marginBottom: '0.5rem', fontSize: '0.9rem', color: 'var(--text-secondary)' }}>Descrição</label>
            <textarea rows={2} value={descricao} onChange={(e) => setDescricao(e.target.value)} />
          </div>
          <button type="submit" className="btn-primary" disabled={isSubmitting}>
            {isSubmitting ? <Loader2 className="animate-spin" size={20} /> : 'Salvar Material'}
          </button>
        </form>
      </Modal>

      {/* Modal Movimentação */}
      <Modal isOpen={isMovimentacaoModalOpen} onClose={() => setIsMovimentacaoModalOpen(false)} title="Movimentação Manual" width="500px">
        <form onSubmit={handleMovimentacao} style={{ display: 'flex', flexDirection: 'column', gap: '1.25rem' }}>
          <div style={{ display: 'flex', gap: '1rem', borderBottom: '1px solid var(--border-color)', paddingBottom: '0.5rem' }}>
            <button 
              type="button"
              onClick={() => setMovType('ENTRADA')}
              style={{
                flex: 1, padding: '0.5rem', borderRadius: 'var(--radius-md)', border: 'none', cursor: 'pointer',
                background: movType === 'ENTRADA' ? 'var(--success)' : 'var(--bg-secondary)',
                color: movType === 'ENTRADA' ? '#fff' : 'var(--text-primary)',
                fontWeight: 600
              }}
            >
              Entrada
            </button>
            <button 
              type="button"
              onClick={() => setMovType('SAIDA')}
              style={{
                flex: 1, padding: '0.5rem', borderRadius: 'var(--radius-md)', border: 'none', cursor: 'pointer',
                background: movType === 'SAIDA' ? 'var(--danger)' : 'var(--bg-secondary)',
                color: movType === 'SAIDA' ? '#fff' : 'var(--text-primary)',
                fontWeight: 600
              }}
            >
              Saída
            </button>
          </div>
          
          <div>
            <label style={{ display: 'block', marginBottom: '0.5rem', fontSize: '0.9rem', color: 'var(--text-secondary)' }}>
              Selecione o Item ({activeTab === 'MATERIAIS' ? 'Matéria-Prima' : 'SKU'})
            </label>
            <select value={movItemId} onChange={e => setMovItemId(e.target.value)} required>
              <option value="">Selecione...</option>
              {activeTab === 'MATERIAIS' ? (
                materiais.map(m => (
                  <option key={m.id} value={m.id}>{m.codigo} - {m.nome} (Atual: {m.quantidadeAtual})</option>
                ))
              ) : (
                produtos.flatMap(p => p.skus.map(sku => (
                  <option key={sku.id} value={sku.id}>
                    {p.codigo} - {sku.cor}/{sku.tamanho} (Atual: {sku.quantidadeAtual})
                  </option>
                )))
              )}
            </select>
          </div>

          <div>
            <label style={{ display: 'block', marginBottom: '0.5rem', fontSize: '0.9rem', color: 'var(--text-secondary)' }}>Quantidade</label>
            <input 
              type="number" 
              step={activeTab === 'MATERIAIS' ? "0.001" : "1"} 
              min="0.001" 
              required 
              value={movQtd} 
              onChange={(e) => setMovQtd(e.target.value)} 
            />
          </div>

          <div>
            <label style={{ display: 'block', marginBottom: '0.5rem', fontSize: '0.9rem', color: 'var(--text-secondary)' }}>Doc. Referência (Opcional)</label>
            <input type="text" value={movDoc} onChange={(e) => setMovDoc(e.target.value)} placeholder="Ex: NF 1234, Contagem, Ajuste..." />
          </div>

          <button type="submit" className="btn-primary" disabled={isSubmitting}>
            {isSubmitting ? <Loader2 className="animate-spin" size={20} /> : 'Confirmar Movimentação'}
          </button>
        </form>
      </Modal>

    </div>
  );
};

export default Estoque;
