import React, { useState, useEffect } from 'react';
import { Loader2, Info } from 'lucide-react';
import api from '../api/axios';
import Modal from '../components/Modal';
import { DataTable } from '../components/DataTable';
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
  Grid
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
      }
    }
  };

  const refreshSelectedProduto = async () => {
    if (!selectedProduto) return;
    try {
      const res = await api.get(`/catalog/produtos/${selectedProduto.id}`);
      setSelectedProduto(res.data);
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

  const columns: ColumnDef<ProdutoBase, any, any>[] = React.useMemo(() => [
    {
      accessorKey: 'codigo',
      header: 'Ref',
      cell: (info) => <Chip label={info.getValue() as string} color="primary" variant="outlined" size="small" />
    },
    {
      accessorKey: 'nome',
      header: 'Produto',
      cell: (info) => <Typography fontWeight={500}>{info.getValue() as string}</Typography>
    },
    {
      accessorKey: 'precoVenda',
      header: 'Preço Venda',
      cell: (info) => <Typography color="primary" fontWeight={600}>{formatCurrency(info.getValue() as number)}</Typography>
    },
    {
      accessorKey: 'precoCusto',
      header: 'Custo Produção',
      cell: (info) => {
        const val = info.getValue() as number;
        return <Typography color="warning.main" fontWeight={500}>{val > 0 ? formatCurrency(val) : '-'}</Typography>;
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

  const handleTabChange = (event: React.SyntheticEvent, newValue: number) => {
    setActiveTab(newValue);
  };

  return (
    <Box className="animate-fade-in">
      <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 4 }}>
        <Box>
          <Typography variant="h4" component="h1" gutterBottom fontWeight="bold">
            Produtos
          </Typography>
          <Typography variant="body1" color="text.secondary">
            Gerencie os produtos e suas Fichas Técnicas integradas.
          </Typography>
        </Box>
        <Button 
          variant="contained" 
          color="primary" 
          startIcon={<AddIcon />}
          onClick={() => setIsAddModalOpen(true)}
          size="large"
          disableElevation
        >
          Novo Produto
        </Button>
      </Box>

      <Card variant="outlined" sx={{ borderRadius: 2, overflow: 'hidden' }}>
        <Box sx={{ p: 2, borderBottom: '1px solid', borderColor: 'divider', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
          <Typography variant="h6">Catálogo</Typography>
          <Chip label={`${produtos.length} Itens`} color="primary" variant="outlined" />
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
          <Box sx={{ cursor: 'pointer', '& tbody tr:hover': { backgroundColor: 'action.hover' } }}>
            <DataTable 
              columns={columns} 
              data={produtos} 
              onRowClick={(row) => openEditModal(row.original)} 
            />
          </Box>
        )}
      </Card>

      {/* Modal Adicionar Produto */}
      <Modal isOpen={isAddModalOpen} onClose={() => setIsAddModalOpen(false)} title="Novo Produto" width="500px">
        <form onSubmit={handleAddProduto}>
          <Stack spacing={3} sx={{ mt: 1 }}>
            <TextField
              label="Código (Referência)"
              variant="outlined"
              fullWidth
              required
              value={codigo}
              onChange={(e) => setCodigo(e.target.value)}
              placeholder="Ex: REF-100"
            />
            <TextField
              label="Nome do Produto"
              variant="outlined"
              fullWidth
              required
              value={nome}
              onChange={(e) => setNome(e.target.value)}
              placeholder="Ex: Calcinha Algodão"
            />
            <TextField
              label="Preço de Venda (R$)"
              type="number"
              variant="outlined"
              fullWidth
              required
              inputProps={{ step: "0.01", min: "0" }}
              value={precoVenda}
              onChange={(e) => setPrecoVenda(e.target.value)}
            />
            <TextField
              label="Descrição (Opcional)"
              variant="outlined"
              fullWidth
              multiline
              rows={2}
              value={descricao}
              onChange={(e) => setDescricao(e.target.value)}
            />
            
            <Box sx={{ display: 'flex', justifyContent: 'flex-end', pt: 2 }}>
              <Button onClick={() => setIsAddModalOpen(false)} sx={{ mr: 2 }}>Cancelar</Button>
              <Button 
                type="submit" 
                variant="contained" 
                color="primary"
                disabled={isSubmitting}
                startIcon={isSubmitting && <CircularProgress size={20} color="inherit" />}
                disableElevation
              >
                Salvar Produto
              </Button>
            </Box>
          </Stack>
        </form>
      </Modal>

      {/* Modal Editar Produto / Ficha Técnica */}
      <Modal isOpen={isEditModalOpen} onClose={() => setIsEditModalOpen(false)} title={`Editar: ${selectedProduto?.nome}`} width="800px">
        {selectedProduto && (
          <Box>
            <Box sx={{ borderBottom: 1, borderColor: 'divider', mb: 3 }}>
              <Tabs value={activeTab} onChange={handleTabChange} aria-label="Abas do produto">
                <Tab label="Informações" />
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

            {/* TAB MATERIAIS */}
            {activeTab === 1 && selectedProduto.fichaTecnica && (
              <Box>
                <Card variant="outlined" sx={{ p: 2, mb: 3, bgcolor: 'action.hover' }}>
                  <Typography variant="body2" color="text.secondary">Custo Total Materiais</Typography>
                  <Typography variant="h5" color="success.main" fontWeight="bold">
                    {formatCurrency(selectedProduto.fichaTecnica.custoTotalMateriais)}
                  </Typography>
                </Card>

                <form onSubmit={handleAddMaterial}>
                  <Stack direction="row" spacing={2} alignItems="center" sx={{ mb: 3 }}>
                    <FormControl fullWidth required>
                      <InputLabel id="material-label">Material</InputLabel>
                      <Select
                        labelId="material-label"
                        value={selectedMaterialId}
                        label="Material"
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
                      inputProps={{ step: "0.001", min: "0" }}
                      value={quantidadeMaterial}
                      onChange={e => setQuantidadeMaterial(e.target.value)}
                      placeholder="0.120"
                      sx={{ width: 150 }}
                    />
                    <Button type="submit" variant="contained" disableElevation sx={{ height: 56 }}>
                      Incluir
                    </Button>
                  </Stack>
                </form>

                <TableContainer component={Paper} variant="outlined">
                  <Table size="small">
                    <TableHead>
                      <TableRow>
                        <TableCell>Material</TableCell>
                        <TableCell align="right">Qtd</TableCell>
                        <TableCell>Un</TableCell>
                      </TableRow>
                    </TableHead>
                    <TableBody>
                      {selectedProduto.fichaTecnica.materiais?.map((m: any) => (
                        <TableRow key={m.id}>
                          <TableCell>{m.materialNome}</TableCell>
                          <TableCell align="right" sx={{ fontWeight: 600 }}>{m.quantidade}</TableCell>
                          <TableCell color="text.secondary">{m.unidadeMedida}</TableCell>
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
            {activeTab === 2 && selectedProduto.fichaTecnica && (
              <Box>
                <Card variant="outlined" sx={{ p: 2, mb: 3, bgcolor: 'action.hover' }}>
                  <Typography variant="body2" color="text.secondary">Tempo TPP Total</Typography>
                  <Typography variant="h5" color="warning.main" fontWeight="bold">
                    {selectedProduto.fichaTecnica.tempoPadraoTotalCentesimal || 0} <Typography component="span" variant="body1">min</Typography>
                  </Typography>
                </Card>

                <Card variant="outlined" sx={{ p: 2, mb: 3 }}>
                  <form onSubmit={handleAddOperacao}>
                    <Grid container spacing={2}>
                      <Grid item xs={12} sm={6}>
                        <TextField
                          label="Operação"
                          fullWidth
                          required
                          size="small"
                          value={opNome}
                          onChange={e => setOpNome(e.target.value)}
                        />
                      </Grid>
                      <Grid item xs={12} sm={6}>
                        <TextField
                          label="Máquina"
                          fullWidth
                          size="small"
                          value={opMaquina}
                          onChange={e => setOpMaquina(e.target.value)}
                        />
                      </Grid>
                      
                      <Grid item xs={6} sm={2}>
                        <TextField label="Ordem" type="number" required size="small" value={opOrdem} onChange={e => setOpOrdem(e.target.value)} fullWidth />
                      </Grid>
                      <Grid item xs={6} sm={2}>
                        <TextField label="Folhas" type="number" required size="small" value={opFolhas} onChange={e => setOpFolhas(e.target.value)} fullWidth />
                      </Grid>
                      <Grid item xs={6} sm={3}>
                        <TextField label="Paradas" type="number" required size="small" value={opParadas} onChange={e => setOpParadas(e.target.value)} fullWidth />
                      </Grid>
                      <Grid item xs={6} sm={3}>
                        <FormControl fullWidth size="small">
                          <InputLabel>Comp.</InputLabel>
                          <Select value={opComprimento} label="Comp." onChange={e => setOpComprimento(e.target.value)}>
                            <MenuItem value="DE_0_A_60">0-60</MenuItem>
                            <MenuItem value="DE_61_A_90">61-90</MenuItem>
                          </Select>
                        </FormControl>
                      </Grid>
                      <Grid item xs={12} sm={2}>
                        <Button type="submit" variant="contained" disabled={isSubmitting} fullWidth disableElevation sx={{ height: 40 }}>
                          +
                        </Button>
                      </Grid>
                    </Grid>
                  </form>
                </Card>

                <TableContainer component={Paper} variant="outlined">
                  <Table size="small">
                    <TableHead>
                      <TableRow>
                        <TableCell>Ordem</TableCell>
                        <TableCell>Op</TableCell>
                        <TableCell>Máq</TableCell>
                        <TableCell align="right">Tempo</TableCell>
                      </TableRow>
                    </TableHead>
                    <TableBody>
                      {selectedProduto.fichaTecnica.operacoes?.sort((a:any, b:any) => a.ordemExecucao - b.ordemExecucao).map((op: any) => (
                        <TableRow key={op.id}>
                          <TableCell>{op.ordemExecucao}</TableCell>
                          <TableCell sx={{ fontWeight: 500 }}>{op.nome}</TableCell>
                          <TableCell color="text.secondary">{op.maquina}</TableCell>
                          <TableCell align="right" sx={{ fontWeight: 600, color: 'warning.main' }}>{op.tempoCalculadoCentesimal}m</TableCell>
                        </TableRow>
                      ))}
                    </TableBody>
                  </Table>
                </TableContainer>
              </Box>
            )}
          </Box>
        )}
      </Modal>

    </Box>
  );
};

export default Produtos;
