import React from 'react';
import { Box, Typography, Paper, Avatar, Divider } from '@mui/material';
import { Sun, Moon, Palette, Mail, Shield, Building } from 'lucide-react';
import { useAuth } from '../contexts/AuthContext';
import { useThemeContext } from '../contexts/ThemeContext';

const Settings = () => {
  const { user } = useAuth();
  const { mode, setMode } = useThemeContext() as any; 

  return (
    <Box sx={{ p: 4, maxWidth: 1200, margin: '0 auto', animation: 'fadeIn 0.3s ease-in-out' }}>
      <Box sx={{ mb: 4 }}>
        <Typography variant="h4" sx={{ fontWeight: 800, mb: 1, color: 'var(--text-primary)' }}>
          Configurações e Perfil
        </Typography>
        <Typography variant="body1" sx={{ color: 'var(--text-secondary)' }}>
          Gerencie suas preferências pessoais e informações de acesso.
        </Typography>
      </Box>

      <Box sx={{ display: 'flex', flexWrap: 'wrap', gap: 4 }}>
        {/* Perfil */}
        <Box sx={{ flex: '1 1 350px' }}>
          <Paper sx={{ 
            p: 4, 
            borderRadius: 4, 
            background: 'var(--bg-card)', 
            border: '1px solid var(--border-color)',
            display: 'flex',
            flexDirection: 'column',
            alignItems: 'center',
            textAlign: 'center'
          }}>
            <Avatar 
              sx={{ 
                width: 100, 
                height: 100, 
                bgcolor: 'var(--accent-primary)',
                fontSize: '2.5rem',
                mb: 3,
                boxShadow: 'var(--glass-shadow)'
              }}
            >
              {user?.nome?.charAt(0).toUpperCase()}
            </Avatar>
            
            <Typography variant="h5" sx={{ fontWeight: 700, color: 'var(--text-primary)', mb: 0.5 }}>
              {user?.nome}
            </Typography>
            <Typography variant="body2" sx={{ color: 'var(--text-secondary)', display: 'flex', alignItems: 'center', justifyContent: 'center', gap: 1, mb: 3 }}>
              <Mail size={16} /> {user?.email}
            </Typography>

            <Divider sx={{ width: '100%', borderColor: 'var(--border-color)', mb: 3 }} />

            <Box sx={{ width: '100%', display: 'flex', flexDirection: 'column', gap: 2 }}>
              <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                <Typography variant="body2" sx={{ color: 'var(--text-muted)', display: 'flex', alignItems: 'center', gap: 1 }}>
                  <Shield size={16} /> Função
                </Typography>
                <Typography variant="body2" sx={{ fontWeight: 600, color: 'var(--text-primary)' }}>
                  {user?.role}
                </Typography>
              </Box>
              
              <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                <Typography variant="body2" sx={{ color: 'var(--text-muted)', display: 'flex', alignItems: 'center', gap: 1 }}>
                  <Building size={16} /> ID do Tenant
                </Typography>
                <Typography variant="body2" sx={{ fontWeight: 600, color: 'var(--text-primary)' }}>
                  {user?.tenantId}
                </Typography>
              </Box>
            </Box>
          </Paper>
        </Box>

        {/* Aparência */}
        <Box sx={{ flex: '2 1 500px' }}>
          <Paper sx={{ 
            p: 4, 
            borderRadius: 4, 
            background: 'var(--bg-card)', 
            border: '1px solid var(--border-color)',
            height: '100%'
          }}>
            <Typography variant="h6" sx={{ fontWeight: 700, color: 'var(--text-primary)', mb: 1 }}>
              Aparência do Sistema
            </Typography>
            <Typography variant="body2" sx={{ color: 'var(--text-secondary)', mb: 4 }}>
              Personalize como o ERP será exibido no seu dispositivo.
            </Typography>

            <Box sx={{ display: 'flex', gap: 3, flexWrap: 'wrap' }}>
              <ThemeOptionCard 
                active={mode === 'light'} 
                title="Modo Claro" 
                icon={<Sun size={32} />} 
                onClick={() => setMode('light')} 
                colors={['#f8fafc', '#ffffff', '#4f46e5']}
              />
              <ThemeOptionCard 
                active={mode === 'dark'} 
                title="Modo Escuro" 
                icon={<Moon size={32} />} 
                onClick={() => setMode('dark')}
                colors={['#0b0f19', '#151b2b', '#6366f1']} 
              />
              <ThemeOptionCard 
                active={mode === 'warm'} 
                title="Rose Gold" 
                icon={<Palette size={32} />} 
                onClick={() => setMode('warm')} 
                colors={['#fdfbf7', '#ffffff', '#d4af37']}
              />
            </Box>
          </Paper>
        </Box>
      </Box>
    </Box>
  );
};

const ThemeOptionCard = ({ active, title, icon, onClick, colors }: any) => {
  return (
    <Box 
      onClick={onClick}
      sx={{
        flex: '1 1 150px',
        p: 3,
        borderRadius: 3,
        border: `2px solid ${active ? 'var(--accent-primary)' : 'var(--border-color)'}`,
        background: active ? 'rgba(99, 102, 241, 0.05)' : 'transparent',
        cursor: 'pointer',
        display: 'flex',
        flexDirection: 'column',
        alignItems: 'center',
        transition: 'all var(--transition-fast)',
        '&:hover': {
          borderColor: 'var(--accent-primary)',
          transform: 'translateY(-2px)'
        }
      }}
    >
      <Box sx={{ color: active ? 'var(--accent-primary)' : 'var(--text-muted)', mb: 2 }}>
        {icon}
      </Box>
      <Typography variant="body2" sx={{ fontWeight: 600, color: active ? 'var(--text-primary)' : 'var(--text-secondary)', mb: 2 }}>
        {title}
      </Typography>

      <Box sx={{ display: 'flex', gap: 1 }}>
        {colors.map((color: string, i: number) => (
          <Box key={i} sx={{ width: 16, height: 16, borderRadius: '50%', bgcolor: color, border: '1px solid rgba(0,0,0,0.1)' }} />
        ))}
      </Box>
    </Box>
  );
};

export default Settings;
