import React, { useState, useEffect } from 'react';
import { Scissors, Plus, Search, Loader2, Info, Layers, Beaker, FileBox } from 'lucide-react';
import api from '../api/axios';

interface Material {
  id: string;
  codigo: string;
  nome: string;
  unidadeMedida: string;
  custoUnitario: number;
}

interface ProdutoBase {
  id: string;
  codigo: string;
  nome: string;
  precoVenda: number;
  precoCusto: number;
}

interface FichaTecnica {
  id: string;
  produtoBaseId: string;
  produtoBaseNome: string;
  versao: string;
  observacoes: string;
  tempoPadraoTotalCentesimal: number;
  custoTotalMateriais: number;
  materiais: any[];
  operacoes: any[];
}

const FichasTecnicas = () => {
  const [fichas, setFichas] = useState<FichaTecnica[]>([]);
  const [produtos, setProdutos] = useState<ProdutoBase[]>([]);
  const [estoque, setEstoque] = useState<Material[]>([]);
  const [loading, setLoading] = useState(true);
  
  // Selection state
  const [selectedProduto, setSelectedProduto] = useState<string>('');
  const [activeFicha, setActiveFicha] = useState<FichaTecnica | null>(null);
  const [activeTab, setActiveTab] = useState<'MATERIAIS' | 'OPERACOES'>('MATERIAIS');

  // Forms state
  const [isSubmitting, setIsSubmitting] = useState(false);

  // Material Form
  const [selectedMaterialId, setSelectedMaterialId] = useState('');
  const [quantidadeMaterial, setQuantidadeMaterial] = useState('');

  // Operacao Form
  const [opNome, setOpNome] = useState('');
  const [opMaquina, setOpMaquina] = useState('');
  const [opOrdem, setOpOrdem] = useState('1');
  const [opFolhas, setOpFolhas] = useState('2');
  const [opParadas, setOpParadas] = useState('1');
  const [opDificuldade, setOpDificuldade] = useState('MEDIO');
  const [opComprimento, setOpComprimento] = useState('DE_0_A_60');

  useEffect(() => {
    fetchInitialData();
  }, []);

  const fetchInitialData = async () => {
    try {
      setLoading(true);
      const [prodRes, matRes] = await Promise.all([
        api.get('/catalog/produtos'),
        api.get('/inventory/materiais')
      ]);
      setProdutos(prodRes.data);
      setEstoque(matRes.data);
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const loadFichasByProduto = async (produtoId: string) => {
    setSelectedProduto(produtoId);
    setActiveFicha(null);
    if (!produtoId) return;

    try {
      setLoading(true);
      const res = await api.get(`/production/fichas-tecnicas/produto/${produtoId}`);
      setFichas(res.data);
      if (res.data.length > 0) {
        setActiveFicha(res.data[0]);
      }
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const createNovaFicha = async () => {
    if (!selectedProduto) return;
    try {
      setIsSubmitting(true);
      const res = await api.post('/production/fichas-tecnicas', {
        produtoBaseId: selectedProduto,
        versao: `V${fichas.length + 1}`,
        observacoes: 'Nova Ficha Técnica'
      });
      setFichas([...fichas, res.data]);
      setActiveFicha(res.data);
    } catch (err) {
      console.error(err);
      alert('Erro ao criar Ficha Técnica');
    } finally {
      setIsSubmitting(false);
    }
  };

  const refreshFicha = async (id: string) => {
    try {
      const res = await api.get(`/production/fichas-tecnicas/${id}`);
      setActiveFicha(res.data);
      setFichas(fichas.map(f => f.id === id ? res.data : f));
    } catch (err) {
      console.error(err);
    }
  };

  const handleAddMaterial = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!activeFicha || !selectedMaterialId || !quantidadeMaterial) return;

    // As our current API in FichaTecnicaService doesn't have an "addMaterial" endpoint individually, 
    // we would ideally have it. For now, we will simulate a full update or prompt that this feature needs the endpoint.
    alert('Endpoint de adicionar material isolado será mapeado no backend, mas a estrutura já está pronta!');
    // A future implementation would be: 
    // await api.post(`/production/fichas-tecnicas/${activeFicha.id}/materiais`, { materialId, quantidade })
    // refreshFicha(activeFicha.id)
  };

  const handleAddOperacao = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!activeFicha || !opNome) return;

    try {
      setIsSubmitting(true);
      await api.post(`/production/fichas-tecnicas/${activeFicha.id}/operacoes`, {
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
      await refreshFicha(activeFicha.id);
    } catch (err: any) {
      alert(err.response?.data?.message || 'Erro ao adicionar operação. Verifique se o Tempo Padrão existe na Tabela de Tempos.');
    } finally {
      setIsSubmitting(false);
    }
  };

  const formatCurrency = (val: number) => {
    if (val === undefined || val === null) return 'R$ 0,00';
    return new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' }).format(val);
  };

  return (
    <div className="animate-fade-in">
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '2rem' }}>
        <div>
          <h1 style={{ fontSize: '1.8rem', marginBottom: '0.5rem' }}>Fichas Técnicas (BOM & Operações)</h1>
          <p style={{ color: 'var(--text-secondary)' }}>Gerencie matérias-primas e a cronoanálise de produção.</p>
        </div>
      </div>

      {/* Seletor de Produto */}
      <div className="glass-card" style={{ marginBottom: '2rem', display: 'flex', gap: '1rem', alignItems: 'flex-end' }}>
        <div style={{ flex: 1 }}>
          <label style={{ display: 'block', marginBottom: '0.5rem', fontSize: '0.9rem', color: 'var(--text-secondary)' }}>Selecione um Produto</label>
          <select 
            value={selectedProduto} 
            onChange={e => loadFichasByProduto(e.target.value)}
          >
            <option value="">-- Escolha um Produto --</option>
            {produtos.map(p => (
              <option key={p.id} value={p.id}>{p.codigo} - {p.nome}</option>
            ))}
          </select>
        </div>
        <button className="btn-primary" disabled={!selectedProduto || isSubmitting} onClick={createNovaFicha}>
          <Plus size={18} />
          Nova Ficha
        </button>
      </div>

      {loading && !activeFicha ? (
        <div style={{ padding: '3rem', textAlign: 'center' }}>
          <Loader2 className="animate-spin" size={32} style={{ margin: '0 auto', color: 'var(--accent-primary)' }} />
        </div>
      ) : activeFicha ? (
        <div style={{ display: 'grid', gridTemplateColumns: '1fr', gap: '1.5rem' }}>
          
          {/* Header da Ficha (Totais) */}
          <div className="glass-card" style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
            <div>
              <h2 style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
                <FileBox size={24} className="text-accent" />
                Ficha Técnica: {activeFicha.produtoBaseNome} ({activeFicha.versao})
              </h2>
              <p style={{ color: 'var(--text-secondary)', marginTop: '0.25rem' }}>{activeFicha.observacoes}</p>
            </div>
            
            <div style={{ display: 'flex', gap: '2rem' }}>
              <div style={{ textAlign: 'right' }}>
                <span style={{ fontSize: '0.85rem', color: 'var(--text-secondary)' }}>Custo Materiais</span>
                <div style={{ fontSize: '1.5rem', fontWeight: 600, color: 'var(--success)' }}>
                  {formatCurrency(activeFicha.custoTotalMateriais)}
                </div>
              </div>
              <div style={{ textAlign: 'right' }}>
                <span style={{ fontSize: '0.85rem', color: 'var(--text-secondary)' }}>Tempo TPP</span>
                <div style={{ fontSize: '1.5rem', fontWeight: 600, color: 'var(--warning)' }}>
                  {activeFicha.tempoPadraoTotalCentesimal} <span style={{ fontSize: '1rem' }}>min</span>
                </div>
              </div>
            </div>
          </div>

          {/* Abas */}
          <div style={{ display: 'flex', gap: '1rem', borderBottom: '1px solid var(--border-color)', paddingBottom: '0.5rem' }}>
            <button 
              onClick={() => setActiveTab('MATERIAIS')}
              style={{
                background: 'none', border: 'none', padding: '0.5rem 1rem', fontSize: '1rem', cursor: 'pointer',
                fontWeight: activeTab === 'MATERIAIS' ? 600 : 400,
                color: activeTab === 'MATERIAIS' ? 'var(--accent-primary)' : 'var(--text-secondary)',
                borderBottom: activeTab === 'MATERIAIS' ? '2px solid var(--accent-primary)' : 'none'
              }}
            >
              Materiais
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

          <div style={{ display: 'grid', gridTemplateColumns: '1fr 2fr', gap: '2rem' }}>
            {/* Lado Esquerdo: Formulários */}
            <div className="glass-card">
              {activeTab === 'MATERIAIS' ? (
                <>
                  <h3 style={{ marginBottom: '1rem' }}>Adicionar Material</h3>
                  <form onSubmit={handleAddMaterial} style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}>
                    <div>
                      <label style={{ display: 'block', marginBottom: '0.5rem', fontSize: '0.9rem' }}>Material (Estoque)</label>
                      <select value={selectedMaterialId} onChange={e => setSelectedMaterialId(e.target.value)} required>
                        <option value="">Selecione...</option>
                        {estoque.map(m => (
                          <option key={m.id} value={m.id}>{m.codigo} - {m.nome} ({formatCurrency(m.custoUnitario)}/{m.unidadeMedida})</option>
                        ))}
                      </select>
                    </div>
                    <div>
                      <label style={{ display: 'block', marginBottom: '0.5rem', fontSize: '0.9rem' }}>Quantidade Gasta</label>
                      <input 
                        type="number" step="0.001" min="0" required 
                        value={quantidadeMaterial} onChange={e => setQuantidadeMaterial(e.target.value)} 
                        placeholder="Ex: 0.120"
                      />
                    </div>
                    <button type="submit" className="btn-primary" disabled={isSubmitting}>Incluir Material</button>
                  </form>
                </>
              ) : (
                <>
                  <h3 style={{ marginBottom: '1rem' }}>Adicionar Operação</h3>
                  <form onSubmit={handleAddOperacao} style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}>
                    <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '1rem' }}>
                      <div>
                        <label style={{ display: 'block', marginBottom: '0.5rem', fontSize: '0.9rem', color: 'var(--text-secondary)' }}>Operação</label>
                        <input type="text" value={opNome} onChange={e => setOpNome(e.target.value)} placeholder="Ex: Fechar Fundo" style={{ width: '100%' }} required />
                      </div>
                      <div>
                        <label style={{ display: 'block', marginBottom: '0.5rem', fontSize: '0.9rem', color: 'var(--text-secondary)' }}>Máquina</label>
                        <input type="text" value={opMaquina} onChange={e => setOpMaquina(e.target.value)} placeholder="Ex: OVL" style={{ width: '100%' }} />
                      </div>
                    </div>
                    <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr 1fr', gap: '1rem' }}>
                      <div>
                        <label style={{ display: 'block', marginBottom: '0.5rem', fontSize: '0.9rem' }}>Ordem</label>
                        <input type="number" required value={opOrdem} onChange={e => setOpOrdem(e.target.value)} />
                      </div>
                      <div>
                        <label style={{ display: 'block', marginBottom: '0.5rem', fontSize: '0.9rem' }}>Folhas</label>
                        <input type="number" required value={opFolhas} onChange={e => setOpFolhas(e.target.value)} />
                      </div>
                      <div>
                        <label style={{ display: 'block', marginBottom: '0.5rem', fontSize: '0.9rem' }}>Paradas</label>
                        <input type="number" required value={opParadas} onChange={e => setOpParadas(e.target.value)} />
                      </div>
                    </div>
                    <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '1rem' }}>
                      <div>
                        <label style={{ display: 'block', marginBottom: '0.5rem', fontSize: '0.9rem' }}>Comprimento</label>
                        <select value={opComprimento} onChange={e => setOpComprimento(e.target.value)}>
                          <option value="DE_0_A_60">0 a 60 cm</option>
                          <option value="DE_61_A_90">61 a 90 cm</option>
                          <option value="ACIMA_DE_91">Acima de 91 cm</option>
                        </select>
                      </div>
                      <div>
                        <label style={{ display: 'block', marginBottom: '0.5rem', fontSize: '0.9rem' }}>Dificuldade</label>
                        <select value={opDificuldade} onChange={e => setOpDificuldade(e.target.value)}>
                          <option value="FACIL">Fácil</option>
                          <option value="MEDIO">Média</option>
                          <option value="DIFICIL">Difícil</option>
                          <option value="MUITO_DIFICIL">Muito Difícil</option>
                        </select>
                      </div>
                    </div>
                    <button type="submit" className="btn-primary" disabled={isSubmitting}>Salvar Operação</button>
                  </form>
                </>
              )}
            </div>

            {/* Lado Direito: Tabelas */}
            <div className="glass-card" style={{ padding: 0, overflow: 'hidden' }}>
              {activeTab === 'MATERIAIS' ? (
                <>
                  <div style={{ padding: '1.5rem', borderBottom: '1px solid var(--border-color)' }}>
                    <h3>Composição do Custo</h3>
                  </div>
                  {activeFicha.materiais?.length === 0 ? (
                    <div style={{ padding: '2rem', textAlign: 'center', color: 'var(--text-muted)' }}>Sem materiais cadastrados.</div>
                  ) : (
                    <table className="premium-table">
                      <thead><tr><th>Material</th><th>Qtd</th><th>Un</th></tr></thead>
                      <tbody>
                        {activeFicha.materiais?.map((m: any) => (
                          <tr key={m.id}>
                            <td>{m.materialNome}</td>
                            <td style={{ fontWeight: 600 }}>{m.quantidade}</td>
                            <td style={{ color: 'var(--text-secondary)' }}>{m.unidadeMedida}</td>
                          </tr>
                        ))}
                      </tbody>
                    </table>
                  )}
                </>
              ) : (
                <>
                  <div style={{ padding: '1.5rem', borderBottom: '1px solid var(--border-color)' }}>
                    <h3>Roteiro de Produção</h3>
                  </div>
                  {activeFicha.operacoes?.length === 0 ? (
                    <div style={{ padding: '2rem', textAlign: 'center', color: 'var(--text-muted)' }}>Sem operações cadastradas.</div>
                  ) : (
                    <table className="premium-table">
                      <thead><tr><th>Nº</th><th>Operação</th><th>Máquina</th><th>Dif. / Comp.</th><th>Tempo</th></tr></thead>
                      <tbody>
                        {activeFicha.operacoes?.sort((a:any, b:any) => a.ordemExecucao - b.ordemExecucao).map((op: any) => (
                          <tr key={op.id}>
                            <td style={{ color: 'var(--text-secondary)' }}>{op.ordemExecucao}</td>
                            <td style={{ fontWeight: 500 }}>{op.nome}</td>
                            <td style={{ color: 'var(--text-secondary)' }}>{op.maquina || '-'}</td>
                            <td style={{ fontSize: '0.85rem' }}>{op.grauDificuldade} / {op.faixaComprimento}</td>
                            <td style={{ fontWeight: 600, color: 'var(--warning)' }}>{op.tempoCalculadoCentesimal}m</td>
                          </tr>
                        ))}
                      </tbody>
                    </table>
                  )}
                </>
              )}
            </div>

          </div>
        </div>
      ) : (
        <div className="glass-card" style={{ padding: '4rem 2rem', textAlign: 'center', color: 'var(--text-muted)' }}>
          <Layers size={32} style={{ margin: '0 auto 1rem', opacity: 0.5 }} />
          <p>Selecione um produto para visualizar ou criar sua Ficha Técnica.</p>
        </div>
      )}
    </div>
  );
};

export default FichasTecnicas;
