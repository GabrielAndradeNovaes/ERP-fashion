import React, { useState, useEffect } from 'react';
import { Package, Plus, Loader2, Info, FileBox } from 'lucide-react';
import api from '../api/axios';
import Modal from '../components/Modal';

interface ProdutoBase {
  id: string;
  codigo: string;
  nome: string;
  descricao: string;
  precoVenda: number;
  precoCusto: number;
  fichaTecnica: any; // Simplified for now
}

interface Material {
  id: string;
  codigo: string;
  nome: string;
  unidadeMedida: string;
  custoUnitario: number;
}

const Produtos = () => {
  const [produtos, setProdutos] = useState<ProdutoBase[]>([]);
  const [estoque, setEstoque] = useState<Material[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  // Modal states
  const [isAddModalOpen, setIsAddModalOpen] = useState(false);
  const [isEditModalOpen, setIsEditModalOpen] = useState(false);
  const [selectedProduto, setSelectedProduto] = useState<ProdutoBase | null>(null);
  const [activeTab, setActiveTab] = useState<'INFO' | 'FICHA' | 'OPERACOES'>('INFO');

  // Form State (Add Product)
  const [codigo, setCodigo] = useState('');
  const [nome, setNome] = useState('');
  const [descricao, setDescricao] = useState('');
  const [precoVenda, setPrecoVenda] = useState<string>('');
  const [isSubmitting, setIsSubmitting] = useState(false);

  // Form State (Add Ficha/Material/Operacao)
  const [selectedMaterialId, setSelectedMaterialId] = useState('');
  const [quantidadeMaterial, setQuantidadeMaterial] = useState('');
  
  const [opNome, setOpNome] = useState('');
  const [opMaquina, setOpMaquina] = useState('');
  const [opOrdem, setOpOrdem] = useState('1');
  const [opFolhas, setOpFolhas] = useState('2');
  const [opParadas, setOpParadas] = useState('1');
  const [opDificuldade, setOpDificuldade] = useState('MEDIO');
  const [opComprimento, setOpComprimento] = useState('DE_0_A_60');

  const fetchInitialData = async () => {
    try {
      setLoading(true);
      const [prodRes, matRes] = await Promise.all([
        api.get('/catalog/produtos'),
        api.get('/inventory/materiais')
      ]);
      setProdutos(prodRes.data);
      setEstoque(matRes.data);
      setError(null);
    } catch (err: any) {
      setError(err.response?.data?.message || 'Erro ao buscar dados.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchInitialData();
  }, []);

  const handleAddProduto = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!codigo || !nome) return;

    try {
      setIsSubmitting(true);
      await api.post('/catalog/produtos', {
        codigo,
        nome,
        descricao,
        precoVenda: parseFloat(precoVenda) || 0,
        precoCusto: 0,
        skus: []
      });
      
      setCodigo('');
      setNome('');
      setDescricao('');
      setPrecoVenda('');
      setIsAddModalOpen(false);
      fetchInitialData();
    } catch (err: any) {
      alert(err.response?.data?.message || 'Erro ao cadastrar produto.');
    } finally {
      setIsSubmitting(false);
    }
  };

  const openEditModal = async (produto: ProdutoBase) => {
    setSelectedProduto(produto);
    setActiveTab('INFO');
    setIsEditModalOpen(true);
    
    // Auto-create Ficha Se não existir
    if (!produto.fichaTecnica) {
      try {
        const res = await api.post('/production/fichas-tecnicas', {
          produtoBaseId: produto.id,
          versao: `V1`,
          observacoes: 'Ficha Técnica Inicial'
        });
        setSelectedProduto({ ...produto, fichaTecnica: res.data });
      } catch (err) {
        console.error("Ficha já existe ou erro", err);
        // Refresh product if it was created from another tab
        const prodRes = await api.get(`/catalog/produtos/${produto.id}`);
        setSelectedProduto(prodRes.data);
      }
    }
  };

  const refreshSelectedProduto = async () => {
    if (!selectedProduto) return;
    try {
      const res = await api.get(`/catalog/produtos/${selectedProduto.id}`);
      setSelectedProduto(res.data);
      fetchInitialData(); // update table
    } catch (err) {
      console.error(err);
    }
  }

  const handleAddOperacao = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!selectedProduto?.fichaTecnica || !opNome) return;

    try {
      setIsSubmitting(true);
      await api.post(`/production/fichas-tecnicas/${selectedProduto.fichaTecnica.id}/operacoes`, {
        nome: opNome,
        maquina: opMaquina,
        ordemExecucao: parseInt(opOrdem),
        quantidadeFolhas: parseInt(opFolhas),
        quantidadeParadas: parseInt(opParadas),
        grauDificuldade: opDificuldade,
        faixaComprimento: opComprimento
      });
      
      setOpNome('');
      setOpMaquina('');
      setOpOrdem((parseInt(opOrdem) + 1).toString());
      await refreshSelectedProduto();
    } catch (err: any) {
      alert(err.response?.data?.message || 'Erro ao adicionar operação.');
    } finally {
      setIsSubmitting(false);
    }
  };

  const handleAddMaterial = async (e: React.FormEvent) => {
    e.preventDefault();
    alert('Função de adicionar material (endpoint) precisa ser desenvolvida no backend.');
  };

  const formatCurrency = (value: number) => {
    if (value === undefined || value === null) return 'R$ 0,00';
    return new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' }).format(value);
  };

  return (
    <div className="animate-fade-in">
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '2rem' }}>
        <div>
          <h1 style={{ fontSize: '1.8rem', marginBottom: '0.5rem' }}>Produtos</h1>
          <p style={{ color: 'var(--text-secondary)' }}>Gerencie os produtos e suas Fichas Técnicas integradas.</p>
        </div>
        <button className="btn-primary" onClick={() => setIsAddModalOpen(true)}>
          <Plus size={18} /> Novo Produto
        </button>
      </div>

      <div className="glass-card" style={{ padding: 0, overflow: 'hidden' }}>
        <div style={{ padding: '1.5rem', borderBottom: '1px solid var(--border-color)', display: 'flex', justifyContent: 'space-between' }}>
          <h3>Catálogo</h3>
          <span className="badge badge-green">{produtos.length} Itens</span>
        </div>
        
        {loading ? (
          <div style={{ padding: '3rem', textAlign: 'center', color: 'var(--text-muted)' }}>
            <Loader2 className="animate-spin" size={32} style={{ margin: '0 auto', marginBottom: '1rem' }} />
          </div>
        ) : error ? (
          <div style={{ padding: '2rem', textAlign: 'center', color: 'var(--danger)' }}>{error}</div>
        ) : produtos.length === 0 ? (
          <div style={{ padding: '4rem 2rem', textAlign: 'center', color: 'var(--text-muted)' }}>
            <Info size={32} style={{ margin: '0 auto 1rem', opacity: 0.5 }} />
            <p>Nenhum produto cadastrado.</p>
          </div>
        ) : (
          <div style={{ overflowX: 'auto' }}>
            <table className="premium-table">
              <thead>
                <tr>
                  <th>Ref</th>
                  <th>Produto</th>
                  <th>Preço Venda</th>
                  <th>Custo Produção</th>
                  <th>Status Ficha</th>
                </tr>
              </thead>
              <tbody>
                {produtos.map(p => (
                  <tr key={p.id} onClick={() => openEditModal(p)} style={{ cursor: 'pointer' }}>
                    <td><span className="badge badge-orange">{p.codigo}</span></td>
                    <td style={{ fontWeight: 500 }}>{p.nome}</td>
                    <td style={{ color: 'var(--accent-primary)', fontWeight: 500 }}>{formatCurrency(p.precoVenda)}</td>
                    <td style={{ color: 'var(--warning)', fontWeight: 500 }}>
                      {p.precoCusto > 0 ? formatCurrency(p.precoCusto) : '-'}
                    </td>
                    <td>
                      {p.fichaTecnica ? <span className="badge badge-green">Preenchida</span> : <span className="badge">Pendente</span>}
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </div>

      {/* Modal Adicionar Produto */}
      <Modal isOpen={isAddModalOpen} onClose={() => setIsAddModalOpen(false)} title="Novo Produto" width="500px">
        <form onSubmit={handleAddProduto} style={{ display: 'flex', flexDirection: 'column', gap: '1.25rem' }}>
          <div>
            <label style={{ display: 'block', marginBottom: '0.5rem', fontSize: '0.9rem', color: 'var(--text-secondary)' }}>Código (Referência)</label>
            <input type="text" required value={codigo} onChange={(e) => setCodigo(e.target.value)} placeholder="Ex: REF-100" />
          </div>
          <div>
            <label style={{ display: 'block', marginBottom: '0.5rem', fontSize: '0.9rem', color: 'var(--text-secondary)' }}>Nome do Produto</label>
            <input type="text" required value={nome} onChange={(e) => setNome(e.target.value)} placeholder="Ex: Calcinha Algodão" />
          </div>
          <div>
            <label style={{ display: 'block', marginBottom: '0.5rem', fontSize: '0.9rem', color: 'var(--text-secondary)' }}>Preço de Venda (R$)</label>
            <input type="number" step="0.01" min="0" required value={precoVenda} onChange={(e) => setPrecoVenda(e.target.value)} />
          </div>
          <div>
            <label style={{ display: 'block', marginBottom: '0.5rem', fontSize: '0.9rem', color: 'var(--text-secondary)' }}>Descrição (Opcional)</label>
            <textarea rows={2} value={descricao} onChange={(e) => setDescricao(e.target.value)} />
          </div>
          <button type="submit" className="btn-primary" disabled={isSubmitting} style={{ marginTop: '0.5rem' }}>
            {isSubmitting ? <Loader2 className="animate-spin" size={20} /> : 'Salvar Produto'}
          </button>
        </form>
      </Modal>

      {/* Modal Editar Produto / Ficha Técnica */}
      <Modal isOpen={isEditModalOpen} onClose={() => setIsEditModalOpen(false)} title={`Editar: ${selectedProduto?.nome}`} width="800px">
        {selectedProduto && (
          <div>
            <div style={{ display: 'flex', gap: '1rem', borderBottom: '1px solid var(--border-color)', paddingBottom: '0.5rem', marginBottom: '1.5rem' }}>
              <button 
                onClick={() => setActiveTab('INFO')}
                style={{
                  background: 'none', border: 'none', padding: '0.5rem 1rem', fontSize: '1rem', cursor: 'pointer',
                  fontWeight: activeTab === 'INFO' ? 600 : 400,
                  color: activeTab === 'INFO' ? 'var(--accent-primary)' : 'var(--text-secondary)',
                  borderBottom: activeTab === 'INFO' ? '2px solid var(--accent-primary)' : 'none'
                }}
              >
                Informações
              </button>
              <button 
                onClick={() => setActiveTab('FICHA')}
                style={{
                  background: 'none', border: 'none', padding: '0.5rem 1rem', fontSize: '1rem', cursor: 'pointer',
                  fontWeight: activeTab === 'FICHA' ? 600 : 400,
                  color: activeTab === 'FICHA' ? 'var(--accent-primary)' : 'var(--text-secondary)',
                  borderBottom: activeTab === 'FICHA' ? '2px solid var(--accent-primary)' : 'none'
                }}
              >
                Materiais (BOM)
              </button>
              <button 
                onClick={() => setActiveTab('OPERACOES')}
                style={{
                  background: 'none', border: 'none', padding: '0.5rem 1rem', fontSize: '1rem', cursor: 'pointer',
                  fontWeight: activeTab === 'OPERACOES' ? 600 : 400,
                  color: activeTab === 'OPERACOES' ? 'var(--accent-primary)' : 'var(--text-secondary)',
                  borderBottom: activeTab === 'OPERACOES' ? '2px solid var(--accent-primary)' : 'none'
                }}
              >
                Operações de Costura
              </button>
            </div>

            {activeTab === 'INFO' && (
              <div style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}>
                <p><strong>Ref:</strong> {selectedProduto.codigo}</p>
                <p><strong>Descrição:</strong> {selectedProduto.descricao}</p>
                <p><strong>Preço Venda:</strong> {formatCurrency(selectedProduto.precoVenda)}</p>
                <p style={{ color: 'var(--text-secondary)', fontSize: '0.9rem' }}>A edição completa das informações do produto será disponibilizada em breve.</p>
              </div>
            )}

            {activeTab === 'FICHA' && selectedProduto.fichaTecnica && (
              <div>
                <div style={{ marginBottom: '1.5rem', display: 'flex', gap: '1rem' }}>
                  <div style={{ flex: 1, padding: '1rem', background: 'var(--bg-secondary)', borderRadius: 'var(--radius-md)' }}>
                    <span style={{ fontSize: '0.85rem', color: 'var(--text-secondary)' }}>Custo Total Materiais</span>
                    <div style={{ fontSize: '1.5rem', fontWeight: 600, color: 'var(--success)' }}>
                      {formatCurrency(selectedProduto.fichaTecnica.custoTotalMateriais)}
                    </div>
                  </div>
                </div>

                <form onSubmit={handleAddMaterial} style={{ display: 'flex', gap: '1rem', alignItems: 'flex-end', marginBottom: '1.5rem' }}>
                  <div style={{ flex: 1 }}>
                    <label style={{ display: 'block', marginBottom: '0.5rem', fontSize: '0.9rem' }}>Material</label>
                    <select value={selectedMaterialId} onChange={e => setSelectedMaterialId(e.target.value)} required>
                      <option value="">Selecione...</option>
                      {estoque.map(m => (
                        <option key={m.id} value={m.id}>{m.codigo} - {m.nome}</option>
                      ))}
                    </select>
                  </div>
                  <div>
                    <label style={{ display: 'block', marginBottom: '0.5rem', fontSize: '0.9rem' }}>Qtd Gasta</label>
                    <input type="number" step="0.001" min="0" required value={quantidadeMaterial} onChange={e => setQuantidadeMaterial(e.target.value)} placeholder="0.120" style={{ width: '120px' }} />
                  </div>
                  <button type="submit" className="btn-primary" disabled={isSubmitting}>Incluir</button>
                </form>

                <table className="premium-table">
                  <thead><tr><th>Material</th><th>Qtd</th><th>Un</th></tr></thead>
                  <tbody>
                    {selectedProduto.fichaTecnica.materiais?.map((m: any) => (
                      <tr key={m.id}>
                        <td>{m.materialNome}</td>
                        <td style={{ fontWeight: 600 }}>{m.quantidade}</td>
                        <td style={{ color: 'var(--text-secondary)' }}>{m.unidadeMedida}</td>
                      </tr>
                    ))}
                    {(!selectedProduto.fichaTecnica.materiais || selectedProduto.fichaTecnica.materiais.length === 0) && (
                      <tr><td colSpan={3} style={{ textAlign: 'center', color: 'var(--text-muted)' }}>Sem materiais.</td></tr>
                    )}
                  </tbody>
                </table>
              </div>
            )}

            {activeTab === 'OPERACOES' && selectedProduto.fichaTecnica && (
              <div>
                 <div style={{ marginBottom: '1.5rem', display: 'flex', gap: '1rem' }}>
                  <div style={{ flex: 1, padding: '1rem', background: 'var(--bg-secondary)', borderRadius: 'var(--radius-md)' }}>
                    <span style={{ fontSize: '0.85rem', color: 'var(--text-secondary)' }}>Tempo TPP Total</span>
                    <div style={{ fontSize: '1.5rem', fontWeight: 600, color: 'var(--warning)' }}>
                      {selectedProduto.fichaTecnica.tempoPadraoTotalCentesimal || 0} <span style={{ fontSize: '1rem' }}>min</span>
                    </div>
                  </div>
                </div>

                <form onSubmit={handleAddOperacao} style={{ display: 'flex', flexDirection: 'column', gap: '1rem', marginBottom: '1.5rem', padding: '1rem', background: 'var(--bg-secondary)', borderRadius: 'var(--radius-md)' }}>
                  <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '1rem' }}>
                    <div>
                      <label style={{ display: 'block', marginBottom: '0.25rem', fontSize: '0.85rem' }}>Operação</label>
                      <input type="text" value={opNome} onChange={e => setOpNome(e.target.value)} required />
                    </div>
                    <div>
                      <label style={{ display: 'block', marginBottom: '0.25rem', fontSize: '0.85rem' }}>Máquina</label>
                      <input type="text" value={opMaquina} onChange={e => setOpMaquina(e.target.value)} />
                    </div>
                  </div>
                  <div style={{ display: 'grid', gridTemplateColumns: 'repeat(5, 1fr)', gap: '0.5rem', alignItems: 'flex-end' }}>
                    <div>
                      <label style={{ display: 'block', marginBottom: '0.25rem', fontSize: '0.85rem' }}>Ordem</label>
                      <input type="number" required value={opOrdem} onChange={e => setOpOrdem(e.target.value)} />
                    </div>
                    <div>
                      <label style={{ display: 'block', marginBottom: '0.25rem', fontSize: '0.85rem' }}>Folhas</label>
                      <input type="number" required value={opFolhas} onChange={e => setOpFolhas(e.target.value)} />
                    </div>
                    <div>
                      <label style={{ display: 'block', marginBottom: '0.25rem', fontSize: '0.85rem' }}>Paradas</label>
                      <input type="number" required value={opParadas} onChange={e => setOpParadas(e.target.value)} />
                    </div>
                    <div>
                      <label style={{ display: 'block', marginBottom: '0.25rem', fontSize: '0.85rem' }}>Comp.</label>
                      <select value={opComprimento} onChange={e => setOpComprimento(e.target.value)}>
                        <option value="DE_0_A_60">0-60</option>
                        <option value="DE_61_A_90">61-90</option>
                      </select>
                    </div>
                    <div>
                      <button type="submit" className="btn-primary" disabled={isSubmitting} style={{ width: '100%' }}>+</button>
                    </div>
                  </div>
                </form>

                <table className="premium-table">
                  <thead><tr><th>Ordem</th><th>Op</th><th>Máq</th><th>Tempo</th></tr></thead>
                  <tbody>
                    {selectedProduto.fichaTecnica.operacoes?.sort((a:any, b:any) => a.ordemExecucao - b.ordemExecucao).map((op: any) => (
                      <tr key={op.id}>
                        <td>{op.ordemExecucao}</td>
                        <td style={{ fontWeight: 500 }}>{op.nome}</td>
                        <td style={{ color: 'var(--text-secondary)' }}>{op.maquina}</td>
                        <td style={{ fontWeight: 600, color: 'var(--warning)' }}>{op.tempoCalculadoCentesimal}m</td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            )}
          </div>
        )}
      </Modal>

    </div>
  );
};

export default Produtos;
