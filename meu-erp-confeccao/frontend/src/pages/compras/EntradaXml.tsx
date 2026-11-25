import React, { useState, useEffect, useCallback } from 'react';
import {
  Box, Typography, Button, Paper, Alert, CircularProgress,
  Table, TableBody, TableCell, TableContainer, TableHead, TableRow,
  Dialog, DialogTitle, DialogContent, DialogActions, FormControl, InputLabel, Select, MenuItem,
  Chip, TextField, Tooltip
} from '@mui/material';
import { Upload, FileText, CheckCircle, Link2, AlertCircle, Edit3, RefreshCw } from 'lucide-react';
import api from '../../api/axios';
import { useToast } from '../../contexts/ToastContext';

interface XmlItem {
  id: string;
  produtoNome: string;
  produtoCodigo: string;
  quantidade: number;
  precoUnitario: number;
  valorTotal: number;
  materialId?: string;
  materialNome?: string;
  materialCodigo?: string;
  fatorConversao?: number;
}

interface XmlResponse {
  id: string;
  chaveNfe: string;
  numeroNf: string;
  fornecedorId: string;
  fornecedorNome: string;
  valorTotalNf: number;
  status: 'PENDENTE' | 'PROCESSADA' | string;
  dataRecebimento?: string;
  itens: XmlItem[];
}

interface MaterialInterno {
  id: string;
  codigo: string;
  nome: string;
}

