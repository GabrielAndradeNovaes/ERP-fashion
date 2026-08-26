import React, { useState, useEffect } from 'react';
import { 
  Box, Typography, Paper, Table, TableBody, TableCell, TableContainer, TableHead, TableRow,
  Button, IconButton, Dialog, DialogTitle, DialogContent, DialogActions, TextField,
  Grid, Switch, FormControlLabel
} from '@mui/material';
import api from '../../api/axios';
import { Edit2, Trash2, Plus } from 'lucide-react';

interface Funcionario {
  id: string;
  nome: string;
  matricula: string;
  cargaHorariaDiariaPadrao: number;
  cargaHorariaMensalPadrao: number;
  ativo: boolean;
}

const Funcionarios: React.FC = () => {
  const [funcionarios, setFuncionarios] = useState<Funcionario[]>([]);
  const [open, setOpen] = useState(false);
  const [editingFuncionario, setEditingFuncionario] = useState<Partial<Funcionario>>({
    ativo: true,
    cargaHorariaDiariaPadrao: 8.8,
    cargaHorariaMensalPadrao: 220
  });

  const carregarFuncionarios = () => {
    api.get('/funcionarios').then(res => setFuncionarios(res.data)).catch(console.error);
  };

  useEffect(() => {
    carregarFuncionarios();
  }, []);

  const handleSalvar = () => {
    if (editingFuncionario.id) {
      api.put(`/funcionarios/${editingFuncionario.id}`, editingFuncionario)
        .then(() => { setOpen(false); carregarFuncionarios(); })
        .catch(console.error);
    } else {
      api.post('/funcionarios', editingFuncionario)
        .then(() => { setOpen(false); carregarFuncionarios(); })
        .catch(console.error);
    }
  };

  const handleExcluir = (id: string) => {
    if (window.confirm("Deseja realmente excluir?")) {
      api.delete(`/funcionarios/${id}`)
        .then(() => carregarFuncionarios())
        .catch(console.error);
    }
  };

  return (
    <Box sx={{ p: 4, height: '100%' }}>
      <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 4 }}>
        <Typography variant="h4" sx={{ fontWeight: 'bold' }}>Funcionários (PCP)</Typography>
        <Button 
          variant="contained" 
          startIcon={<Plus size={20} />}
          onClick={() => {
            setEditingFuncionario({ ativo: true, cargaHorariaDiariaPadrao: 8.8, cargaHorariaMensalPadrao: 220 });
            setOpen(true);
          }}
          sx={{ background: 'var(--accent-gradient)' }}
        >
          Novo Funcionário
        </Button>
      </Box>

      <TableContainer component={Paper} className="premium-card">
        <Table>
          <TableHead>
            <TableRow>
              <TableCell>Nome</TableCell>
              <TableCell>Matrícula</TableCell>
              <TableCell>Carga Horária (Diária)</TableCell>
              <TableCell>Carga Horária (Mensal)</TableCell>
              <TableCell>Status</TableCell>
              <TableCell align="right">Ações</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {funcionarios.map(f => (
              <TableRow key={f.id} hover>
                <TableCell>{f.nome}</TableCell>
                <TableCell>{f.matricula}</TableCell>
                <TableCell>{f.cargaHorariaDiariaPadrao} h</TableCell>
                <TableCell>{f.cargaHorariaMensalPadrao} h</TableCell>
                <TableCell>{f.ativo ? 'Ativo' : 'Inativo'}</TableCell>
                <TableCell align="right">
                  <IconButton onClick={() => { setEditingFuncionario(f); setOpen(true); }} size="small" color="primary">
                    <Edit2 size={18} />
                  </IconButton>
                  <IconButton onClick={() => handleExcluir(f.id)} size="small" color="error">
                    <Trash2 size={18} />
                  </IconButton>
                </TableCell>
              </TableRow>
            ))}
            {funcionarios.length === 0 && (
              <TableRow>
                <TableCell colSpan={6} align="center">Nenhum funcionário cadastrado.</TableCell>
              </TableRow>
            )}
          </TableBody>
        </Table>
      </TableContainer>

      <Dialog open={open} onClose={() => setOpen(false)} maxWidth="sm" fullWidth>
        <DialogTitle>{editingFuncionario.id ? 'Editar Funcionário' : 'Novo Funcionário'}</DialogTitle>
        <DialogContent>
          <Box sx={{ display: 'grid', gridTemplateColumns: 'repeat(12, 1fr)', gap: 3, mt: 1 }}>
            <Box sx={{ gridColumn: 'span 12' }}>
              <TextField 
                fullWidth label="Nome" 
                value={editingFuncionario.nome || ''} 
                onChange={e => setEditingFuncionario({...editingFuncionario, nome: e.target.value})} 
              />
            </Box>
            <Box sx={{ gridColumn: 'span 12' }}>
              <TextField 
                fullWidth label="Matrícula" 
                value={editingFuncionario.matricula || ''} 
                onChange={e => setEditingFuncionario({...editingFuncionario, matricula: e.target.value})} 
              />
            </Box>
            <Box sx={{ gridColumn: 'span 6' }}>
              <TextField 
                fullWidth label="Carga Horária Diária" type="number"
                value={editingFuncionario.cargaHorariaDiariaPadrao || ''} 
                onChange={e => setEditingFuncionario({...editingFuncionario, cargaHorariaDiariaPadrao: Number(e.target.value)})} 
              />
            </Box>
            <Box sx={{ gridColumn: 'span 6' }}>
              <TextField 
                fullWidth label="Carga Horária Mensal" type="number"
                value={editingFuncionario.cargaHorariaMensalPadrao || ''} 
                onChange={e => setEditingFuncionario({...editingFuncionario, cargaHorariaMensalPadrao: Number(e.target.value)})} 
              />
            </Box>
            <Box sx={{ gridColumn: 'span 12' }}>
              <FormControlLabel 
                control={<Switch checked={editingFuncionario.ativo} onChange={e => setEditingFuncionario({...editingFuncionario, ativo: e.target.checked})} />} 
                label="Ativo" 
              />
            </Box>
          </Box>
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setOpen(false)}>Cancelar</Button>
          <Button onClick={handleSalvar} variant="contained" color="primary">Salvar</Button>
        </DialogActions>
      </Dialog>
    </Box>
  );
};

export default Funcionarios;
