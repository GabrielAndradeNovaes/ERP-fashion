import React, { useState, useEffect } from 'react';
import { 
  Box, Typography, Card, Table, TableBody, TableCell, TableContainer, TableHead, TableRow, 
  Button, IconButton, Modal, TextField, Paper, Chip 
} from '@mui/material';
import { Plus, Link as LinkIcon, ExternalLink } from 'lucide-react';
import axios from 'axios';
import PageHeader from '../../components/PageHeader';

interface TituloReceber {
  id: string;
  descricao: string;
  valor: number;
  dataVencimento: string;
  status: string;
}

const TitulosReceber = () => {
  const [titulos, setTitulos] = useState<TituloReceber[]>([]);
  const [openModal, setOpenModal] = useState(false);
  const [formData, setFormData] = useState({ descricao: '', valor: 0, dataVencimento: '' });

  const fetchTitulos = async () => {
    try {
      const response = await axios.get('/api/financeiro/receber');
      setTitulos(response.data);
    } catch (error) {
      console.error('Erro ao buscar títulos', error);
    }
  };

  useEffect(() => {
    fetchTitulos();
  }, []);

  const handleCreate = async () => {
    try {
      await axios.post('/api/financeiro/receber', formData);
      setOpenModal(false);
      setFormData({ descricao: '', valor: 0, dataVencimento: '' });
      fetchTitulos();
    } catch (error) {
      console.error('Erro ao criar título', error);
    }
  };

  const handleGeneratePayment = async (id: string) => {
    try {
      const response = await axios.post(`/api/financeiro/receber/${id}/gerar-pagamento?gateway=MOCK`);
      window.open(response.data, '_blank');
    } catch (error) {
      console.error('Erro ao gerar pagamento', error);
      alert('Erro ao gerar pagamento. Verifique se já não existe um PENDING.');
    }
  };

  return (
    <Box>
      <PageHeader
        title="Títulos a Receber"
        description="Gerencie as cobranças dos seus clientes"
        action={
          <Button
            variant="contained"
            color="primary"
            startIcon={<Plus size={20} />}
            onClick={() => setOpenModal(true)}
          >
            Novo Título
          </Button>
        }
      />

      <Card sx={{ mt: 3, boxShadow: '0 4px 12px rgba(0,0,0,0.05)', borderRadius: 2 }}>
        <TableContainer component={Paper} elevation={0}>
          <Table>
            <TableHead sx={{ bgcolor: 'rgba(0,0,0,0.02)' }}>
              <TableRow>
                <TableCell>Descrição</TableCell>
                <TableCell>Valor (R$)</TableCell>
                <TableCell>Vencimento</TableCell>
                <TableCell>Status</TableCell>
                <TableCell align="center">Ações</TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {titulos.map((titulo) => (
                <TableRow key={titulo.id}>
                  <TableCell>{titulo.descricao}</TableCell>
                  <TableCell>{titulo.valor.toFixed(2)}</TableCell>
                  <TableCell>{titulo.dataVencimento}</TableCell>
                  <TableCell>
                    <Chip 
                      label={titulo.status} 
                      color={titulo.status === 'PAID' ? 'success' : titulo.status === 'PENDING' ? 'warning' : 'default'}
                      size="small"
                      sx={{ fontWeight: 'bold' }}
                    />
                  </TableCell>
                  <TableCell align="center">
                    {titulo.status === 'PENDING' && (
                      <Button
                        size="small"
                        variant="outlined"
                        startIcon={<LinkIcon size={16} />}
                        onClick={() => handleGeneratePayment(titulo.id)}
                        sx={{ mr: 1 }}
                      >
                        Cobrar
                      </Button>
                    )}
                    <IconButton size="small" onClick={() => window.open(`/pagamento/${titulo.id}`, '_blank')}>
                      <ExternalLink size={18} />
                    </IconButton>
                  </TableCell>
                </TableRow>
              ))}
              {titulos.length === 0 && (
                <TableRow>
                  <TableCell colSpan={5} align="center" sx={{ py: 3, color: 'text.secondary' }}>
                    Nenhum título a receber encontrado.
                  </TableCell>
                </TableRow>
              )}
            </TableBody>
          </Table>
        </TableContainer>
      </Card>

      <Modal open={openModal} onClose={() => setOpenModal(false)}>
        <Box sx={{
          position: 'absolute', top: '50%', left: '50%', transform: 'translate(-50%, -50%)',
          width: 400, bgcolor: 'background.paper', borderRadius: 2, boxShadow: 24, p: 4
        }}>
          <Typography variant="h6" mb={2}>Novo Título a Receber</Typography>
          <TextField
            fullWidth label="Descrição" variant="outlined" margin="normal"
            value={formData.descricao} onChange={(e) => setFormData({ ...formData, descricao: e.target.value })}
          />
          <TextField
            fullWidth label="Valor (R$)" type="number" variant="outlined" margin="normal"
            value={formData.valor} onChange={(e) => setFormData({ ...formData, valor: parseFloat(e.target.value) })}
          />
          <TextField
            fullWidth label="Data Vencimento" type="date" InputLabelProps={{ shrink: true }} variant="outlined" margin="normal"
            value={formData.dataVencimento} onChange={(e) => setFormData({ ...formData, dataVencimento: e.target.value })}
          />
          <Box sx={{ mt: 3, display: 'flex', justifyContent: 'flex-end', gap: 2 }}>
            <Button onClick={() => setOpenModal(false)}>Cancelar</Button>
            <Button variant="contained" onClick={handleCreate}>Salvar</Button>
          </Box>
        </Box>
      </Modal>
    </Box>
  );
};

export default TitulosReceber;