export default function EntradaXml() {
  const { showToast } = useToast();
  const [file, setFile] = useState<File | null>(null);
  const [loading, setLoading] = useState(false);
  const [result, setResult] = useState<XmlResponse | null>(null);
  const [error, setError] = useState('');

  // Histórico de NFs importadas
  const [historico, setHistorico] = useState<XmlResponse[]>([]);
  const [loadingHistorico, setLoadingHistorico] = useState(false);

  // Dialog State
  const [openDialog, setOpenDialog] = useState(false);
  const [itemParaVincular, setItemParaVincular] = useState<XmlItem | null>(null);
  const [materiais, setMateriais] = useState<MaterialInterno[]>([]);
  const [selectedMaterial, setSelectedMaterial] = useState('');
  const [fatorConversao, setFatorConversao] = useState('1');
  const [vinculando, setVinculando] = useState(false);
  const [confirmando, setConfirmando] = useState(false);

  const carregarHistorico = useCallback(async () => {
    try {
      setLoadingHistorico(true);
      const res = await api.get('/procurement/recebimentos-nf?size=20&sort=criadoEm,desc');
      setHistorico(res.data.content || res.data || []);
    } catch (err) {
      console.error('Erro ao carregar histórico de NFs:', err);
    } finally {
      setLoadingHistorico(false);
    }
  }, []);

  useEffect(() => {
    api.get('/inventory/materiais').then(res => {
      setMateriais(res.data.content || res.data || []);
    }).catch(err => console.error(err));

    carregarHistorico();
  }, [carregarHistorico]);

  const handleFileChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    if (e.target.files && e.target.files.length > 0) {
      setFile(e.target.files[0]);
      setError('');
    }
  };

  const handleUpload = async () => {
    if (!file) return;

    const formData = new FormData();
    formData.append('file', file);

    try {
      setLoading(true);
      setError('');

      const res = await api.post('/procurement/recebimentos-nf/upload-xml', formData);
      setResult(res.data);
      setFile(null);
      showToast(`NF ${res.data.numeroNf} importada com sucesso!`, 'success');
      carregarHistorico();
    } catch (err: any) {
      console.error(err);
      const msg = err.response?.data?.message || 'Erro ao processar o XML da NF-e. Verifique se o formato está correto.';
      setError(msg);
      showToast(msg, 'error');
    } finally {
      setLoading(false);
    }
  };

  const selecionarRecebimento = (nf: XmlResponse) => {
    setResult(nf);
    setError('');
  };

  const abrirDialogVincular = (item: XmlItem) => {
    setItemParaVincular(item);
    setSelectedMaterial(item.materialId || '');
    setFatorConversao(item.fatorConversao != null ? String(item.fatorConversao) : '1');
    setOpenDialog(true);
  };

  const confirmarVinculo = async () => {
    if (!itemParaVincular || !selectedMaterial || !result) return;

    const fatorNumerico = Number(fatorConversao.replace(',', '.'));
    if (!Number.isFinite(fatorNumerico) || fatorNumerico <= 0) {
      showToast('Informe um fator de conversão numérico maior que zero.', 'error');
      return;
    }

    try {
      setVinculando(true);
      const payload = {
        fornecedorId: result.fornecedorId,
        materialId: selectedMaterial,
        codigoFornecedor: itemParaVincular.produtoCodigo,
        descricaoFornecedor: itemParaVincular.produtoNome,
        fatorConversao: fatorNumerico
      };

      await api.post('/inventory/materiais-fornecedor/vincular', payload);

      const matObj = materiais.find(m => m.id === selectedMaterial);

      setResult(prev => {
        if (!prev) return prev;
        return {
          ...prev,
          itens: prev.itens.map(it => {
            if (it.id === itemParaVincular.id || it.produtoCodigo === itemParaVincular.produtoCodigo) {
              return {
                ...it,
                materialId: selectedMaterial,
                materialNome: matObj?.nome,
                materialCodigo: matObj?.codigo,
                fatorConversao: fatorNumerico
              };
            }
            return it;
          })
        };
      });

      showToast('Vínculo De/Para salvo com sucesso!', 'success');
      setOpenDialog(false);
      carregarHistorico();
    } catch (err: any) {
      showToast(err.response?.data?.message || 'Erro ao vincular material', 'error');
    } finally {
      setVinculando(false);
    }
  };

  const confirmarEntradaEstoque = async () => {
    if (!result) return;
    try {
      setConfirmando(true);
      await api.post(`/procurement/recebimentos-nf/${result.id}/confirmar-estoque`);
      setResult(prev => (prev ? { ...prev, status: 'PROCESSADA' } : prev));
      showToast('Entrada no estoque confirmada com sucesso!', 'success');
      carregarHistorico();
    } catch (err: any) {
      showToast(err.response?.data?.message || 'Erro ao confirmar entrada no estoque.', 'error');
    } finally {
      setConfirmando(false);
    }
  };

  const isProcessada = result?.status === 'PROCESSADA';
  const todosMapeados = Boolean(result && result.itens.length > 0 && result.itens.every(i => i.materialId != null));

  const fatorPreview = Number(fatorConversao.replace(',', '.'));
  const qtdConvertidaPreview =
    itemParaVincular && Number.isFinite(fatorPreview) && fatorPreview > 0
      ? (itemParaVincular.quantidade * fatorPreview).toFixed(2)
      : null;

  return (
    <Box sx={{ p: 3 }}>
      <Typography variant="h4" sx={{ fontWeight: 800, mb: 3 }}>
        Entrada de NF-e e Mapeamento
      </Typography>

      <Paper sx={{ p: 4, mb: 4, border: '1px dashed var(--border-color)', background: 'var(--bg-card-secondary)', display: 'flex', flexDirection: 'column', alignItems: 'center', gap: 2 }}>
        <FileText size={48} color="var(--text-muted)" />
        <Typography variant="h6" sx={{ color: 'var(--text-secondary)' }}>
          Selecione o arquivo XML da Nota Fiscal
        </Typography>

        <Box sx={{ display: 'flex', gap: 2, alignItems: 'center' }}>
          <Button variant="outlined" component="label">
            Selecionar Arquivo
            <input type="file" hidden accept=".xml" onChange={handleFileChange} />
          </Button>

          {file && <Typography variant="body2">{file.name}</Typography>}
        </Box>

        <Button
          variant="contained"
          color="primary"
          startIcon={loading ? <CircularProgress size={20} color="inherit" /> : <Upload size={20} />}
          disabled={!file || loading}
          onClick={handleUpload}
          sx={{ mt: 2 }}
        >
          {loading ? 'Processando...' : 'Processar XML'}
        </Button>

        {error && <Alert severity="error" sx={{ mt: 2, width: '100%' }}>{error}</Alert>}
      </Paper>

      {result && (
        <Box sx={{ mb: 4 }}>
          <Alert severity={isProcessada ? 'success' : 'info'} icon={<CheckCircle />} sx={{ mb: 3 }}>
            NF <strong>{result.numeroNf}</strong> — Fornecedor <strong>{result.fornecedorNome}</strong> — Total:{' '}
            <strong>
              {Number(result.valorTotalNf || 0).toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' })}
            </strong>{' '}
            ({isProcessada ? 'Estoque já atualizado' : 'Aguardando confirmação de estoque'})
          </Alert>

          <Typography variant="h6" sx={{ mb: 2 }}>Revisão de Itens e De/Para</Typography>
          <TableContainer component={Paper} sx={{ boxShadow: 'none', border: '1px solid var(--border-color)', mb: 3 }}>
            <Table size="small">
              <TableHead sx={{ background: 'var(--bg-card-secondary)' }}>
                <TableRow>
                  <TableCell>Cód. Fornecedor</TableCell>
                  <TableCell>Descrição na NF</TableCell>
                  <TableCell align="right">Qtd NF</TableCell>
                  <TableCell align="right">Fator Conv.</TableCell>
                  <TableCell align="right">Qtd Estoque</TableCell>
                  <TableCell align="center">Vínculo Interno (De/Para)</TableCell>
                </TableRow>
              </TableHead>
              <TableBody>
                {result.itens.map(item => {
                  const fator = item.fatorConversao ?? 1;
                  const qtdEstoque = item.quantidade * fator;
                  return (
                    <TableRow key={item.id}>
                      <TableCell>{item.produtoCodigo}</TableCell>
                      <TableCell>{item.produtoNome}</TableCell>
                      <TableCell align="right">{item.quantidade}</TableCell>
                      <TableCell align="right">{fator}x</TableCell>
                      <TableCell align="right">
                        <strong>{qtdEstoque.toLocaleString('pt-BR', { maximumFractionDigits: 4 })}</strong>
                      </TableCell>
                      <TableCell align="center">
                        {item.materialId ? (
                          <Tooltip title={isProcessada ? 'Recebimento já processado' : 'Clique para alterar vínculo ou fator de conversão'}>
                            <Chip
                              icon={<CheckCircle size={16} />}
                              deleteIcon={!isProcessada ? <Edit3 size={14} /> : undefined}
                              onDelete={!isProcessada ? () => abrirDialogVincular(item) : undefined}
                              onClick={!isProcessada ? () => abrirDialogVincular(item) : undefined}
                              label={item.materialCodigo ? `${item.materialCodigo} - ${item.materialNome}` : (item.materialNome || 'Vinculado')}
                              color="success"
                              size="small"
                              variant="outlined"
                            />
                          </Tooltip>
                        ) : (
                          <Button
                            size="small"
                            variant="outlined"
                            color="warning"
                            startIcon={<Link2 size={16} />}
                            onClick={() => abrirDialogVincular(item)}
                            disabled={isProcessada}
                          >
                            Vincular Material
                          </Button>
                        )}
                      </TableCell>
                    </TableRow>
                  );
                })}
              </TableBody>
            </Table>
          </TableContainer>

          <Box sx={{ display: 'flex', justifyContent: 'flex-end', gap: 2, alignItems: 'center' }}>
            {!todosMapeados && !isProcessada && (
              <Typography color="warning.main" sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                <AlertCircle size={20} /> Vincule todos os itens para confirmar a entrada.
              </Typography>
            )}
            <Button
              variant="contained"
              color="primary"
              disabled={!todosMapeados || confirmando || isProcessada}
              onClick={confirmarEntradaEstoque}
              startIcon={confirmando ? <CircularProgress size={20} color="inherit" /> : <CheckCircle size={20} />}
            >
              {confirmando ? 'Confirmando...' : (isProcessada ? 'Estoque Atualizado' : 'Confirmar Entrada no Estoque')}
            </Button>
          </Box>
        </Box>
      )}

      {/* Histórico de NFs Importadas / Pendentes */}
      <Box sx={{ mt: 4 }}>
        <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 2 }}>
          <Typography variant="h6">Notas Fiscais Importadas</Typography>
          <Button
            size="small"
            variant="text"
            startIcon={<RefreshCw size={16} />}
            onClick={carregarHistorico}
            disabled={loadingHistorico}
          >
            Atualizar
          </Button>
        </Box>

        <TableContainer component={Paper} sx={{ boxShadow: 'none', border: '1px solid var(--border-color)' }}>
          <Table size="small">
            <TableHead sx={{ background: 'var(--bg-card-secondary)' }}>
              <TableRow>
                <TableCell>Nº NF</TableCell>
                <TableCell>Fornecedor</TableCell>
                <TableCell align="right">Valor Total</TableCell>
                <TableCell align="center">Status</TableCell>
                <TableCell align="center">Ação</TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {historico.length === 0 ? (
                <TableRow>
                  <TableCell colSpan={5} align="center" sx={{ py: 3, color: 'text.secondary' }}>
                    {loadingHistorico ? 'Carregando...' : 'Nenhuma nota fiscal importada ainda.'}
                  </TableCell>
                </TableRow>
              ) : (
                historico.map(nf => (
                  <TableRow key={nf.id} selected={result?.id === nf.id}>
                    <TableCell>{nf.numeroNf || '-'}</TableCell>
                    <TableCell>{nf.fornecedorNome || '-'}</TableCell>
                    <TableCell align="right">
                      {Number(nf.valorTotalNf || 0).toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' })}
                    </TableCell>
                    <TableCell align="center">
                      <Chip
                        label={nf.status === 'PROCESSADA' ? 'Processada' : 'Pendente'}
                        color={nf.status === 'PROCESSADA' ? 'success' : 'warning'}
                        size="small"
                        variant="outlined"
                      />
                    </TableCell>
                    <TableCell align="center">
                      <Button size="small" variant="outlined" onClick={() => selecionarRecebimento(nf)}>
                        {nf.status === 'PROCESSADA' ? 'Visualizar' : 'Retomar De/Para'}
                      </Button>
                    </TableCell>
                  </TableRow>
                ))
              )}
            </TableBody>
          </Table>
        </TableContainer>
      </Box>

      {/* Dialog para Vincular Material */}
      <Dialog open={openDialog} onClose={() => setOpenDialog(false)} maxWidth="sm" fullWidth>
        <DialogTitle>Vincular Produto do Fornecedor</DialogTitle>
        <DialogContent dividers>
          <Typography variant="body2" color="text.secondary" sx={{ mb: 2 }}>
            Você está comprando <strong>{itemParaVincular?.produtoNome}</strong> ({itemParaVincular?.produtoCodigo}).
            Selecione o material interno correspondente e o fator de conversão para o estoque.
          </Typography>

          <FormControl fullWidth sx={{ mt: 1 }}>
            <InputLabel>Selecione o Material Interno</InputLabel>
            <Select
              value={selectedMaterial}
              onChange={(e) => setSelectedMaterial(e.target.value)}
              label="Selecione o Material Interno"
            >
              {materiais.map(mat => (
                <MenuItem key={mat.id} value={mat.id}>
                  {mat.codigo} - {mat.nome}
                </MenuItem>
              ))}
            </Select>
          </FormControl>

          <TextField
            fullWidth
            type="number"
            label="Fator de Conversão"
            value={fatorConversao}
            onChange={(e) => setFatorConversao(e.target.value)}
            inputProps={{ min: 0.0001, step: 'any' }}
            helperText={
              qtdConvertidaPreview
                ? `Ex: 1 rolo = 50 metros → informe 50. Entrada prevista no estoque: ${qtdConvertidaPreview}`
                : 'Ex: 1 rolo = 50 metros → informe 50 (padrão: 1)'
            }
            sx={{ mt: 2 }}
          />
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setOpenDialog(false)} color="inherit">Cancelar</Button>
          <Button
            onClick={confirmarVinculo}
            variant="contained"
            disabled={!selectedMaterial || vinculando}
          >
            {vinculando ? 'Salvando...' : 'Salvar Vínculo'}
          </Button>
        </DialogActions>
      </Dialog>
    </Box>
  );
}
