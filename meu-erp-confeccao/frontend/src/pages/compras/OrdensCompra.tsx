import React, { useState, useEffect } from 'react';
import { 
  Box, Typography, Button, TextField, Table, TableBody, TableCell, 
  TableContainer, TableHead, TableRow, Paper, IconButton, Chip 
} from '@mui/material';
import { Search, Plus, Eye, CheckCircle, Clock } from 'lucide-react';
import api from '../../services/api';

interface OrdemCompra {
  id: string;
  fornecedorNome: string;
  numeroPedido: string;
  dataEmissao: string;
  dataPrevisaoEntrega: string;
  status: string;
  valorTotal: number;
}

export default function OrdensCompra() {
  const [ordens, setOrdens] = useState<OrdemCompra[]>([]);
  const [search, setSearch] = useState('');
  const [loading, setLoading] = useState(false);

  const fetchOrdens = async () => {
    try {
      setLoading(true);
      const res = await api.get('/api/procurement/ordens-compra', {
        params: { numeroPedido: search }
      });
      setOrdens(res.data.content);
    } catch (error) {
      console.error(error);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchOrdens();
  }, []); // eslint-disable-line

  const getStatusChip = (status: string) => {
    switch(status) {
      case 'PENDENTE': return <Chip label="Pendente" color="warning" size="small" icon={<Clock size={16} />} />;
      case 'ENTREGUE': return <Chip label="Entregue" color="success" size="small" icon={<CheckCircle size={16} />} />;
      case 'CANCELADO': return <Chip label="Cancelado" color="error" size="small" />;
      default: return <Chip label={status} size="small" />;
    }
  };

  return (
    <Box sx={{ p: 3 }}>
      <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 3 }}>
        <Typography variant="h4" sx={{ fontWeight: 800 }}>Pedidos de Compra</Typography>
        <Button variant="contained" color="primary" startIcon={<Plus size={20} />}>
          Novo Pedido
        </Button>
      </Box>

      {/* Box de busca respeitando as regras do usuário:
          - Sem hover effect (sem scale ou shadow hover na caixa)
          - Input normal
          - Search ativado ao dar Enter
          - Ícone do botão trocado/arredondado
      */}
      <Box 
        sx={{ 
          display: 'flex', gap: 2, mb: 3, p: 2, 
          background: 'var(--bg-card)', 
          border: '1px solid var(--border-color)', 
          borderRadius: 'var(--radius-md)' 
        }}
      >
        <TextField 
          label="Buscar por Número do Pedido" 
          variant="outlined" 
          size="small"
          fullWidth
          value={search}
          onChange={(e) => setSearch(e.target.value)}
          onKeyDown={(e) => {
            if (e.key === 'Enter') fetchOrdens();
          }}
        />
        <Button 
          variant="contained" 
          onClick={fetchOrdens}
          sx={{ borderRadius: '50%', minWidth: '40px', width: '40px', height: '40px', p: 0 }}
        >
          <Search size={20} />
        </Button>
      </Box>

      <TableContainer component={Paper} sx={{ boxShadow: 'none', border: '1px solid var(--border-color)' }}>
        <Table>
          <TableHead sx={{ background: 'var(--bg-card-secondary)' }}>
            <TableRow>
              <TableCell>Número</TableCell>
              <TableCell>Fornecedor</TableCell>
              <TableCell>Data Emissão</TableCell>
              <TableCell>Previsão</TableCell>
              <TableCell>Valor Total</TableCell>
              <TableCell>Status</TableCell>
              <TableCell align="right">Ações</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {loading ? (
              <TableRow><TableCell colSpan={7} align="center">Carregando...</TableCell></TableRow>
            ) : ordens.length === 0 ? (
              <TableRow><TableCell colSpan={7} align="center">Nenhuma ordem de compra encontrada.</TableCell></TableRow>
            ) : (
              ordens.map((oc) => (
                <TableRow key={oc.id}>
                  <TableCell>{oc.numeroPedido}</TableCell>
                  <TableCell>{oc.fornecedorNome}</TableCell>
                  <TableCell>{new Date(oc.dataEmissao).toLocaleDateString()}</TableCell>
                  <TableCell>{oc.dataPrevisaoEntrega ? new Date(oc.dataPrevisaoEntrega).toLocaleDateString() : '-'}</TableCell>
                  <TableCell>
                    {new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' }).format(oc.valorTotal || 0)}
                  </TableCell>
                  <TableCell>{getStatusChip(oc.status)}</TableCell>
                  <TableCell align="right">
                    <IconButton size="small" color="primary">
                      <Eye size={20} />
                    </IconButton>
                  </TableCell>
                </TableRow>
              ))
            )}
          </TableBody>
        </Table>
      </TableContainer>
    </Box>
  );
}
