import React, { useState, useEffect } from 'react';
import { Box, Typography, Paper, Table, TableBody, TableCell, TableContainer, TableHead, TableRow, Button, Chip } from '@mui/material';
import api from '../../api/axios';
import { useAuth } from '../../contexts/AuthContext';
import { CheckCircle, Clock } from 'lucide-react';

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
  const { hasPermission } = useAuth();
  const canEdit = hasPermission('USUARIOS_ADMIN'); // TODO: Create specific finance permission

  const carregarTitulos = async () => {
    try {
      const res = await api.get('/financeiro/titulos');
      setTitulos(res.data);
    } catch (err) {
      console.error(err);
    }
  };

  useEffect(() => {
    carregarTitulos();
  }, []);

  const handleBaixar = async (id: string) => {
    if (!window.confirm("Confirmar o pagamento deste título?")) return;
    try {
      await api.post(`/financeiro/titulos/${id}/baixar`);
      carregarTitulos();
    } catch (err) {
      console.error(err);
      alert('Erro ao baixar título');
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
      <Typography variant="h4" sx={{ fontWeight: 'bold', mb: 4 }}>Contas a Pagar</Typography>
      
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
    </Box>
  );
};

export default ContasPagar;
