import React, { useState, useEffect } from 'react';
import { Box, Typography, Paper, Table, TableBody, TableCell, TableContainer, TableHead, TableRow, Button, Chip, Dialog, DialogTitle, DialogContent, DialogActions, TextField, MenuItem, Select, FormControl, InputLabel } from '@mui/material';
import api from '../../api/axios';
import { useAuth } from '../../contexts/AuthContext';
import { CheckCircle, Clock } from 'lucide-react';
import { useToast } from '../../contexts/ToastContext';

interface TituloPagar {
  id: string;
  descricao: string;
  valor: number;
  dataEmissao: string;
  dataVencimento: string;
  dataPagamento: string | null;
  status: 'PENDENTE' | 'PAGO' | 'CANCELADO';
  funcionario?: {
    nome: string;
  };
}

const ContasPagar: React.FC = () => {
  const [titulos, setTitulos] = useState<TituloPagar[]>([]);
  const [funcionarios, setFuncionarios] = useState<any[]>([]);
  const [openModal, setOpenModal] = useState(false);
  const [formData, setFormData] = useState({ descricao: '', valor: '', dataEmissao: '', dataVencimento: '', funcionarioId: '' });
  const { hasPermission } = useAuth();
  const canEdit = hasPermission('USUARIOS_ADMIN'); // TODO: Create specific finance permission
  const { showToast } = useToast();

  const carregarTitulos = async () => {
    try {
      const res = await api.get('/financeiro/titulos');
      setTitulos(res.data);
    } catch (err) {
      console.error(err);
    }
  };

  const carregarFuncionarios = async () => {
    try {
      const res = await api.get('/funcionarios');
      setFuncionarios(res.data);
    } catch (err) {
      console.error("Erro ao carregar funcionários", err);
    }
  };

  useEffect(() => {
    carregarTitulos();
    carregarFuncionarios();
  }, []);

  const handleBaixar = async (id: string) => {
    if (!window.confirm("Confirmar o pagamento deste título?")) return;
    try {
      await api.post(`/financeiro/titulos/${id}/baixar`);
      carregarTitulos();
    } catch (err) {
      console.error(err);
      showToast('Erro ao baixar título', 'error');
    }
  };

  const handleCreate = async () => {
    try {
      await api.post('/financeiro/titulos', {
        ...formData,
        valor: parseFloat(formData.valor),
        funcionarioId: formData.funcionarioId ? formData.funcionarioId : null,
        dataEmissao: formData.dataEmissao ? formData.dataEmissao : undefined,
        dataVencimento: formData.dataVencimento ? formData.dataVencimento : undefined,
      });
      setOpenModal(false);
      setFormData({ descricao: '', valor: '', dataEmissao: '', dataVencimento: '', funcionarioId: '' });
      carregarTitulos();
    } catch (err) {
      console.error(err);
      showToast('Erro ao criar conta', 'error');
    }
  };

  const formatCurrency = (val: number) => {
    return new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' }).format(val);
  };

  const formatDate = (dateStr: string | null) => {
    if (!dateStr) return '-';
    return new Date(dateStr).toLocaleDateString('pt-BR');
  };

  return (
    <Box sx={{ p: 4, height: '100%' }}>
      <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 4 }}>
        <Typography variant="h4" sx={{ fontWeight: 'bold' }}>Contas a Pagar</Typography>
        {canEdit && (
          <Button variant="contained" color="primary" onClick={() => setOpenModal(true)}>
            Nova Conta
          </Button>
        )}
      </Box>
      
      <TableContainer component={Paper} className="premium-card">
        <Table>
          <TableHead>
            <TableRow>
              <TableCell>Descrição</TableCell>
              <TableCell>Favorecido</TableCell>
              <TableCell>Emissão</TableCell>
              <TableCell>Vencimento</TableCell>
              <TableCell>Pagamento</TableCell>
              <TableCell>Valor</TableCell>
              <TableCell>Status</TableCell>
              <TableCell align="right">Ações</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {titulos.length === 0 ? (
              <TableRow>
                <TableCell colSpan={8} align="center">Nenhum título encontrado.</TableCell>
              </TableRow>
            ) : titulos.map(t => (
              <TableRow key={t.id} hover>
                <TableCell>{t.descricao}</TableCell>
                <TableCell>{t.funcionario?.nome || '-'}</TableCell>
                <TableCell>{formatDate(t.dataEmissao)}</TableCell>
                <TableCell>{formatDate(t.dataVencimento)}</TableCell>
                <TableCell>{formatDate(t.dataPagamento)}</TableCell>
                <TableCell sx={{ fontWeight: 600, color: 'var(--danger)' }}>{formatCurrency(t.valor)}</TableCell>
                <TableCell>
                  {t.status === 'PAGO' ? (
                    <Chip label="Pago" size="small" color="success" icon={<CheckCircle size={16}/>} />
                  ) : (
                    <Chip label="Pendente" size="small" color="warning" icon={<Clock size={16}/>} />
                  )}
                </TableCell>
                <TableCell align="right">
                  {t.status === 'PENDENTE' && canEdit && (
                    <Button 
                      variant="outlined" 
                      color="success" 
                      size="small"
                      onClick={() => handleBaixar(t.id)}
                    >
                      Dar Baixa
                    </Button>
                  )}
                </TableCell>
              </TableRow>
            ))}
          </TableBody>
        </Table>
      </TableContainer>

      <Dialog open={openModal} onClose={() => setOpenModal(false)} maxWidth="sm" fullWidth>
        <DialogTitle>Nova Conta a Pagar</DialogTitle>
        <DialogContent sx={{ display: 'flex', flexDirection: 'column', gap: 2, mt: 1 }}>
          <TextField
            label="Descrição"
            value={formData.descricao}
            onChange={(e) => setFormData({ ...formData, descricao: e.target.value })}
            fullWidth
          />
          
          <FormControl fullWidth>
            <InputLabel>Favorecido (Funcionário)</InputLabel>
            <Select
              value={formData.funcionarioId}
              label="Favorecido (Funcionário)"
              onChange={(e) => setFormData({ ...formData, funcionarioId: e.target.value })}
            >
              <MenuItem value=""><em>Nenhum / Externo</em></MenuItem>
              {funcionarios.map(f => (
                <MenuItem key={f.id} value={f.id}>{f.nome}</MenuItem>
              ))}
            </Select>
          </FormControl>

          <TextField
            label="Valor"
            type="number"
            value={formData.valor}
            onChange={(e) => setFormData({ ...formData, valor: e.target.value })}
            fullWidth
          />
          <TextField
            label="Data Emissão"
            type="date"
            slotProps={{ inputLabel: { shrink: true } }}
            value={formData.dataEmissao}
            onChange={(e) => setFormData({ ...formData, dataEmissao: e.target.value })}
            fullWidth
          />
          <TextField
            label="Data Vencimento"
            type="date"
            slotProps={{ inputLabel: { shrink: true } }}
            value={formData.dataVencimento}
            onChange={(e) => setFormData({ ...formData, dataVencimento: e.target.value })}
            fullWidth
          />
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setOpenModal(false)}>Cancelar</Button>
          <Button variant="contained" onClick={handleCreate} disabled={!formData.descricao || !formData.valor}>Salvar</Button>
        </DialogActions>
      </Dialog>
    </Box>
  );
};

export default ContasPagar;
