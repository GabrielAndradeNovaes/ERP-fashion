import React, { useState, useEffect } from 'react';
import { 
  Dialog, DialogTitle, DialogContent, DialogActions, 
  Button, TextField, Grid, MenuItem, Box, Typography, IconButton 
} from '@mui/material';
import { Plus, Trash2 } from 'lucide-react';
import api from '../../api/axios';

interface NovoPedidoModalProps {
  open: boolean;
  onClose: () => void;
  onSuccess: () => void;
}

export default function NovoPedidoModal({ open, onClose, onSuccess }: NovoPedidoModalProps) {
  const [fornecedores, setFornecedores] = useState<any[]>([]);
  const [materiais, setMateriais] = useState<any[]>([]);
  const [loading, setLoading] = useState(false);

  const [formData, setFormData] = useState({
    fornecedorId: '',
    numeroPedido: '',
    dataPrevisaoEntrega: '',
    observacoes: '',
    itens: [{ materialId: '', quantidadeSolicitada: 1, precoUnitario: 0 }]
  });

  useEffect(() => {
    if (open) {
      fetchFornecedores();
      fetchMateriais();
    }
  }, [open]);

  const fetchFornecedores = async () => {
    try {
      const res = await api.get('/core/fornecedores');
      setFornecedores(res.data.content || res.data);
    } catch (e) {
      console.error(e);
    }
  };

  const fetchMateriais = async () => {
    try {
      const res = await api.get('/inventory/materiais');
      setMateriais(res.data.content || res.data);
    } catch (e) {
      console.error(e);
    }
  };

  const handleAddItem = () => {
    setFormData({
      ...formData,
      itens: [...formData.itens, { materialId: '', quantidadeSolicitada: 1, precoUnitario: 0 }]
    });
  };

  const handleRemoveItem = (index: number) => {
    const newItens = formData.itens.filter((_, i) => i !== index);
    setFormData({ ...formData, itens: newItens });
  };

  const handleItemChange = (index: number, field: string, value: any) => {
    const newItens = [...formData.itens];
    newItens[index] = { ...newItens[index], [field]: value };
    setFormData({ ...formData, itens: newItens });
  };

  const handleSubmit = async () => {
    try {
      setLoading(true);
      await api.post('/procurement/ordens-compra', formData);
      onSuccess();
      onClose();
    } catch (error) {
      console.error(error);
      alert('Erro ao salvar o pedido.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <Dialog open={open} onClose={onClose} maxWidth="md" fullWidth>
      <DialogTitle>Novo Pedido de Compra</DialogTitle>
      <DialogContent dividers>
        <Grid container spacing={3}>
          <Grid size={{ xs: 12, sm: 6 }}>
            <TextField
              select
              label="Fornecedor"
              fullWidth
              value={formData.fornecedorId}
              onChange={(e) => setFormData({ ...formData, fornecedorId: e.target.value })}
            >
              {Array.isArray(fornecedores) && fornecedores.map(f => (
                <MenuItem key={f.id} value={f.id}>{f.nome || f.razaoSocial}</MenuItem>
              ))}
            </TextField>
          </Grid>
          <Grid size={{ xs: 12, sm: 6 }}>
            <TextField
              label="Número do Pedido (Opcional)"
              fullWidth
              value={formData.numeroPedido}
              onChange={(e) => setFormData({ ...formData, numeroPedido: e.target.value })}
            />
          </Grid>
          <Grid size={{ xs: 12, sm: 6 }}>
            <TextField
              label="Previsão de Entrega"
              type="date"
              fullWidth
              slotProps={{ inputLabel: { shrink: true } }}
              value={formData.dataPrevisaoEntrega}
              onChange={(e) => setFormData({ ...formData, dataPrevisaoEntrega: e.target.value })}
            />
          </Grid>
          <Grid size={{ xs: 12, sm: 6 }}>
            <TextField
              label="Observações"
              fullWidth
              value={formData.observacoes}
              onChange={(e) => setFormData({ ...formData, observacoes: e.target.value })}
            />
          </Grid>
        </Grid>

        <Box sx={{ mt: 4, mb: 2, display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
          <Typography variant="h6">Itens do Pedido</Typography>
          <Button startIcon={<Plus size={16} />} onClick={handleAddItem} variant="outlined" size="small">
            Adicionar Item
          </Button>
        </Box>

        {formData.itens.map((item, index) => (
          <Grid container spacing={2} key={index} sx={{ mb: 2, alignItems: 'center' }}>
            <Grid size={{ xs: 12, sm: 5 }}>
              <TextField
                select
                label="Material"
                fullWidth
                size="small"
                value={item.materialId}
                onChange={(e) => handleItemChange(index, 'materialId', e.target.value)}
              >
                {Array.isArray(materiais) && materiais.map(m => (
                  <MenuItem key={m.id} value={m.id}>{m.nome} ({m.codigo})</MenuItem>
                ))}
              </TextField>
            </Grid>
            <Grid size={{ xs: 6, sm: 3 }}>
              <TextField
                label="Quantidade"
                type="number"
                fullWidth
                size="small"
                value={item.quantidadeSolicitada}
                onChange={(e) => handleItemChange(index, 'quantidadeSolicitada', e.target.value)}
              />
            </Grid>
            <Grid size={{ xs: 6, sm: 3 }}>
              <TextField
                label="Preço Unitário"
                type="number"
                fullWidth
                size="small"
                value={item.precoUnitario}
                onChange={(e) => handleItemChange(index, 'precoUnitario', e.target.value)}
              />
            </Grid>
            <Grid size={{ xs: 12, sm: 1 }}>
              <IconButton color="error" onClick={() => handleRemoveItem(index)}>
                <Trash2 size={20} />
              </IconButton>
            </Grid>
          </Grid>
        ))}
      </DialogContent>
      <DialogActions>
        <Button onClick={onClose} disabled={loading}>Cancelar</Button>
        <Button onClick={handleSubmit} variant="contained" disabled={loading}>
          {loading ? 'Salvando...' : 'Salvar Pedido'}
        </Button>
      </DialogActions>
    </Dialog>
  );
}
