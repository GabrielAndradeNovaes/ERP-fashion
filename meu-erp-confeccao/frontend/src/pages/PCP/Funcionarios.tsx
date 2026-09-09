import React, { useState, useEffect } from 'react';
import { 
  Box, Typography, Paper, Table, TableBody, TableCell, TableContainer, TableHead, TableRow,
  Button, IconButton, Dialog, DialogTitle, DialogContent, DialogActions, TextField,
  Grid, Switch, FormControlLabel, Autocomplete, MenuItem, Select, InputLabel, FormControl
} from '@mui/material';
import api from '../../api/axios';
import { useAuth } from '../../contexts/AuthContext';
import { Edit2, Trash2, Plus } from 'lucide-react';

interface Jornada {
  id?: string;
  diaSemana: number;
  entrada: string;
  saida: string;
}

interface GrupoFuncionario {
  id: string;
  nome: string;
}

interface Funcionario {
  id: string;
  nome: string;
  matricula: string;
  cargaHorariaDiariaPadrao: number;
  cargaHorariaMensalPadrao: number;
  ativo: boolean;
  metaMinima: number;
  premio100: number;
  grupo?: GrupoFuncionario;
  jornadas: Jornada[];
}

const Funcionarios: React.FC = () => {
  const [funcionarios, setFuncionarios] = useState<Funcionario[]>([]);
  const [grupos, setGrupos] = useState<GrupoFuncionario[]>([]);
  const [open, setOpen] = useState(false);
  const [editingFuncionario, setEditingFuncionario] = useState<Partial<Funcionario>>({
    ativo: true,
    cargaHorariaDiariaPadrao: 8.8,
    cargaHorariaMensalPadrao: 220,
    metaMinima: 75,
    premio100: 1000,
    jornadas: []
  });
  
  const [diasSelecionados, setDiasSelecionados] = useState<number[]>([]);
  const [horarioEntrada, setHorarioEntrada] = useState<string>('07:00');
  const [horarioSaida, setHorarioSaida] = useState<string>('17:00');

  const { hasPermission } = useAuth();
  const canEdit = hasPermission('PCP_EDIT');

  const carregarDados = () => {
    api.get('/funcionarios').then(res => setFuncionarios(res.data)).catch(console.error);
    api.get('/grupos-funcionarios').then(res => setGrupos(res.data)).catch(console.error);
  };

  useEffect(() => {
    carregarDados();
  }, []);

  const handleSalvar = () => {
    if (!editingFuncionario.nome || !editingFuncionario.matricula) {
      alert("Nome e Matrícula são obrigatórios!");
      return;
    }
    
    // Convert string inputs to proper payload
    let payload = { ...editingFuncionario };
    if (typeof payload.grupo === 'string') {
      payload.grupo = { nome: payload.grupo } as any;
    }
    
    if (editingFuncionario.id) {
      api.put(`/funcionarios/${editingFuncionario.id}`, payload)
        .then(() => { setOpen(false); carregarDados(); })
        .catch(console.error);
    } else {
      api.post('/funcionarios', payload)
        .then(() => { setOpen(false); carregarDados(); })
        .catch(console.error);
    }
  };

  const handleExcluir = (id: string) => {
    if (window.confirm("Deseja realmente excluir?")) {
      api.delete(`/funcionarios/${id}`)
        .then(() => carregarDados())
        .catch(console.error);
    }
  };

  const handleCopiarJornada = (id: string) => {
    if (window.confirm("Isso irá sobrescrever a jornada de TODOS os funcionários deste mesmo grupo. Confirma?")) {
      api.post(`/funcionarios/${id}/copiar-jornada`)
        .then(() => { alert("Jornadas copiadas com sucesso!"); carregarDados(); })
        .catch(console.error);
    }
  };

  const addJornada = () => {
    if (diasSelecionados.length > 0 && horarioEntrada && horarioSaida) {
      const novas = diasSelecionados.map(d => ({
        diaSemana: d,
        entrada: horarioEntrada,
        saida: horarioSaida
      }));
      
      const jornadasAtuais = editingFuncionario.jornadas || [];

      setEditingFuncionario({
        ...editingFuncionario,
        jornadas: [...jornadasAtuais, ...novas]
      });
      setDiasSelecionados([]);
    }
  };
  
  const removeJornada = (index: number) => {
    const list = [...(editingFuncionario.jornadas || [])];
    list.splice(index, 1);
    setEditingFuncionario({ ...editingFuncionario, jornadas: list });
  };

  const diasSemana = ['Segunda', 'Terça', 'Quarta', 'Quinta', 'Sexta', 'Sábado', 'Domingo'];

  return (
    <Box sx={{ p: 4, height: '100%' }}>
      <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 4 }}>
        <Typography variant="h4" sx={{ fontWeight: 'bold' }}>Funcionários (PCP)</Typography>
        {canEdit && (
          <Button 
            variant="contained" 
            startIcon={<Plus size={20} />}
            onClick={() => {
              setEditingFuncionario({ ativo: true, cargaHorariaDiariaPadrao: 8.8, cargaHorariaMensalPadrao: 220, metaMinima: 75, premio100: 1000, jornadas: [] });
              setOpen(true);
            }}
            sx={{ background: 'var(--accent-gradient)' }}
          >
            Novo Funcionário
          </Button>
        )}
      </Box>

      <TableContainer component={Paper} className="premium-card">
        <Table>
          <TableHead>
            <TableRow>
              <TableCell>Nome</TableCell>
              <TableCell>Grupo</TableCell>
              <TableCell>Matrícula</TableCell>
              <TableCell>Status</TableCell>
              {canEdit && <TableCell align="right">Ações</TableCell>}
            </TableRow>
          </TableHead>
          <TableBody>
            {funcionarios.map(f => (
              <TableRow key={f.id} hover>
                <TableCell>{f.nome}</TableCell>
                <TableCell>{f.grupo?.nome || '-'}</TableCell>
                <TableCell>{f.matricula}</TableCell>
                <TableCell>{f.ativo ? 'Ativo' : 'Inativo'}</TableCell>
                {canEdit && (
                  <TableCell align="right">
                    {f.grupo && f.jornadas?.length > 0 && (
                       <Button size="small" onClick={() => handleCopiarJornada(f.id)} sx={{ mr: 1 }}>Copiar Horários</Button>
                    )}
                    <IconButton onClick={() => { setEditingFuncionario(f); setOpen(true); }} size="small" color="primary">
                      <Edit2 size={18} />
                    </IconButton>
                    <IconButton onClick={() => handleExcluir(f.id)} size="small" color="error">
                      <Trash2 size={18} />
                    </IconButton>
                  </TableCell>
                )}
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
            <Box sx={{ gridColumn: 'span 4' }}>
              <TextField 
                fullWidth label="Meta Mínima (%)" type="number"
                value={editingFuncionario.metaMinima || ''} 
                onChange={e => setEditingFuncionario({...editingFuncionario, metaMinima: Number(e.target.value)})} 
              />
            </Box>
            <Box sx={{ gridColumn: 'span 4' }}>
              <TextField 
                fullWidth label="Prêmio 100% (R$)" type="number"
                value={editingFuncionario.premio100 || ''} 
                onChange={e => setEditingFuncionario({...editingFuncionario, premio100: Number(e.target.value)})} 
              />
            </Box>
            <Box sx={{ gridColumn: 'span 4' }}>
              <Autocomplete
                freeSolo
                options={grupos.map(g => g.nome)}
                value={typeof editingFuncionario.grupo === 'object' ? editingFuncionario.grupo?.nome : editingFuncionario.grupo || ''}
                onChange={(e, newValue) => setEditingFuncionario({...editingFuncionario, grupo: newValue as any})}
                onInputChange={(e, newInputValue) => setEditingFuncionario({...editingFuncionario, grupo: newInputValue as any})}
                renderInput={(params) => <TextField {...params} label="Grupo / Setor" />}
              />
            </Box>
            <Box sx={{ gridColumn: 'span 4' }}>
              <FormControlLabel 
                control={<Switch checked={editingFuncionario.ativo} onChange={e => setEditingFuncionario({...editingFuncionario, ativo: e.target.checked})} />} 
                label="Ativo" 
              />
            </Box>

            <Box sx={{ gridColumn: 'span 12', mt: 2 }}>
              <Typography variant="h6" sx={{ mb: 2, fontWeight: 'bold' }}>Grade de Horários</Typography>
              <Box sx={{ display: 'flex', gap: 2, alignItems: 'center', mb: 2 }}>
                <FormControl sx={{ minWidth: 200, flex: 1 }}>
                  <InputLabel>Dias da Semana</InputLabel>
                  <Select 
                    multiple
                    value={diasSelecionados} 
                    label="Dias da Semana"
                    onChange={e => {
                      const val = e.target.value;
                      setDiasSelecionados(typeof val === 'string' ? val.split(',').map(Number) : val as number[]);
                    }}
                    renderValue={(selected) => selected.map(val => diasSemana[val - 1]).join(', ')}
                  >
                    {diasSemana.map((d, i) => <MenuItem key={i+1} value={i+1}>{d}</MenuItem>)}
                  </Select>
                </FormControl>
                <TextField type="time" label="Entrada" value={horarioEntrada} onChange={e => setHorarioEntrada(e.target.value)} slotProps={{ inputLabel: { shrink: true } }} />
                <TextField type="time" label="Saída" value={horarioSaida} onChange={e => setHorarioSaida(e.target.value)} slotProps={{ inputLabel: { shrink: true } }} />
                <Button variant="contained" onClick={addJornada}>Adicionar</Button>
              </Box>

              <Table size="small">
                <TableHead>
                  <TableRow>
                    <TableCell>Dia</TableCell>
                    <TableCell>Entrada</TableCell>
                    <TableCell>Saída</TableCell>
                    <TableCell width={50}></TableCell>
                  </TableRow>
                </TableHead>
                <TableBody>
                  {(editingFuncionario.jornadas || []).sort((a,b) => a.diaSemana === b.diaSemana ? a.entrada.localeCompare(b.entrada) : a.diaSemana - b.diaSemana).map((j, i) => (
                    <TableRow key={i}>
                      <TableCell>{diasSemana[j.diaSemana - 1]}</TableCell>
                      <TableCell>{j.entrada}</TableCell>
                      <TableCell>{j.saida}</TableCell>
                      <TableCell>
                        <IconButton size="small" color="error" onClick={() => removeJornada(i)}>
                          <Trash2 size={16} />
                        </IconButton>
                      </TableCell>
                    </TableRow>
                  ))}
                  {(!editingFuncionario.jornadas || editingFuncionario.jornadas.length === 0) && (
                    <TableRow><TableCell colSpan={4} align="center">Nenhum horário configurado.</TableCell></TableRow>
                  )}
                </TableBody>
              </Table>
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
