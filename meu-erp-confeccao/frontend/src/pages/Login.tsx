import React, { useState } from 'react';
import { Box, Button, TextField, Typography, Card, CircularProgress, Alert } from '@mui/material';
import { Scissors } from 'lucide-react';
import { useAuth } from '../contexts/AuthContext';
import api from '../api/axios';
import { useNavigate } from 'react-router-dom';

const Login = () => {
  const [email, setEmail] = useState('');
  const [senha, setSenha] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const { login } = useAuth();
  const navigate = useNavigate();

  const handleLogin = async (e: React.FormEvent) => {
    e.preventDefault();
    setError('');
    setLoading(true);

    try {
      const response = await api.post('/auth/login', { email, senha });
      
      const { token, ...userData } = response.data;
      
      login(token, userData);
      navigate('/');
    } catch (err: any) {
      setError(err.response?.data?.message || 'E-mail ou senha inválidos.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <Box 
      sx={{ 
        height: '100vh', 
        display: 'flex', 
        alignItems: 'center', 
        justifyContent: 'center',
        background: 'linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%)'
      }}
    >
      <Card sx={{ p: 5, width: '100%', maxWidth: 400, borderRadius: 3, boxShadow: '0 8px 32px rgba(0,0,0,0.1)' }}>
        <Box sx={{ display: 'flex', flexDirection: 'column', alignItems: 'center', mb: 4 }}>
          <Box sx={{ bgcolor: 'primary.main', p: 2, borderRadius: 2, color: 'white', mb: 2 }}>
            <Scissors size={32} />
          </Box>
          <Typography variant="h5" sx={{ fontWeight: 'bold' }}>Fashion ERP</Typography>
          <Typography variant="body2" color="text.secondary">Acesse sua conta</Typography>
        </Box>

        {error && <Alert severity="error" sx={{ mb: 3 }}>{error}</Alert>}

        <form onSubmit={handleLogin}>
          <TextField
            label="E-mail"
            type="email"
            fullWidth
            margin="normal"
            variant="outlined"
            required
            value={email}
            onChange={(e) => setEmail(e.target.value)}
          />
          <TextField
            label="Senha"
            type="password"
            fullWidth
            margin="normal"
            variant="outlined"
            required
            value={senha}
            onChange={(e) => setSenha(e.target.value)}
          />
          
          <Button
            type="submit"
            fullWidth
            variant="contained"
            size="large"
            disabled={loading}
            sx={{ mt: 3, py: 1.5, fontWeight: 'bold' }}
            disableElevation
          >
            {loading ? <CircularProgress size={24} color="inherit" /> : 'Entrar'}
          </Button>
        </form>
      </Card>
    </Box>
  );
};

export default Login;
