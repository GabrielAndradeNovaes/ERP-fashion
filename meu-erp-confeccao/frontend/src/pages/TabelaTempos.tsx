import React, { useState, useEffect } from 'react';
import { Plus, Trash2, Loader2, Info } from 'lucide-react';
import api from '../api/axios';

interface TabelaTempo {
  id: string;
  indice: number;
  grauDificuldade: string;
  faixaComprimento: string;
  tempoCentesimal: number;
}

const TabelaTempos = () => {
  const [tempos, setTempos] = useState<TabelaTempo[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  // Form State
  const [indice, setIndice] = useState<number | ''>('');
  const [grauDificuldade, setGrauDificuldade] = useState('MEDIO');
  const [faixaComprimento, setFaixaComprimento] = useState('DE_0_A_60');
  const [tempo, setTempo] = useState<string>('');
  const [isSubmitting, setIsSubmitting] = useState(false);

  const fetchTempos = async () => {
    try {
      setLoading(true);
      const res = await api.get('/production/tempos-padrao');
      setTempos(res.data);
      setError(null);
    } catch (err: any) {
      setError(err.response?.data?.message || 'Erro ao buscar tabela de tempos.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchTempos();
  }, []);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!indice || !tempo) return;

    try {
      setIsSubmitting(true);
      await api.post('/production/tempos-padrao', {
        indice: Number(indice),
        grauDificuldade,
        faixaComprimento,
        tempoCentesimal: parseFloat(tempo)
      });
      // Reset form & reload
      setIndice('');
      setTempo('');
      fetchTempos();
    } catch (err: any) {
      alert(err.response?.data?.message || 'Erro ao cadastrar tempo padrão.');
    } finally {
      setIsSubmitting(false);
    }
  };

  const handleDelete = async (id: string) => {
    if (!confirm('Deseja realmente remover este tempo?')) return;
    try {
      await api.delete(`/production/tempos-padrao/${id}`);
      fetchTempos();
    } catch (err) {
      alert('Erro ao excluir registro.');
    }
  };

  // Utility to show user-friendly format (ex: 1.50 -> "1m 30s")
  const formatTime = (centesimal: number) => {
    const minutes = Math.floor(centesimal);
    const seconds = Math.round((centesimal - minutes) * 100 * 0.6); // 100 centesimals = 60 seconds
    if (minutes === 0) return `${seconds}s`;
    return `${minutes}m ${seconds}s`;
  };

  return (
    <div className="animate-fade-in">
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '2rem' }}>
        <div>
          <h1 style={{ fontSize: '1.8rem', marginBottom: '0.5rem' }}>Gestão de Tempos (TPP)</h1>
          <p style={{ color: 'var(--text-secondary)' }}>Cadastre a matriz de tempos centesimais para operações.</p>
        </div>
      </div>

      <div style={{ display: 'grid', gridTemplateColumns: '1fr 2fr', gap: '2rem' }}>
        
        {/* Formulário */}
        <div className="glass-card">
          <h3 style={{ marginBottom: '1.5rem', display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
            <Plus size={18} className="text-accent" />
            Novo Registro
          </h3>
          
          <form onSubmit={handleSubmit} style={{ display: 'flex', flexDirection: 'column', gap: '1.25rem' }}>
            <div>
              <label style={{ display: 'block', marginBottom: '0.5rem', fontSize: '0.9rem', color: 'var(--text-secondary)' }}>
                Índice (Folhas + Paradas)
              </label>
              <input 
                type="number" 
                min="0" 
                required 
                value={indice} 
                onChange={(e) => setIndice(e.target.value === '' ? '' : Number(e.target.value))} 
                placeholder="Ex: 3"
              />
            </div>
            
            <div>
              <label style={{ display: 'block', marginBottom: '0.5rem', fontSize: '0.9rem', color: 'var(--text-secondary)' }}>Grau de Dificuldade</label>
              <select value={grauDificuldade} onChange={e => setGrauDificuldade(e.target.value)}>
                <option value="MUITO_FACIL">Muito Fácil</option>
                <option value="FACIL">Fácil</option>
                <option value="MEDIO">Médio</option>
                <option value="MEDIO_DIFICIL">Médio-Difícil</option>
                <option value="DIFICIL">Difícil</option>
              </select>
            </div>

            <div>
              <label style={{ display: 'block', marginBottom: '0.5rem', fontSize: '0.9rem', color: 'var(--text-secondary)' }}>Comprimento da Costura</label>
              <select value={faixaComprimento} onChange={e => setFaixaComprimento(e.target.value)}>
                <option value="DE_0_A_60">0 a 60 cm</option>
                <option value="DE_61_A_90">61 a 90 cm</option>
                <option value="ACIMA_DE_91">Acima de 91 cm</option>
              </select>
            </div>

            <div>
              <label style={{ display: 'block', marginBottom: '0.5rem', fontSize: '0.9rem', color: 'var(--text-secondary)' }}>
                Tempo Centesimal (Minutos)
              </label>
              <input 
                type="number" 
                step="0.01"
                min="0"
                required 
                value={tempo} 
                onChange={(e) => setTempo(e.target.value)} 
                placeholder="Ex: 1.50 (1m 30s)"
              />
            </div>

            <button type="submit" className="btn-primary" disabled={isSubmitting} style={{ marginTop: '0.5rem' }}>
              {isSubmitting ? <Loader2 className="animate-spin" size={20} /> : 'Cadastrar Tempo'}
            </button>
          </form>
        </div>

        {/* Tabela */}
        <div className="glass-card" style={{ padding: 0, overflow: 'hidden' }}>
          <div style={{ padding: '1.5rem', borderBottom: '1px solid var(--border-color)', display: 'flex', justifyContent: 'space-between' }}>
            <h3>Matriz Cadastrada</h3>
            <span className="badge badge-blue">{tempos.length} Registros</span>
          </div>
          
          {loading ? (
            <div style={{ padding: '3rem', textAlign: 'center', color: 'var(--text-muted)' }}>
              <Loader2 className="animate-spin" size={32} style={{ margin: '0 auto', marginBottom: '1rem' }} />
              Carregando dados...
            </div>
          ) : error ? (
            <div style={{ padding: '2rem', textAlign: 'center', color: 'var(--danger)' }}>{error}</div>
          ) : tempos.length === 0 ? (
            <div style={{ padding: '4rem 2rem', textAlign: 'center', color: 'var(--text-muted)' }}>
              <Info size={32} style={{ margin: '0 auto 1rem', opacity: 0.5 }} />
              <p>Nenhum tempo padrão cadastrado.</p>
              <p style={{ fontSize: '0.9rem', marginTop: '0.5rem' }}>Utilize o formulário ao lado para iniciar a matriz.</p>
            </div>
          ) : (
            <div style={{ overflowX: 'auto' }}>
              <table className="premium-table">
                <thead>
                  <tr>
                    <th>Índice</th>
                    <th>Dificuldade</th>
                    <th>Comprimento</th>
                    <th>Centesimal</th>
                    <th>Formato Leitura</th>
                    <th>Ações</th>
                  </tr>
                </thead>
                <tbody>
                  {tempos.map(t => (
                    <tr key={t.id}>
                      <td><span className="badge badge-blue">Idx {t.indice}</span></td>
                      <td>{t.grauDificuldade.replace('_', ' ')}</td>
                      <td>{t.faixaComprimento.replace(/_/g, ' ')}</td>
                      <td style={{ fontWeight: 600 }} className="text-accent">{t.tempoCentesimal.toFixed(2)}</td>
                      <td style={{ color: 'var(--text-secondary)' }}>{formatTime(t.tempoCentesimal)}</td>
                      <td>
                        <button 
                          onClick={() => handleDelete(t.id)} 
                          className="btn-danger"
                          style={{ padding: '0.4rem', borderRadius: 'var(--radius-sm)' }}
                        >
                          <Trash2 size={16} />
                        </button>
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

export default TabelaTempos;
