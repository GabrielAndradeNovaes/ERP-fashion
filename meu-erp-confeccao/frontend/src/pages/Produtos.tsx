import React, { useState, useEffect } from 'react';
import { Info } from 'lucide-react';
import api from '../api/axios';
import Modal from '../components/Modal';
import { DataTable } from '../components/DataTable';
import { useAuth } from '../contexts/AuthContext';
import type { ColumnDef } from '@tanstack/react-table';
import {
  Box,
  Typography,
  Button,
  Card,
  TextField,
  Stack,
  Chip,
  CircularProgress,
  Tabs,
  Tab,
  Select,
  MenuItem,
  FormControl,
  InputLabel,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Paper,
  Grid,
  Autocomplete
} from '@mui/material';
import AddIcon from '@mui/icons-material/Add';

interface ProdutoBase {
  id: string;
  codigo: string;
  nome: string;
  descricao: string;
  precoVenda: number;
  precoCusto: number;
  fichaTecnica: any; // Simplified for now
  skus?: any[];
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
  const [activeTab, setActiveTab] = useState(0);
  const { hasPermission } = useAuth();
  const canEdit = hasPermission('PRODUTOS_EDIT');

  // Form State (Add Product)
  const [codigo, setCodigo] = useState('');
  const [nome, setNome] = useState('');
  const [descricao, setDescricao] = useState('');
  const [precoVenda, setPrecoVenda] = useState<string>('');
  
  const [marca, setMarca] = useState('');
  const [categoria, setCategoria] = useState('');
  const [colecao, setColecao] = useState('');
  const [genero, setGenero] = useState('');
  const [ncm, setNcm] = useState('');
  const [cest, setCest] = useState('');
  const [origem, setOrigem] = useState('');
  const [pesoBruto, setPesoBruto] = useState('');
  const [pesoLiquido, setPesoLiquido] = useState('');
  const [status, setStatus] = useState('ATIVO');

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
  
  // Form State (Add SKU Matrix)
  const [skuCores, setSkuCores] = useState<any[]>([]);
  const [skuTamanhos, setSkuTamanhos] = useState<any[]>([]);
  const [dbCores, setDbCores] = useState<any[]>([]);
  const [dbTamanhos, setDbTamanhos] = useState<any[]>([]);
  const [temporarySkus, setTemporarySkus] = useState<any[]>([]);

  const fetchInitialData = async () => {
    try {
      setLoading(true);
      const [prodRes, matRes, coresRes, tamRes] = await Promise.all([
        api.get('/catalog/produtos'),
        api.get('/inventory/materiais'),
        api.get('/catalog/cores'),
        api.get('/catalog/tamanhos')
      ]);
      setProdutos(prodRes.data);
      setEstoque(matRes.data);
      setDbCores(coresRes.data.filter((c:any) => c.ativo));
      setDbTamanhos(tamRes.data.filter((t:any) => t.ativo));
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
        marca,
        categoria,
        colecao,
        genero,
        ncm,
        cest,
        origem,
        pesoBruto: parseFloat(pesoBruto) || null,
        pesoLiquido: parseFloat(pesoLiquido) || null,
        status,
        skus: []
      });
      
