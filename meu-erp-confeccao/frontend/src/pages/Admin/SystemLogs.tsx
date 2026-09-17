import React, { useEffect, useState, useMemo } from 'react';
import { 
  Box, Typography, Paper, Table, TableBody, TableCell, 
  TableContainer, TableHead, TableRow, Chip, TextField, 
  MenuItem, Select, InputLabel, FormControl, IconButton,
  CircularProgress
} from '@mui/material';
import { RefreshCcw, Terminal } from 'lucide-react';
import api from '../../api/axios';
import { useAuth } from '../../contexts/AuthContext';

interface LogEntry {
  '@timestamp': string;
  level: string;
  thread_name: string;
  logger_name: string;
  message: string;
  stack_trace?: string;
}

export default function SystemLogs() {
  const { user } = useAuth();
  const [logs, setLogs] = useState<LogEntry[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [levelFilter, setLevelFilter] = useState('ALL');
  const [search, setSearch] = useState('');

  const fetchLogs = async () => {
    setLoading(true);
    setError('');
    try {
      // Pedimos 500 linhas para ter histórico
      const response = await api.get('/admin/logs?lines=500');
      
      const parsedLogs: LogEntry[] = [];
      response.data.forEach((line: string) => {
        try {
          const parsed = JSON.parse(line);
          parsedLogs.push(parsed);
        } catch (e) {
          console.error(e);
          // Linha não-JSON (pode ser log comum)
        }
      });
      setLogs(parsedLogs);
    } catch (err: any) {
      console.error(err);
      setError('Erro ao carregar logs. Verifique suas permissões (Super Admin).');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    if (user?.role === 'SUPERADMIN' || user?.role === 'ADMIN') {
      fetchLogs();
    } else {
      setError('Acesso Negado: Apenas administradores podem visualizar os logs do sistema.');
    }
  }, [user]);

  const filteredLogs = useMemo(() => {
    return logs.filter((log) => {
      const matchLevel = levelFilter === 'ALL' || log.level === levelFilter;
      const matchSearch = log.message?.toLowerCase().includes(search.toLowerCase()) || 
                          log.logger_name?.toLowerCase().includes(search.toLowerCase());
      return matchLevel && matchSearch;
    });
  }, [logs, levelFilter, search]);

  const getLevelColor = (level: string) => {
    switch (level) {
      case 'ERROR': return 'error';
      case 'WARN': return 'warning';
      case 'INFO': return 'info';
      case 'DEBUG': return 'default';
      default: return 'default';
    }
  };

  if (error) {
    return (
      <Box sx={{ p: 3 }}>
        <Typography variant="h4" color="error" gutterBottom>
          Logs do Sistema
        </Typography>
        <Typography>{error}</Typography>
      </Box>
    );
  }

  return (
    <Box sx={{ p: 3 }}>
      <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 3 }}>
        <Typography variant="h4" sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
          <Terminal size={32} />
          Logs do Sistema
        </Typography>
        
        <Box sx={{ display: 'flex', gap: 2, alignItems: 'center' }}>
          <TextField
            size="small"
            label="Buscar na mensagem"
            value={search}
            onChange={(e) => setSearch(e.target.value)}
          />
          <FormControl size="small" sx={{ minWidth: 120 }}>
            <InputLabel>Nível</InputLabel>
            <Select
              value={levelFilter}
              label="Nível"
              onChange={(e) => setLevelFilter(e.target.value)}
            >
              <MenuItem value="ALL">TODOS</MenuItem>
              <MenuItem value="ERROR">ERROR</MenuItem>
              <MenuItem value="WARN">WARN</MenuItem>
              <MenuItem value="INFO">INFO</MenuItem>
              <MenuItem value="DEBUG">DEBUG</MenuItem>
            </Select>
          </FormControl>
          <IconButton onClick={fetchLogs} color="primary" disabled={loading}>
            {loading ? <CircularProgress size={24} /> : <RefreshCcw />}
          </IconButton>
        </Box>
      </Box>

      <TableContainer component={Paper} sx={{ maxHeight: '75vh', overflow: 'auto', bgcolor: '#1e1e1e' }}>
        <Table stickyHeader size="small" sx={{ '& .MuiTableCell-root': { color: '#d4d4d4', borderColor: '#333' } }}>
          <TableHead>
            <TableRow>
              <TableCell sx={{ bgcolor: '#2d2d2d', fontWeight: 'bold' }}>Timestamp</TableCell>
              <TableCell sx={{ bgcolor: '#2d2d2d', fontWeight: 'bold' }}>Nível</TableCell>
              <TableCell sx={{ bgcolor: '#2d2d2d', fontWeight: 'bold' }}>Classe (Logger)</TableCell>
              <TableCell sx={{ bgcolor: '#2d2d2d', fontWeight: 'bold' }}>Mensagem</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {filteredLogs.map((log, index) => (
              <TableRow key={index} hover sx={{ '&:hover': { bgcolor: '#333' } }}>
                <TableCell sx={{ whiteSpace: 'nowrap', color: '#569cd6' }}>
                  {new Date(log['@timestamp']).toLocaleString()}
                </TableCell>
                <TableCell>
                  <Chip 
                    label={log.level} 
                    size="small" 
                    color={getLevelColor(log.level) as any} 
                    sx={{ fontWeight: 'bold', fontSize: '0.7rem' }}
                  />
                </TableCell>
                <TableCell sx={{ color: '#4ec9b0', fontSize: '0.85rem' }}>
                  {log.logger_name?.split('.').pop()}
                </TableCell>
                <TableCell sx={{ fontFamily: 'monospace', fontSize: '0.85rem' }}>
                  {log.message}
                  {log.stack_trace && (
                    <Box sx={{ mt: 1, p: 1, bgcolor: '#000', color: '#f14c4c', borderRadius: 1, overflowX: 'auto' }}>
                      <pre style={{ margin: 0, fontSize: '0.75rem' }}>{log.stack_trace}</pre>
                    </Box>
                  )}
                </TableCell>
              </TableRow>
            ))}
            {filteredLogs.length === 0 && !loading && (
              <TableRow>
                <TableCell colSpan={4} align="center" sx={{ py: 4, color: '#888' }}>
                  Nenhum log encontrado.
                </TableCell>
              </TableRow>
            )}
          </TableBody>
        </Table>
      </TableContainer>
    </Box>
  );
}
