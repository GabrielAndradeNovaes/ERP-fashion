import React, { useState, useRef, useEffect } from 'react';
import { 
  Box, 
  Typography, 
  TextField, 
  Select, 
  MenuItem, 
  FormControl, 
  InputLabel, 
  Paper,
  Snackbar,
  Alert,
  List,
  ListItem,
  ListItemText,
  ListItemIcon,
  Divider
} from '@mui/material';
import CheckCircleIcon from '@mui/icons-material/CheckCircle';
import ErrorIcon from '@mui/icons-material/Error';
import api from '../../api/axios';

interface Funcionario {
  id: string;
  nome: string;
  matricula: string;
}

interface BipagemLog {
  id: number;
  codigoBarras: string;
  status: 'success' | 'error';
  mensagem: string;
  timestamp: Date;
}

const Bipagem: React.FC = () => {
  const [funcionarios, setFuncionarios] = useState<Funcionario[]>([]);
  const [selectedFuncionario, setSelectedFuncionario] = useState<string>('');
  const [codigoBarras, setCodigoBarras] = useState<string>('');
  const [logs, setLogs] = useState<BipagemLog[]>([]);
  
  const [snackbar, setSnackbar] = useState<{ open: boolean; message: string; severity: 'success' | 'error' }>({
    open: false,
    message: '',
    severity: 'success'
  });

  const inputRef = useRef<HTMLInputElement>(null);

  useEffect(() => {
    // Fetch Funcionarios on load
    api.get('/funcionarios')
      .then(res => setFuncionarios(res.data))
      .catch(err => console.error("Erro ao carregar funcionários", err));
  }, []);

  useEffect(() => {
    // Keep focus on the input as much as possible if a user is selected
    if (selectedFuncionario && inputRef.current) {
      inputRef.current.focus();
    }
  }, [selectedFuncionario, snackbar]); // Re-focus after showing snackbar too

  const handleKeyDown = async (e: React.KeyboardEvent) => {
    if (e.key === 'Enter') {
      e.preventDefault();
      
      if (!selectedFuncionario) {
        showFeedback('Selecione um funcionário primeiro.', 'error', codigoBarras);
        return;
      }

      if (!codigoBarras.trim()) return;

      const currentCode = codigoBarras.trim();
      setCodigoBarras(''); // Clear input immediately for the next scan

      try {
        await api.post('/pcp/bipagem', {
          codigoBarras: currentCode,
          funcionarioId: selectedFuncionario
        });
        showFeedback(`Cupom ${currentCode} processado com sucesso!`, 'success', currentCode);
      } catch (err: any) {
        const errorMsg = err.response?.data?.message || err.message || 'Erro desconhecido ao processar cupom.';
        showFeedback(errorMsg, 'error', currentCode);
      }
    }
  };

  const showFeedback = (message: string, severity: 'success' | 'error', code: string) => {
    setSnackbar({ open: true, message, severity });
    setLogs(prev => [
      { id: Date.now(), codigoBarras: code, status: severity, mensagem: message, timestamp: new Date() },
      ...prev
    ].slice(0, 5)); // Keep only last 5 logs
  };

  return (
    <Box sx={{ p: 4, height: '100%', display: 'flex', flexDirection: 'column', alignItems: 'center' }}>
      <Typography variant="h4" gutterBottom sx={{ fontWeight: 'bold', color: 'primary.main' }}>
        Bipagem Rápida (PCP)
      </Typography>
      
      <Paper elevation={6} sx={{ p: 4, width: '100%', maxWidth: 800, borderRadius: 3, mb: 4, backgroundColor: 'background.paper' }}>
        
        <FormControl fullWidth sx={{ mb: 4 }}>
          <InputLabel id="funcionario-label">Operador / Costureira</InputLabel>
          <Select
            labelId="funcionario-label"
            value={selectedFuncionario}
            label="Operador / Costureira"
            onChange={(e) => setSelectedFuncionario(e.target.value)}
          >
            {funcionarios.map(f => (
              <MenuItem key={f.id} value={f.id}>{f.nome} (Mat: {f.matricula})</MenuItem>
            ))}
          </Select>
        </FormControl>

        <TextField
          fullWidth
          label="Código de Barras (Bipe Aqui)"
          variant="outlined"
          value={codigoBarras}
          onChange={(e) => setCodigoBarras(e.target.value)}
          onKeyDown={handleKeyDown}
          inputRef={inputRef}
          disabled={!selectedFuncionario}
          sx={{ 
            '& .MuiInputBase-input': { fontSize: '1.5rem', py: 2, textAlign: 'center' },
            '& .MuiInputLabel-root': { fontSize: '1.2rem' }
          }}
          placeholder="O leitor de código de barras irá preencher este campo automaticamente"
        />
        
        <Typography variant="body2" color="textSecondary" sx={{ mt: 2, textAlign: 'center' }}>
          * Selecione um funcionário. O campo ficará em foco. Bipe o código usando o scanner. O sistema irá registrar e limpar o campo instantaneamente.
        </Typography>

      </Paper>

      {/* Histórico Recente */}
      <Paper elevation={2} sx={{ width: '100%', maxWidth: 800, p: 2, borderRadius: 2 }}>
        <Typography variant="h6" gutterBottom>Últimos Registros da Sessão</Typography>
        {logs.length === 0 ? (
          <Typography variant="body2" color="textSecondary">Nenhum cupom bipado ainda.</Typography>
        ) : (
          <List>
            {logs.map((log, index) => (
              <React.Fragment key={log.id}>
                <ListItem>
                  <ListItemIcon>
                    {log.status === 'success' ? <CheckCircleIcon color="success" fontSize="large" /> : <ErrorIcon color="error" fontSize="large" />}
                  </ListItemIcon>
                  <ListItemText 
                    primary={log.codigoBarras} 
                    secondary={
                      <React.Fragment>
                        <Typography component="span" variant="body2" color="text.primary">
                          {log.mensagem}
                        </Typography>
                        {` — ${log.timestamp.toLocaleTimeString()}`}
                      </React.Fragment>
                    }
                  />
                </ListItem>
                {index < logs.length - 1 && <Divider />}
              </React.Fragment>
            ))}
          </List>
        )}
      </Paper>

      {/* Feedback Instantâneo Gigante */}
      <Snackbar 
        open={snackbar.open} 
        autoHideDuration={2000} 
        onClose={() => setSnackbar({ ...snackbar, open: false })}
        anchorOrigin={{ vertical: 'top', horizontal: 'center' }}
        sx={{ mt: 10 }} // Push down from top edge
      >
        <Alert 
          onClose={() => setSnackbar({ ...snackbar, open: false })} 
          severity={snackbar.severity} 
          variant="filled"
          sx={{ width: '100%', fontSize: '1.5rem', py: 2, px: 4, borderRadius: 2, alignItems: 'center' }}
        >
          {snackbar.message}
        </Alert>
      </Snackbar>

    </Box>
  );
};

export default Bipagem;