      setCodigo('');
      setNome('');
      setDescricao('');
      setPrecoVenda('');
      setMarca('');
      setCategoria('');
      setColecao('');
      setGenero('');
      setNcm('');
      setCest('');
      setOrigem('');
      setPesoBruto('');
      setPesoLiquido('');
      setStatus('ATIVO');
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
    setActiveTab(0);
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
        const prodRes = await api.get(`/catalog/produtos/${produto.id}`);
        setSelectedProduto(prodRes.data);
        setTemporarySkus(prodRes.data.skus || []);
      }
    } else {
      setTemporarySkus(produto.skus || []);
    }
  };

  const refreshSelectedProduto = async () => {
    if (!selectedProduto) return;
    try {
      const res = await api.get(`/catalog/produtos/${selectedProduto.id}`);
      setSelectedProduto(res.data);
      setTemporarySkus(res.data.skus || []);
      fetchInitialData();
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
        ordemExecucao: parseInt(opOrdem) || 1,
        quantidadeFolhas: parseInt(opFolhas) || 0,
        quantidadeParadas: parseInt(opParadas) || 0,
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

  const handleGenerateSkus = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!selectedProduto || skuCores.length === 0 || skuTamanhos.length === 0) {
        alert("Selecione ao menos uma cor e um tamanho.");
        return;
    }

    const newSkus: any[] = [];
    skuCores.forEach(cor => {
        skuTamanhos.forEach(tamanho => {
            // Check if it already exists in temporary
            const exists = temporarySkus.find(ts => ts.cor === cor.nome && ts.tamanho === tamanho.nome);
            if (!exists) {
                newSkus.push({ 
                  cor: cor.nome, 
                  tamanho: tamanho.nome, 
                  codigoBarras: '', 
                  precoVenda: selectedProduto.precoVenda 
                });
            }
        });
    });

    setTemporarySkus([...temporarySkus, ...newSkus]);
    setSkuCores([]);
    setSkuTamanhos([]);
  };

  const handleAutoGenerateEan = () => {
    const updated = temporarySkus.map(ts => ({
      ...ts,
      codigoBarras: ts.codigoBarras ? ts.codigoBarras : `${selectedProduto?.codigo}-${ts.cor.substring(0,3).toUpperCase()}-${ts.tamanho.toUpperCase()}`
    }));
    setTemporarySkus(updated);
  };

  const handleSaveSkus = async () => {
    if (!selectedProduto) return;
    try {
      setIsSubmitting(true);
      await api.put(`/catalog/produtos/${selectedProduto.id}`, {
        codigo: selectedProduto.codigo,
        nome: selectedProduto.nome,
        descricao: selectedProduto.descricao,
        precoVenda: selectedProduto.precoVenda,
        precoCusto: selectedProduto.precoCusto,
        skus: temporarySkus
      });
      await refreshSelectedProduto();
      alert('Grade salva com sucesso!');
    } catch (err: any) {
      alert(err.response?.data?.message || 'Erro ao salvar grade.');
    } finally {
      setIsSubmitting(false);
    }
  };

  const updateTemporarySku = (index: number, field: string, value: any) => {
    const updated = [...temporarySkus];
    updated[index] = { ...updated[index], [field]: value };
    setTemporarySkus(updated);
  };

  const handleAddMaterial = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!selectedProduto?.fichaTecnica || !selectedMaterialId || !quantidadeMaterial) return;

    try {
      setIsSubmitting(true);
      await api.post(`/production/fichas-tecnicas/${selectedProduto.fichaTecnica.id}/materiais`, {
        materialId: selectedMaterialId,
        quantidade: parseFloat(quantidadeMaterial)
      });
      
      setSelectedMaterialId('');
      setQuantidadeMaterial('');
      await refreshSelectedProduto();
    } catch (err: any) {
      alert(err.response?.data?.message || 'Erro ao adicionar material.');
    } finally {
      setIsSubmitting(false);
    }
  };

  const formatCurrency = (value: number) => {
    if (value === undefined || value === null) return 'R$ 0,00';
    return new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' }).format(value);
  };

  const columns: ColumnDef<any, any, any>[] = React.useMemo(() => [
    {
      accessorKey: 'empresa',
      header: 'Empresa',
      cell: (info) => {
        const emp = info.getValue();
        if (!emp) return '-';
        return (
          <Chip 
            label={emp.nomeFantasia || emp.razaoSocial || '-'} 
            size="small" 
            variant="outlined"
            sx={{ borderColor: 'var(--accent-primary)', color: 'var(--accent-primary)', fontWeight: 600 }}
          />
        );
      }
    },
    {
      accessorKey: 'codigo',
      header: 'Ref',
      cell: (info) => <Chip label={info.getValue() as string} color="primary" variant="outlined" size="small" />
    },
    {
      accessorKey: 'nome',
      header: 'Produto',
      cell: (info) => <Typography sx={{ fontWeight: 500 }}>{info.getValue() as string}</Typography>
    },
    {
      accessorKey: 'precoVenda',
      header: 'Preço Venda',
      cell: (info) => <Typography color="primary" sx={{ fontWeight: 600 }}>{formatCurrency(info.getValue() as number)}</Typography>
    },
    {
      accessorKey: 'precoCusto',
      header: 'Custo Produção',
      cell: (info) => {
        const val = info.getValue() as number;
        return <Typography color="warning.main" sx={{ fontWeight: 500 }}>{val > 0 ? formatCurrency(val) : '-'}</Typography>;
      }
    },
    {
      id: 'statusFicha',
      header: 'Status Ficha',
      cell: (info) => info.row.original.fichaTecnica 
        ? <Chip label="Preenchida" color="success" size="small" /> 
        : <Chip label="Pendente" color="default" size="small" />
    }
  ], []);

  const handleTabChange = (_: React.SyntheticEvent, newValue: number) => {
    setActiveTab(newValue);
  };

  return (
    <Box className="animate-fade-in-up">
      <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 4 }}>
        <Box>
          <Typography variant="h4" component="h1" gutterBottom sx={{ fontWeight: 800, color: 'var(--text-primary)', letterSpacing: '-0.5px' }}>
            Produtos e <span className="text-gradient">Fichas Técnicas</span>
          </Typography>
          <Typography variant="body1" sx={{ color: 'var(--text-secondary)' }}>
            Gerencie os produtos e suas estruturas (BOM e Operações).
          </Typography>
        </Box>
        {canEdit && (
          <Button 
            variant="contained" 
            startIcon={<AddIcon />}
            onClick={() => setIsAddModalOpen(true)}
            size="large"
            sx={{
              background: 'var(--accent-gradient)',
              borderRadius: 'var(--radius-md)',
              textTransform: 'none',
              fontWeight: 600,
              boxShadow: '0 4px 14px 0 rgba(99, 102, 241, 0.39)',
              '&:hover': {
                boxShadow: '0 6px 20px rgba(99, 102, 241, 0.23)'
              }
            }}
          >
            Novo Produto
          </Button>
        )}
      </Box>

      <div className="premium-card">
        <Box sx={{ p: 2, borderBottom: '1px solid', borderColor: 'var(--border-color)', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
          <Typography variant="h6" sx={{ fontWeight: 600, color: 'var(--text-primary)' }}>Catálogo</Typography>
          <Chip label={`${produtos.length} Itens`} sx={{ bgcolor: 'rgba(99, 102, 241, 0.1)', color: 'var(--accent-primary)', fontWeight: 600 }} size="small" />
        </Box>
        
        {loading ? (
          <Box sx={{ p: 6, display: 'flex', justifyContent: 'center' }}>
            <CircularProgress />
          </Box>
        ) : error ? (
          <Box sx={{ p: 4, textAlign: 'center' }}>
            <Typography color="error">{error}</Typography>
          </Box>
        ) : produtos.length === 0 ? (
          <Box sx={{ p: 8, display: 'flex', flexDirection: 'column', alignItems: 'center', color: 'text.secondary' }}>
            <Info size={48} style={{ marginBottom: '16px', opacity: 0.5 }} />
            <Typography>Nenhum produto cadastrado.</Typography>
          </Box>
        ) : (
          <Box sx={{ 
            cursor: 'pointer', 
            '& th': { bgcolor: 'transparent', color: 'var(--text-secondary)', fontWeight: 600 },
            '& td': { borderColor: 'var(--border-color)', color: 'var(--text-primary)' },
            '& tbody tr:hover': { backgroundColor: 'rgba(255,255,255,0.02)' } 
          }}>
            <DataTable 
              columns={columns} 
              data={produtos} 
              onRowClick={(row) => openEditModal(row.original)} 
            />
          </Box>
        )}
      </div>

      {/* Modal Adicionar Produto */}
      <Modal isOpen={isAddModalOpen} onClose={() => setIsAddModalOpen(false)} title="Novo Produto" width="500px">
        <div className="glass-panel" style={{ padding: '24px', background: 'var(--bg-card)', border: 'none', boxShadow: 'none' }}>
          <form onSubmit={handleAddProduto}>
          <Stack spacing={3} sx={{ mt: 1 }}>
            <Grid container spacing={2}>
              <Grid size={{ xs: 12, sm: 6 }}>
                <TextField
                  label="Código (Referência)"
                  variant="outlined"
                  fullWidth
                  required
                  value={codigo}
                  onChange={(e) => setCodigo(e.target.value)}
                  placeholder="Ex: REF-100"
                />
              </Grid>
              <Grid size={{ xs: 12, sm: 6 }}>
                <TextField
                  label="Nome do Produto"
                  variant="outlined"
                  fullWidth
                  required
                  value={nome}
                  onChange={(e) => setNome(e.target.value)}
                  placeholder="Ex: Calcinha Algodão"
                />
              </Grid>
              <Grid size={{ xs: 12, sm: 6 }}>
                <TextField
                  label="Preço de Venda (R$)"
                  type="number"
                  variant="outlined"
                  fullWidth
                  required
                  slotProps={{ htmlInput: { step: "0.01", min: "0" } }}
                  value={precoVenda}
                  onChange={(e) => setPrecoVenda(e.target.value)}
                />
              </Grid>
              <Grid size={{ xs: 12, sm: 6 }}>
                <TextField label="Marca" fullWidth value={marca} onChange={e => setMarca(e.target.value)} />
              </Grid>
              <Grid size={{ xs: 12, sm: 4 }}>
                <TextField label="Categoria" fullWidth value={categoria} onChange={e => setCategoria(e.target.value)} />
              </Grid>
              <Grid size={{ xs: 12, sm: 4 }}>
                <TextField label="Coleção" fullWidth value={colecao} onChange={e => setColecao(e.target.value)} />
              </Grid>
              <Grid size={{ xs: 12, sm: 4 }}>
                <TextField label="Gênero" fullWidth value={genero} onChange={e => setGenero(e.target.value)} />
              </Grid>
              <Grid size={{ xs: 12, sm: 4 }}>
                <TextField label="NCM" fullWidth value={ncm} onChange={e => setNcm(e.target.value)} />
              </Grid>
              <Grid size={{ xs: 12, sm: 4 }}>
                <TextField label="CEST" fullWidth value={cest} onChange={e => setCest(e.target.value)} />
              </Grid>
              <Grid size={{ xs: 12, sm: 4 }}>
                <TextField label="Origem" fullWidth value={origem} onChange={e => setOrigem(e.target.value)} />
              </Grid>
              <Grid size={{ xs: 12, sm: 6 }}>
                <TextField label="Peso Bruto" type="number" fullWidth value={pesoBruto} onChange={e => setPesoBruto(e.target.value)} />
              </Grid>
              <Grid size={{ xs: 12, sm: 6 }}>
                <TextField label="Peso Líquido" type="number" fullWidth value={pesoLiquido} onChange={e => setPesoLiquido(e.target.value)} />
              </Grid>
            </Grid>
            <TextField
              label="Descrição (Opcional)"
              variant="outlined"
              fullWidth
              multiline
              rows={2}
              value={descricao}
              onChange={(e) => setDescricao(e.target.value)}
            />
            
            <Box sx={{ display: 'flex', justifyContent: 'flex-end', pt: 2, gap: 2 }}>
              <Button onClick={() => setIsAddModalOpen(false)} sx={{ color: 'var(--text-secondary)' }}>Cancelar</Button>
              <Button 
                type="submit" 
                variant="contained" 
                disabled={isSubmitting}
                startIcon={isSubmitting && <CircularProgress size={20} color="inherit" />}
                sx={{ bgcolor: 'var(--accent-primary)', '&:hover': { bgcolor: 'var(--accent-hover)' } }}
                disableElevation
              >
                Salvar Produto
              </Button>
            </Box>
          </Stack>
        </form>
        </div>
      </Modal>

      {/* Modal Editar Produto / Ficha Técnica */}
      <Modal isOpen={isEditModalOpen} onClose={() => setIsEditModalOpen(false)} title={`Ficha Técnica: ${selectedProduto?.nome}`} width="850px">
        {selectedProduto && (
          <div className="glass-panel" style={{ padding: '24px', background: 'var(--bg-card)', border: 'none', boxShadow: 'none' }}>
            <Box sx={{ borderBottom: 1, borderColor: 'var(--border-color)', mb: 3 }}>
              <Tabs 
                value={activeTab} 
                onChange={handleTabChange} 
                aria-label="Abas do produto"
                sx={{
                  '& .MuiTab-root': { color: 'var(--text-secondary)', fontWeight: 600, textTransform: 'none' },
                  '& .Mui-selected': { color: 'var(--accent-primary) !important' },
                  '& .MuiTabs-indicator': { backgroundColor: 'var(--accent-primary)' }
                }}
              >
                <Tab label="Informações Gerais" />
                <Tab label="Grades (SKUs)" />
                <Tab label="Materiais (BOM)" />
                <Tab label="Operações de Costura" />
              </Tabs>
            </Box>

            {/* TAB INFO */}
            {activeTab === 0 && (
              <Stack spacing={2}>
                <Typography><strong>Ref:</strong> {selectedProduto.codigo}</Typography>
                <Typography><strong>Descrição:</strong> {selectedProduto.descricao}</Typography>
                <Typography><strong>Preço Venda:</strong> {formatCurrency(selectedProduto.precoVenda)}</Typography>
                <Typography variant="body2" color="text.secondary" sx={{ mt: 2 }}>
                  A edição completa das informações do produto será disponibilizada em breve.
                </Typography>
              </Stack>
            )}

            {/* TAB SKUS */}
            {activeTab === 1 && (
              <Box className="animate-fade-in-up">
                <div className="premium-card" style={{ padding: '24px', marginBottom: '24px', backgroundColor: 'rgba(99, 102, 241, 0.03)', border: '1px solid rgba(99, 102, 241, 0.2)' }}>
                  <Typography variant="subtitle1" sx={{ fontWeight: 600, mb: 2, color: 'var(--accent-primary)' }}>
                    Gerador Automático de Grade
                  </Typography>
                  {canEdit ? (
                    <form onSubmit={handleGenerateSkus}>
                      <Grid container spacing={2} alignItems="flex-end">
                        <Grid size={{ xs: 12, sm: 5 }}>
                          <Autocomplete
                            multiple
                            size="small"
                            options={dbCores}
                            getOptionLabel={(option) => option.nome}
                            value={skuCores}
                            onChange={(e, newValue) => setSkuCores(newValue)}
                            renderInput={(params) => (
                              <TextField {...params} label="Cores (Do Catálogo)" required={skuCores.length === 0} placeholder="Selecione..." />
                            )}
                          />
                        </Grid>
                        <Grid size={{ xs: 12, sm: 4 }}>
                          <Autocomplete
                            multiple
                            size="small"
                            options={dbTamanhos}
                            getOptionLabel={(option) => option.nome}
                            value={skuTamanhos}
                            onChange={(e, newValue) => setSkuTamanhos(newValue)}
                            renderInput={(params) => (
                              <TextField {...params} label="Tamanhos (Do Catálogo)" required={skuTamanhos.length === 0} placeholder="Selecione..." />
                            )}
                          />
                        </Grid>
                        <Grid size={{ xs: 12, sm: 3 }}>
                          <Button 
                            type="submit" 
                            variant="contained" 
                            fullWidth 
                            disableElevation 
                            sx={{ height: 40, bgcolor: 'var(--accent-primary)', '&:hover': { bgcolor: 'var(--accent-hover)' } }}
                          >
                            Gerar Grade
                          </Button>
                        </Grid>
                      </Grid>
                    </form>
                  ) : (
                    <Typography color="text.secondary">Você não tem permissão para gerar grade de SKUs.</Typography>
                  )}
                  <Typography variant="caption" sx={{ color: 'var(--text-muted)', display: 'block', mt: 1 }}>
                    Dica: O sistema criará as combinações na tabela abaixo. Edite os preços e códigos de barras e clique em "Salvar Grade".
                  </Typography>
                </div>

                {temporarySkus.length > 0 && canEdit && (
                  <Box sx={{ mb: 2, display: 'flex', gap: 2 }}>
                    <Button variant="outlined" onClick={handleAutoGenerateEan} size="small">
                      Auto-Gerar EANs/Códigos
                    </Button>
                    <Button variant="contained" onClick={handleSaveSkus} size="small" disabled={isSubmitting} color="success">
                      {isSubmitting ? 'Salvando...' : 'Salvar Grade no Banco'}
                    </Button>
                  </Box>
                )}

                <TableContainer sx={{ border: '1px solid var(--border-color)', borderRadius: 'var(--radius-sm)', bgcolor: 'transparent' }}>
                  <Table size="small">
                    <TableHead sx={{ bgcolor: 'rgba(255,255,255,0.02)' }}>
                      <TableRow>
                        <TableCell sx={{ color: 'var(--text-secondary)', fontWeight: 600, borderColor: 'var(--border-color)' }}>Cor</TableCell>
                        <TableCell sx={{ color: 'var(--text-secondary)', fontWeight: 600, borderColor: 'var(--border-color)' }}>Tamanho</TableCell>
                        <TableCell sx={{ color: 'var(--text-secondary)', fontWeight: 600, borderColor: 'var(--border-color)' }}>Preço Venda</TableCell>
                        <TableCell sx={{ color: 'var(--text-secondary)', fontWeight: 600, borderColor: 'var(--border-color)' }}>Cód. Barras</TableCell>
                      </TableRow>
                    </TableHead>
                    <TableBody>
                      {temporarySkus.map((sku: any, idx: number) => (
                        <TableRow key={idx} sx={{ '&:hover': { bgcolor: 'rgba(255,255,255,0.02)' } }}>
                          <TableCell sx={{ borderColor: 'var(--border-color)', color: 'var(--text-primary)' }}>{sku.cor}</TableCell>
                          <TableCell sx={{ borderColor: 'var(--border-color)', color: 'var(--text-primary)' }}>{sku.tamanho}</TableCell>
                          <TableCell sx={{ borderColor: 'var(--border-color)', p: 1 }}>
                            <TextField 
                              size="small" 
                              type="number"
                              value={sku.precoVenda || ''}
                              onChange={(e) => updateTemporarySku(idx, 'precoVenda', parseFloat(e.target.value) || 0)}
                              slotProps={{ htmlInput: { step: '0.01' } }}
                            />
                          </TableCell>
                          <TableCell sx={{ borderColor: 'var(--border-color)', p: 1 }}>
                            <TextField 
                              size="small" 
                              value={sku.codigoBarras || ''}
                              onChange={(e) => updateTemporarySku(idx, 'codigoBarras', e.target.value)}
                              placeholder="EAN ou Código"
                            />
                          </TableCell>
                        </TableRow>
                      ))}
                      {temporarySkus.length === 0 && (
                        <TableRow>
                          <TableCell colSpan={4} align="center" sx={{ py: 3, color: 'text.secondary' }}>
                            Nenhum tamanho ou cor (SKU) na grade.
                          </TableCell>
                        </TableRow>
                      )}
                    </TableBody>
                  </Table>
                </TableContainer>
              </Box>
            )}

            {/* TAB MATERIAIS */}
            {activeTab === 2 && selectedProduto.fichaTecnica && (
              <Box className="animate-fade-in-up">
                <Card variant="outlined" sx={{ p: 3, mb: 3, bgcolor: 'rgba(99, 102, 241, 0.05)', borderColor: 'rgba(99, 102, 241, 0.2)', borderRadius: 'var(--radius-md)' }}>
                  <Typography variant="body2" sx={{ color: 'var(--accent-primary)', fontWeight: 600, mb: 0.5 }}>Custo Total Materiais</Typography>
                  <Typography variant="h4" sx={{ color: 'var(--text-primary)', fontWeight: 800 }}>
                    {formatCurrency(selectedProduto.fichaTecnica.custoTotalMateriais)}
                  </Typography>
                </Card>

                {canEdit && (
                  <form onSubmit={handleAddMaterial}>
                    <Stack direction="row" spacing={2} sx={{ mb: 3, alignItems: 'center' }}>
                      <FormControl fullWidth required>
                        <InputLabel id="material-label">Adicionar Material</InputLabel>
                        <Select
                          labelId="material-label"
                          value={selectedMaterialId}
                          label="Adicionar Material"
                          onChange={e => setSelectedMaterialId(e.target.value)}
                        >
                          <MenuItem value=""><em>Selecione...</em></MenuItem>
                          {estoque.map(m => (
                            <MenuItem key={m.id} value={m.id}>{m.codigo} - {m.nome}</MenuItem>
                          ))}
                        </Select>
                      </FormControl>
                      <TextField
                        label="Qtd Gasta"
                        type="number"
                        required
                        slotProps={{ htmlInput: { step: "0.001", min: "0" } }}
                        value={quantidadeMaterial}
                        onChange={e => setQuantidadeMaterial(e.target.value)}
                        placeholder="0.120"
                        sx={{ width: 150 }}
                      />
                      <Button 
                        type="submit" 
                        variant="contained" 
                        disableElevation 
                        disabled={isSubmitting}
                        sx={{ height: 56, bgcolor: 'var(--accent-primary)', '&:hover': { bgcolor: 'var(--accent-hover)' } }}
                      >
                        {isSubmitting ? '...' : 'Incluir'}
                      </Button>
                    </Stack>
                  </form>
                )}

                <TableContainer sx={{ border: '1px solid var(--border-color)', borderRadius: 'var(--radius-sm)', bgcolor: 'transparent' }}>
                  <Table size="small">
                    <TableHead sx={{ bgcolor: 'rgba(255,255,255,0.02)' }}>
                      <TableRow>
                        <TableCell sx={{ color: 'var(--text-secondary)', fontWeight: 600, borderColor: 'var(--border-color)' }}>Material</TableCell>
                        <TableCell align="right" sx={{ color: 'var(--text-secondary)', fontWeight: 600, borderColor: 'var(--border-color)' }}>Quantidade</TableCell>
                        <TableCell sx={{ color: 'var(--text-secondary)', fontWeight: 600, borderColor: 'var(--border-color)' }}>Unidade</TableCell>
                      </TableRow>
                    </TableHead>
                    <TableBody>
                      {selectedProduto.fichaTecnica.materiais?.map((m: any) => (
                        <TableRow key={m.id} sx={{ '&:hover': { bgcolor: 'rgba(255,255,255,0.02)' } }}>
                          <TableCell sx={{ borderColor: 'var(--border-color)', color: 'var(--text-primary)' }}>{m.materialNome}</TableCell>
                          <TableCell align="right" sx={{ fontWeight: 600, borderColor: 'var(--border-color)', color: 'var(--text-primary)' }}>{m.quantidade}</TableCell>
                          <TableCell sx={{ color: 'var(--text-muted)', borderColor: 'var(--border-color)' }}>{m.unidadeMedida}</TableCell>
                        </TableRow>
                      ))}
                      {(!selectedProduto.fichaTecnica.materiais || selectedProduto.fichaTecnica.materiais.length === 0) && (
                        <TableRow>
                          <TableCell colSpan={3} align="center" sx={{ py: 3, color: 'text.secondary' }}>
                            Sem materiais.
                          </TableCell>
                        </TableRow>
                      )}
                    </TableBody>
                  </Table>
                </TableContainer>
              </Box>
            )}

            {/* TAB OPERAÇÕES */}
            {activeTab === 3 && selectedProduto.fichaTecnica && (
              <Box className="animate-fade-in-up">
                <Card variant="outlined" sx={{ p: 3, mb: 3, bgcolor: 'rgba(245, 158, 11, 0.05)', borderColor: 'rgba(245, 158, 11, 0.2)', borderRadius: 'var(--radius-md)' }}>
                  <Typography variant="body2" sx={{ color: 'var(--warning)', fontWeight: 600, mb: 0.5 }}>Tempo Padrão (TPP) Total</Typography>
                  <Typography variant="h4" sx={{ color: 'var(--text-primary)', fontWeight: 800 }}>
                    {selectedProduto.fichaTecnica.tempoPadraoTotalCentesimal || 0} <Typography component="span" variant="h6" sx={{ color: 'var(--text-muted)' }}>min centesimal</Typography>
                  </Typography>
                </Card>

                {canEdit && (
                  <div className="premium-card" style={{ padding: '16px', marginBottom: '24px' }}>
                    <form onSubmit={handleAddOperacao}>
                      <Grid container spacing={2}>
                        <Grid size={{ xs: 12, sm: 6 }}>
                          <TextField label="Nome da Operação" fullWidth required size="small" value={opNome} onChange={e => setOpNome(e.target.value)} />
                        </Grid>
                        <Grid size={{ xs: 12, sm: 6 }}>
                          <TextField label="Máquina" fullWidth size="small" value={opMaquina} onChange={e => setOpMaquina(e.target.value)} />
                        </Grid>
                        
                        <Grid size={{ xs: 6, sm: 2 }}>
                          <TextField label="Ordem" type="number" required size="small" value={opOrdem} onChange={e => setOpOrdem(e.target.value)} fullWidth />
                        </Grid>
                        <Grid size={{ xs: 6, sm: 2 }}>
                          <TextField label="Folhas" type="number" required size="small" value={opFolhas} onChange={e => setOpFolhas(e.target.value)} fullWidth />
                        </Grid>
                        <Grid size={{ xs: 6, sm: 2 }}>
                          <TextField label="Paradas" type="number" required size="small" value={opParadas} onChange={e => setOpParadas(e.target.value)} fullWidth />
                        </Grid>
                        <Grid size={{ xs: 6, sm: 2 }}>
                          <FormControl fullWidth size="small">
                            <InputLabel>Dif.</InputLabel>
                            <Select value={opDificuldade} label="Dif." onChange={e => setOpDificuldade(e.target.value)}>
                              <MenuItem value="MUITO_FACIL">Muito Fácil</MenuItem>
                              <MenuItem value="FACIL">Fácil</MenuItem>
                              <MenuItem value="MEDIO">Médio</MenuItem>
                              <MenuItem value="MEDIO_DIFICIL">Médio Dif.</MenuItem>
                              <MenuItem value="DIFICIL">Difícil</MenuItem>
                            </Select>
                          </FormControl>
                        </Grid>
                        <Grid size={{ xs: 6, sm: 2 }}>
                          <FormControl fullWidth size="small">
                            <InputLabel>Comp.</InputLabel>
                            <Select value={opComprimento} label="Comp." onChange={e => setOpComprimento(e.target.value)}>
                              <MenuItem value="DE_0_A_60">0-60</MenuItem>
                              <MenuItem value="DE_61_A_90">61-90</MenuItem>
                              <MenuItem value="ACIMA_DE_91">{'>'} 91</MenuItem>
                            </Select>
                          </FormControl>
                        </Grid>
                        <Grid size={{ xs: 12, sm: 2 }}>
                          <Button 
                            type="submit" 
                            variant="contained" 
                            disabled={isSubmitting} 
                            fullWidth 
                            disableElevation 
                            sx={{ height: 40, bgcolor: 'var(--accent-primary)', '&:hover': { bgcolor: 'var(--accent-hover)' } }}
                          >
                            {isSubmitting ? '...' : '+ Operação'}
                          </Button>
                        </Grid>
                      </Grid>
                    </form>
                  </div>
                )}

                <TableContainer sx={{ border: '1px solid var(--border-color)', borderRadius: 'var(--radius-sm)', bgcolor: 'transparent' }}>
                  <Table size="small">
                    <TableHead sx={{ bgcolor: 'rgba(255,255,255,0.02)' }}>
                      <TableRow>
                        <TableCell sx={{ color: 'var(--text-secondary)', fontWeight: 600, borderColor: 'var(--border-color)' }}>Ordem</TableCell>
                        <TableCell sx={{ color: 'var(--text-secondary)', fontWeight: 600, borderColor: 'var(--border-color)' }}>Operação</TableCell>
                        <TableCell sx={{ color: 'var(--text-secondary)', fontWeight: 600, borderColor: 'var(--border-color)' }}>Máquina</TableCell>
                        <TableCell align="right" sx={{ color: 'var(--text-secondary)', fontWeight: 600, borderColor: 'var(--border-color)' }}>Tempo Padrão</TableCell>
                      </TableRow>
                    </TableHead>
                    <TableBody>
                      {selectedProduto.fichaTecnica.operacoes?.sort((a:any, b:any) => a.ordemExecucao - b.ordemExecucao).map((op: any) => (
                        <TableRow key={op.id} sx={{ '&:hover': { bgcolor: 'rgba(255,255,255,0.02)' } }}>
                          <TableCell sx={{ borderColor: 'var(--border-color)', color: 'var(--text-primary)' }}>{op.ordemExecucao}</TableCell>
                          <TableCell sx={{ fontWeight: 500, borderColor: 'var(--border-color)', color: 'var(--text-primary)' }}>{op.nome}</TableCell>
                          <TableCell sx={{ color: 'var(--text-muted)', borderColor: 'var(--border-color)' }}>{op.maquina}</TableCell>
                          <TableCell align="right" sx={{ fontWeight: 700, color: 'var(--warning)', borderColor: 'var(--border-color)' }}>{op.tempoCalculadoCentesimal}m</TableCell>
                        </TableRow>
                      ))}
                    </TableBody>
                  </Table>
                </TableContainer>
              </Box>
            )}
          </div>
        )}
      </Modal>

    </Box>
  );
};

export default Produtos;
