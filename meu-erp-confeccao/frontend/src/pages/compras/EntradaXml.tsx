import React, { useState } from 'react';
import { 
  Box, Typography, Button, Paper, Alert, CircularProgress, 
  Table, TableBody, TableCell, TableContainer, TableHead, TableRow 
} from '@mui/material';
import { Upload, FileText, CheckCircle } from 'lucide-react';
import api from '../../api/axios';

interface XmlItem {
  id: string;
  produtoNome: string;
  produtoCodigo: string;
  quantidade: number;
  precoUnitario: number;
  valorTotal: number;
}

interface XmlResponse {
  id: string;
  chaveNfe: string;
  numeroNf: string;
  fornecedorNome: string;
  valorTotalNf: number;
  itens: XmlItem[];
}

export default function EntradaXml() {
  const [file, setFile] = useState<File | null>(null);
  const [loading, setLoading] = useState(false);
  const [result, setResult] = useState<XmlResponse | null>(null);
  const [error, setError] = useState('');

  const handleFileChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    if (e.target.files && e.target.files.length > 0) {
      setFile(e.target.files[0]);
      setError('');
      setResult(null);
    }
  };

  const handleUpload = async () => {
    if (!file) return;
    
    const formData = new FormData();
    formData.append('file', file);
    
    try {
      setLoading(true);
      setError('');
      
      const res = await api.post('/api/procurement/recebimentos-nf/upload-xml', formData, {
        headers: { 'Content-Type': 'multipart/form-data' }
      });
      
      setResult(res.data);
    } catch (err: any) {
      console.error(err);
      setError(err.response?.data?.message || 'Erro ao processar o XML da NF-e. Verifique se o formato está correto.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <Box sx={{ p: 3 }}>
      <Typography variant="h4" sx={{ fontWeight: 800, mb: 3 }}>
        Entrada de NF-e via XML
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
            XML importado com sucesso! NF <strong>{result.numeroNf}</strong> do fornecedor <strong>{result.fornecedorNome}</strong> (Chave: {result.chaveNfe}).
          </Alert>

          <Typography variant="h6" sx={{ mb: 2 }}>Itens Reconhecidos</Typography>
          <TableContainer component={Paper} sx={{ boxShadow: 'none', border: '1px solid var(--border-color)' }}>
            <Table size="small">
              <TableHead sx={{ background: 'var(--bg-card-secondary)' }}>
                <TableRow>
                  <TableCell>Cód. Prod</TableCell>
                  <TableCell>Descrição</TableCell>
                  <TableCell align="right">Qtd</TableCell>
                  <TableCell align="right">Vlr. Unitário</TableCell>
                  <TableCell align="right">Vlr. Total</TableCell>
                </TableRow>
              </TableHead>
              <TableBody>
                {result.itens.map(item => (
                  <TableRow key={item.id}>
                    <TableCell>{item.produtoCodigo}</TableCell>
                    <TableCell>{item.produtoNome}</TableCell>
                    <TableCell align="right">{item.quantidade}</TableCell>
                    <TableCell align="right">
                      {new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' }).format(item.precoUnitario)}
                    </TableCell>
                    <TableCell align="right">
                      {new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' }).format(item.valorTotal)}
                    </TableCell>
                  </TableRow>
                ))}
              </TableBody>
            </Table>
          </TableContainer>
        </Box>
      )}
    </Box>
  );
}
