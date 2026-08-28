import React, { useState, useEffect } from 'react';

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
  FormControl,
  InputLabel,
  Select,
  MenuItem,
  Stack,
  Chip,
  CircularProgress,
  Tabs,
  Tab,
  Grid
} from '@mui/material';
import ArrowUpwardIcon from '@mui/icons-material/ArrowUpward';
import ArrowDownwardIcon from '@mui/icons-material/ArrowDownward';
import AddIcon from '@mui/icons-material/Add';

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
  const [activeTab, setActiveTab] = useState(0); // 0 = Materiais, 1 = Produtos
  const { hasPermission } = useAuth();
  const canEdit = hasPermission('ESTOQUE_EDIT');

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
  
  const [tipoMaterial, setTipoMaterial] = useState('');
  const [composicao, setComposicao] = useState('');
  const [ncm, setNcm] = useState('');
  const [unidadeCompra, setUnidadeCompra] = useState('');
  const [fatorConversao, setFatorConversao] = useState<string>('');
  const [largura, setLargura] = useState<string>('');
  const [gramatura, setGramatura] = useState<string>('');
  const [rendimento, setRendimento] = useState<string>('');
  const [status, setStatus] = useState('ATIVO');

  const [isSubmitting, setIsSubmitting] = useState(false);

  // Movimentacao Form
  const [movType, setMovType] = useState<'ENTRADA' | 'SAIDA'>('ENTRADA');
  const [movItemId, setMovItemId] = useState(''); 
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
        custoUnitario: parseFloat(custoUnitario) || 0,
        tipoMaterial,
        composicao,
        ncm,
        unidadeCompra,
        fatorConversao: parseFloat(fatorConversao) || null,
        largura: parseFloat(largura) || null,
        gramatura: parseFloat(gramatura) || null,
        rendimento: parseFloat(rendimento) || null,
        status,
        fornecedorPadraoId: null
      });
      
      setCodigo('');
      setNome('');
      setDescricao('');
      setCustoUnitario('');
      setTipoMaterial('');
      setComposicao('');
      setNcm('');
      setUnidadeCompra('');
      setFatorConversao('');
      setLargura('');
      setGramatura('');
      setRendimento('');
      setStatus('ATIVO');
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
      if (activeTab === 0) {
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

  const columnsMateriais: ColumnDef<any, any, any>[] = React.useMemo(() => [
    {
      accessorKey: 'codigo',
      header: 'Código',
      cell: (info) => <Chip label={info.getValue() as string} color="warning" variant="outlined" size="small" />
    },
    {
      accessorKey: 'nome',
      header: 'Material',
      cell: (info) => <Typography sx={{ fontWeight: 500 }}>{info.getValue() as string}</Typography>
    },
    {
      accessorKey: 'unidadeMedida',
      header: 'Unidade',
      cell: (info) => <Typography color="text.secondary">{info.getValue() as string}</Typography>
    },
    {
      accessorKey: 'quantidadeAtual',
      header: 'Qtd Atual',
      cell: (info) => <Typography color="primary" sx={{ fontWeight: 600 }}>{info.getValue() as number || 0}</Typography>
    },
    {
      accessorKey: 'custoUnitario',
      header: 'Custo Unitário',
      cell: (info) => <Typography color="success.main" sx={{ fontWeight: 600 }}>{formatCurrency(info.getValue() as number)}</Typography>
    }
  ], []);

  const flatSkus = React.useMemo(() => {
    return produtos.flatMap(p => p.skus.map(sku => ({
      ...sku,
      produtoBaseNome: p.nome,
      produtoBaseCodigo: p.codigo
    })));
  }, [produtos]);

  const columnsProdutos: ColumnDef<any, any, any>[] = React.useMemo(() => [
    {
      id: 'produto',
      header: 'Produto Base',
      cell: (info) => <Typography sx={{ fontWeight: 500 }}>{info.row.original.produtoBaseCodigo} - {info.row.original.produtoBaseNome}</Typography>
    },
    {
      id: 'sku',
      header: 'SKU (Cor/Tamanho)',
      cell: (info) => (
        <Stack direction="row" spacing={1}>
          <Chip label={info.row.original.cor} color="warning" variant="outlined" size="small" />
          <Chip label={info.row.original.tamanho} size="small" />
        </Stack>
      )
    },
    {
      accessorKey: 'codigoBarras',
      header: 'Código Barras',
      cell: (info) => <Typography color="text.secondary">{info.getValue() || '-'}</Typography>
    },
    {
      accessorKey: 'quantidadeAtual',
      header: 'Qtd Atual',
      cell: (info) => <Typography color="primary" sx={{ fontWeight: 600 }}>{info.getValue() || 0}</Typography>
    }
  ], []);

  return (
    <Box className="animate-fade-in">
      <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 4 }}>
        <Box>
          <Typography variant="h4" component="h1" gutterBottom sx={{ fontWeight: 'bold' }}>
            Estoque
          </Typography>
          <Typography variant="body1" color="text.secondary">
            Gerencie o estoque de Matérias-Primas e Produtos Acabados (SKUs).
          </Typography>
        </Box>
        {canEdit && (
          <Stack direction="row" spacing={2}>
            <Button 
              variant="outlined" 
              color="primary" 
              onClick={() => setIsMovimentacaoModalOpen(true)}
              size="large"
            >
              Movimentar
            </Button>
            {activeTab === 0 && (
              <Button 
                variant="contained" 
                color="primary" 
                startIcon={<AddIcon />}
                onClick={() => setIsMaterialModalOpen(true)}
                size="large"
                disableElevation
              >
                Novo Material
              </Button>
            )}
          </Stack>
        )}
      </Box>

      <Box sx={{ borderBottom: 1, borderColor: 'divider', mb: 3 }}>
        <Tabs value={activeTab} onChange={(_, val) => setActiveTab(val)}>
          <Tab label="Matéria-Prima" />
          <Tab label="Produto Acabado (SKUs)" />
        </Tabs>
      </Box>

      <Card variant="outlined" sx={{ borderRadius: 2, overflow: 'hidden' }}>
        {loading ? (
          <Box sx={{ p: 6, display: 'flex', justifyContent: 'center' }}>
            <CircularProgress />
          </Box>
        ) : error ? (
          <Box sx={{ p: 4, textAlign: 'center' }}>
            <Typography color="error">{error}</Typography>
          </Box>
        ) : activeTab === 0 ? (
          materiais.length === 0 ? (
            <Box sx={{ p: 8, display: 'flex', flexDirection: 'column', alignItems: 'center', color: 'text.secondary' }}>
              <Typography>Nenhum material encontrado.</Typography>
            </Box>
          ) : (
            <DataTable columns={columnsMateriais} data={materiais} />
          )
        ) : (
          flatSkus.length === 0 ? (
            <Box sx={{ p: 8, display: 'flex', flexDirection: 'column', alignItems: 'center', color: 'text.secondary' }}>
              <Typography>Nenhum SKU encontrado. Você precisa cadastrar SKUs nos produtos.</Typography>
            </Box>
          ) : (
            <DataTable columns={columnsProdutos} data={flatSkus} />
          )
        )}
      </Card>

      {/* Modal Cadastro de Material */}
      <Modal isOpen={isMaterialModalOpen} onClose={() => setIsMaterialModalOpen(false)} title="Novo Material" width="500px">
        <form onSubmit={handleAddMaterial}>
          <Stack spacing={3} sx={{ mt: 1 }}>
            <TextField
              label="Código"
              variant="outlined"
              fullWidth
              required
              value={codigo}
              onChange={(e) => setCodigo(e.target.value)}
              placeholder="Ex: TEC-01"
            />
            <TextField
              label="Nome do Material"
              variant="outlined"
              fullWidth
              required
              value={nome}
              onChange={(e) => setNome(e.target.value)}
              placeholder="Ex: Tecido Malha"
            />
            <Grid container spacing={2}>
              <Grid size={6}>
                <FormControl fullWidth>
                  <InputLabel id="medida-label">Medida</InputLabel>
                  <Select
                    labelId="medida-label"
                    value={unidadeMedida}
                    label="Medida"
                    onChange={e => setUnidadeMedida(e.target.value)}
                  >
                    <MenuItem value="KG">Quilo (kg)</MenuItem>
                    <MenuItem value="METRO">Metro (m)</MenuItem>
                    <MenuItem value="UNIDADE">Unidade (un)</MenuItem>
                    <MenuItem value="GRAMA">Grama (g)</MenuItem>
                  </Select>
                </FormControl>
              </Grid>
              <Grid size={6}>
                <TextField
                  label="Custo (R$)"
                  type="number"
                  variant="outlined"
                  fullWidth
                  required
                  slotProps={{ htmlInput: { step: "0.01", min: "0" } }}
                  value={custoUnitario}
                  onChange={(e) => setCustoUnitario(e.target.value)}
                />
              </Grid>
              <Grid size={6}>
                <TextField
                  label="Tipo Material"
                  variant="outlined"
                  fullWidth
                  value={tipoMaterial}
                  onChange={(e) => setTipoMaterial(e.target.value)}
                  placeholder="Ex: Têxtil, Aviamento"
                />
              </Grid>
              <Grid size={6}>
                <TextField
                  label="Composição"
                  variant="outlined"
                  fullWidth
                  value={composicao}
                  onChange={(e) => setComposicao(e.target.value)}
                />
              </Grid>
              <Grid size={6}>
                <TextField
                  label="NCM"
                  variant="outlined"
                  fullWidth
                  value={ncm}
                  onChange={(e) => setNcm(e.target.value)}
                />
              </Grid>
              <Grid size={6}>
                <TextField
                  label="Un. Compra"
                  variant="outlined"
                  fullWidth
                  value={unidadeCompra}
                  onChange={(e) => setUnidadeCompra(e.target.value)}
                />
              </Grid>
              <Grid size={6}>
                <TextField
                  label="Fator Conversão"
                  type="number"
                  variant="outlined"
                  fullWidth
                  value={fatorConversao}
                  onChange={(e) => setFatorConversao(e.target.value)}
                />
              </Grid>
              <Grid size={6}>
                <TextField
                  label="Largura"
                  type="number"
                  variant="outlined"
                  fullWidth
                  value={largura}
                  onChange={(e) => setLargura(e.target.value)}
                />
              </Grid>
              <Grid size={6}>
                <TextField
                  label="Gramatura"
                  type="number"
                  variant="outlined"
                  fullWidth
                  value={gramatura}
                  onChange={(e) => setGramatura(e.target.value)}
                />
              </Grid>
              <Grid size={6}>
                <TextField
                  label="Rendimento"
                  type="number"
                  variant="outlined"
                  fullWidth
                  value={rendimento}
                  onChange={(e) => setRendimento(e.target.value)}
                />
              </Grid>
            </Grid>
            <TextField
              label="Descrição"
              variant="outlined"
              fullWidth
              multiline
              rows={2}
              value={descricao}
              onChange={(e) => setDescricao(e.target.value)}
            />
            <Box sx={{ display: 'flex', justifyContent: 'flex-end', pt: 2 }}>
              <Button onClick={() => setIsMaterialModalOpen(false)} sx={{ mr: 2 }}>Cancelar</Button>
              <Button 
                type="submit" 
                variant="contained" 
                color="primary"
                disabled={isSubmitting}
                startIcon={isSubmitting && <CircularProgress size={20} color="inherit" />}
                disableElevation
              >
                Salvar Material
              </Button>
            </Box>
          </Stack>
        </form>
      </Modal>

      {/* Modal Movimentação */}
      <Modal isOpen={isMovimentacaoModalOpen} onClose={() => setIsMovimentacaoModalOpen(false)} title="Movimentação Manual" width="500px">
        <form onSubmit={handleMovimentacao}>
          <Stack spacing={3} sx={{ mt: 1 }}>
            <Stack direction="row" spacing={2}>
              <Button
                fullWidth
                variant={movType === 'ENTRADA' ? 'contained' : 'outlined'}
                color="success"
                onClick={() => setMovType('ENTRADA')}
                startIcon={<ArrowUpwardIcon />}
                disableElevation
              >
                Entrada
              </Button>
              <Button
                fullWidth
                variant={movType === 'SAIDA' ? 'contained' : 'outlined'}
                color="error"
                onClick={() => setMovType('SAIDA')}
                startIcon={<ArrowDownwardIcon />}
                disableElevation
              >
                Saída
              </Button>
            </Stack>

            <FormControl fullWidth required>
              <InputLabel id="item-label">
                Selecione o Item ({activeTab === 0 ? 'Matéria-Prima' : 'SKU'})
              </InputLabel>
              <Select
                labelId="item-label"
                value={movItemId}
                label={`Selecione o Item (${activeTab === 0 ? 'Matéria-Prima' : 'SKU'})`}
                onChange={e => setMovItemId(e.target.value)}
              >
                <MenuItem value=""><em>Selecione...</em></MenuItem>
                {activeTab === 0 ? (
                  materiais.map(m => (
                    <MenuItem key={m.id} value={m.id}>{m.codigo} - {m.nome} (Atual: {m.quantidadeAtual})</MenuItem>
                  ))
                ) : (
                  produtos.flatMap(p => p.skus.map(sku => (
                    <MenuItem key={sku.id} value={sku.id}>
                      {p.codigo} - {sku.cor}/{sku.tamanho} (Atual: {sku.quantidadeAtual})
                    </MenuItem>
                  )))
                )}
              </Select>
            </FormControl>

            <TextField
              label="Quantidade"
              type="number"
              variant="outlined"
              fullWidth
              required
              slotProps={{ 
                htmlInput: {
                  step: activeTab === 0 ? "0.001" : "1",
                  min: "0.001"
                }
              }}
              value={movQtd}
              onChange={(e) => setMovQtd(e.target.value)}
            />

            <TextField
              label="Doc. Referência (Opcional)"
              variant="outlined"
              fullWidth
              value={movDoc}
              onChange={(e) => setMovDoc(e.target.value)}
              placeholder="Ex: NF 1234, Contagem, Ajuste..."
            />

            <Box sx={{ display: 'flex', justifyContent: 'flex-end', pt: 2 }}>
              <Button onClick={() => setIsMovimentacaoModalOpen(false)} sx={{ mr: 2 }}>Cancelar</Button>
              <Button 
                type="submit" 
                variant="contained" 
                color="primary"
                disabled={isSubmitting}
                startIcon={isSubmitting && <CircularProgress size={20} color="inherit" />}
                disableElevation
              >
                Confirmar
              </Button>
            </Box>
          </Stack>
        </form>
      </Modal>
    </Box>
  );
};

export default Estoque;
