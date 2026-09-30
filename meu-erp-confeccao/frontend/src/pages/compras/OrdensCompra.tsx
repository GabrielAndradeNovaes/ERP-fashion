import React, { useState, useEffect } from 'react';
import { 
  Box, Typography, Button, TextField, Table, TableBody, TableCell, 
  TableContainer, TableHead, TableRow, Paper, IconButton, Chip, Tooltip 
} from '@mui/material';
import { Search, Plus, Eye, CheckCircle, Clock, XCircle, Send } from 'lucide-react';
import api from '../../api/axios';
import NovoPedidoModal from './NovoPedidoModal';
import toast, { Toaster } from 'react-hot-toast';

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
  const [modalOpen, setModalOpen] = useState(false);

  const fetchOrdens = async () => {
    try {
      setLoading(true);
      const res = await api.get('/procurement/ordens-compra', {
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

  const handleStatusChange = async (id: string, novoStatus: string) => {
    try {
      await api.patch(`/procurement/ordens-compra/${id}/status`, null, {
        params: { status: novoStatus }
      });
      toast.success(`Ordem de compra atualizada para ${novoStatus}`);
      fetchOrdens();
    } catch (error) {
      console.error(error);
      toast.error('Erro ao atualizar status');
    }
  };

  const getStatusChip = (status: string) => {
    switch(status) {
      case 'RASCUNHO': return <Chip label="Rascunho" color="default" size="small" icon={<Clock size={16} />} />;
      case 'EMITIDA': return <Chip label="Emitida" color="info" size="small" icon={<Send size={16} />} />;
      case 'PARCIALMENTE_RECEBIDA': return <Chip label="Parcial" color="warning" size="small" icon={<Clock size={16} />} />;
      case 'RECEBIDA': return <Chip label="Recebida" color="success" size="small" icon={<CheckCircle size={16} />} />;
      case 'CANCELADA': return <Chip label="Cancelada" color="error" size="small" icon={<XCircle size={16} />} />;
      default: return <Chip label={status} size="small" />;
    }
  };

  return (
    <Box sx={{ p: 3 }}>
      <Toaster position="top-right" />
      <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 3 }}>
        <Typography variant="h4" sx={{ fontWeight: 800 }}>Pedidos de Compra</Typography>
        <Button 
          variant="contained" 
          color="primary" 
          startIcon={<Plus size={20} />}
          onClick={() => setModalOpen(true)}
        >
          Novo Pedido
        </Button>
      </Box>

      <NovoPedidoModal 
        open={modalOpen} 
        onClose={() => setModalOpen(false)} 
        onSuccess={fetchOrdens} 
      />

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
                    {oc.status === 'RASCUNHO' && (
                      <>
                        <Tooltip title="Emitir Pedido">
                          <IconButton size="small" color="info" onClick={() => handleStatusChange(oc.id, 'EMITIDA')}>
                            <Send size={20} />
                          </IconButton>
                        </Tooltip>
                        <Tooltip title="Cancelar Pedido">
                          <IconButton size="small" color="error" onClick={() => handleStatusChange(oc.id, 'CANCELADA')}>
                            <XCircle size={20} />
                          </IconButton>
                        </Tooltip>
                      </>
                    )}
                    {oc.status === 'EMITIDA' && (
                      <Tooltip title="Cancelar Pedido">
                        <IconButton size="small" color="error" onClick={() => handleStatusChange(oc.id, 'CANCELADA')}>
                          <XCircle size={20} />
                        </IconButton>
                      </Tooltip>
                    )}
                    <Tooltip title="Visualizar Detalhes">
                      <IconButton size="small" color="primary">
                        <Eye size={20} />
                      </IconButton>
                    </Tooltip>
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
