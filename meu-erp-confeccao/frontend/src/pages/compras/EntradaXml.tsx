import React, { useState, useEffect } from 'react';
import { 
  Box, Typography, Button, Paper, Alert, CircularProgress, 
  Table, TableBody, TableCell, TableContainer, TableHead, TableRow,
  Dialog, DialogTitle, DialogContent, DialogActions, FormControl, InputLabel, Select, MenuItem,
  Chip
} from '@mui/material';
import { Upload, FileText, CheckCircle, Link2, AlertCircle } from 'lucide-react';
import api from '../../api/axios';

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
  itens: XmlItem[];
}

interface MaterialInterno {
  id: string;
  codigo: string;
  nome: string;
}

export default function EntradaXml() {
  const [file, setFile] = useState<File | null>(null);
  const [loading, setLoading] = useState(false);
  const [result, setResult] = useState<XmlResponse | null>(null);
  const [error, setError] = useState('');
  
  // Dialog State
  const [openDialog, setOpenDialog] = useState(false);
  const [itemParaVincular, setItemParaVincular] = useState<XmlItem | null>(null);
  const [materiais, setMateriais] = useState<MaterialInterno[]>([]);
  const [selectedMaterial, setSelectedMaterial] = useState('');
  const [vinculando, setVinculando] = useState(false);
  const [confirmando, setConfirmando] = useState(false);
  const [sucessoEstoque, setSucessoEstoque] = useState(false);

  useEffect(() => {
    // Busca materiais internos para o select
    api.get('/inventory/materiais').then(res => {
      setMateriais(res.data.content || res.data);
    }).catch(err => console.error(err));
  }, []);

  const handleFileChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    if (e.target.files && e.target.files.length > 0) {
      setFile(e.target.files[0]);
      setError('');
      setResult(null);
      setSucessoEstoque(false);
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
      setSucessoEstoque(false);
    } catch (err: any) {
      console.error(err);
      setError(err.response?.data?.message || 'Erro ao processar o XML da NF-e. Verifique se o formato está correto.');
    } finally {
      setLoading(false);
    }
  };

  const abrirDialogVincular = (item: XmlItem) => {
    setItemParaVincular(item);
    setSelectedMaterial('');
    setOpenDialog(true);
  };

  const confirmarVinculo = async () => {
    if (!itemParaVincular || !selectedMaterial || !result) return;
    
    try {
      setVinculando(true);
      const payload = {
        fornecedorId: result.fornecedorId,
        materialId: selectedMaterial,
        codigoFornecedor: itemParaVincular.produtoCodigo,
        descricaoFornecedor: itemParaVincular.produtoNome,
        fatorConversao: 1.0 // Simplificação inicial
      };

      await api.post('/inventory/materiais-fornecedor/vincular', payload);

      // Atualiza o estado local para refletir o vinculo
      const materialNome = materiais.find(m => m.id === selectedMaterial)?.nome;
      
      setResult(prev => {
        if (!prev) return prev;
        return {
          ...prev,
          itens: prev.itens.map(it => {
            if (it.id === itemParaVincular.id) {
              return { ...it, materialId: selectedMaterial, materialNome: materialNome };
            }
            return it;
          })
        };
      });

      setOpenDialog(false);
    } catch (err: any) {
      alert(err.response?.data?.message || 'Erro ao vincular material');
    } finally {
      setVinculando(false);
    }
  };

  const confirmarEntradaEstoque = async () => {
    if (!result) return;
    try {
      setConfirmando(true);
      await api.post(`/procurement/recebimentos-nf/${result.id}/confirmar-estoque`);
      setSucessoEstoque(true);
    } catch (err: any) {
      alert(err.response?.data?.message || 'Erro ao confirmar entrada no estoque.');
    } finally {
      setConfirmando(false);
    }
  };

  const todosMapeados = result?.itens.every(i => i.materialId != null);

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
        <Box>
          <Alert severity="success" icon={<CheckCircle />} sx={{ mb: 3 }}>
            XML importado! NF <strong>{result.numeroNf}</strong> do fornecedor <strong>{result.fornecedorNome}</strong>.
          </Alert>

          {sucessoEstoque && (
             <Alert severity="success" sx={{ mb: 3, border: '1px solid green' }}>
             Entrada no estoque confirmada com sucesso! Os materiais já estão disponíveis no inventário.
           </Alert>
          )}

          <Typography variant="h6" sx={{ mb: 2 }}>Revisão de Itens e De/Para</Typography>
          <TableContainer component={Paper} sx={{ boxShadow: 'none', border: '1px solid var(--border-color)', mb: 3 }}>
            <Table size="small">
              <TableHead sx={{ background: 'var(--bg-card-secondary)' }}>
                <TableRow>
                  <TableCell>Cód. Fornecedor</TableCell>
                  <TableCell>Descrição na NF</TableCell>
                  <TableCell align="right">Qtd</TableCell>
                  <TableCell align="center">Vínculo Interno (De/Para)</TableCell>
                </TableRow>
              </TableHead>
              <TableBody>
                {result.itens.map(item => (
                  <TableRow key={item.id}>
                    <TableCell>{item.produtoCodigo}</TableCell>
                    <TableCell>{item.produtoNome}</TableCell>
                    <TableCell align="right">{item.quantidade}</TableCell>
                    <TableCell align="center">
                      {item.materialId ? (
                        <Chip 
                          icon={<CheckCircle size={16} />} 
                          label={item.materialNome || 'Vinculado'} 
                          color="success" 
                          size="small" 
                          variant="outlined" 
                        />
                      ) : (
                        <Button 
                          size="small" 
                          variant="outlined" 
                          color="warning" 
                          startIcon={<Link2 size={16} />}
                          onClick={() => abrirDialogVincular(item)}
                        >
                          Vincular Material
                        </Button>
                      )}
                    </TableCell>
                  </TableRow>
                ))}
              </TableBody>
            </Table>
          </TableContainer>

          <Box sx={{ display: 'flex', justifyContent: 'flex-end', gap: 2 }}>
             {!todosMapeados && !sucessoEstoque && (
                <Typography color="warning.main" sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                  <AlertCircle size={20} /> Vincule todos os itens para confirmar a entrada.
                </Typography>
             )}
             <Button
               variant="contained"
               color="primary"
               disabled={!todosMapeados || confirmando || sucessoEstoque}
               onClick={confirmarEntradaEstoque}
               startIcon={confirmando ? <CircularProgress size={20} color="inherit"/> : <CheckCircle size={20} />}
             >
                {confirmando ? 'Confirmando...' : (sucessoEstoque ? 'Estoque Atualizado' : 'Confirmar Entrada no Estoque')}
             </Button>
          </Box>
        </Box>
      )}

      {/* Dialog para Vincular Material */}
      <Dialog open={openDialog} onClose={() => setOpenDialog(false)} maxWidth="sm" fullWidth>
        <DialogTitle>Vincular Produto do Fornecedor</DialogTitle>
        <DialogContent dividers>
          <Typography variant="body2" color="text.secondary" sx={{ mb: 2 }}>
            Você está comprando <strong>{itemParaVincular?.produtoNome}</strong> ({itemParaVincular?.produtoCodigo}). 
            Selecione a qual material do seu estoque este produto corresponde.
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
