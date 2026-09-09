import React from 'react';
import { Box, Typography, Paper, Avatar, Divider, Button, Chip, Switch, FormControlLabel } from '@mui/material';
import { Sun, Moon, Palette, Mail, Shield, Building, Key, Bell, CheckCircle, Smartphone, Lock } from 'lucide-react';
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

          {/* Segurança */}
          <Paper sx={{ 
            p: 4, 
            mt: 4,
            borderRadius: 4, 
            background: 'var(--bg-card)', 
            border: '1px solid var(--border-color)'
          }}>
            <Typography variant="h6" sx={{ fontWeight: 700, color: 'var(--text-primary)', mb: 1, display: 'flex', alignItems: 'center', gap: 1 }}>
              <Lock size={20} className="text-accent" />
              Segurança
            </Typography>
            <Typography variant="body2" sx={{ color: 'var(--text-secondary)', mb: 4 }}>
              Gerencie a segurança da sua conta e autenticação de dois fatores.
            </Typography>

            <Box sx={{ display: 'flex', flexDirection: 'column', gap: 3 }}>
              <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                <Box>
                  <Typography variant="body2" sx={{ fontWeight: 600, color: 'var(--text-primary)' }}>Senha de Acesso</Typography>
                  <Typography variant="caption" sx={{ color: 'var(--text-muted)' }}>Última alteração há 3 meses</Typography>
                </Box>
                <Button variant="outlined" startIcon={<Key size={16} />} sx={{ textTransform: 'none', borderRadius: 2, borderColor: 'var(--border-color)', color: 'var(--text-primary)' }}>
                  Alterar
                </Button>
              </Box>
              <Divider sx={{ borderColor: 'var(--border-color)' }} />
              <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                <Box>
                  <Typography variant="body2" sx={{ fontWeight: 600, color: 'var(--text-primary)' }}>Autenticação em Duas Etapas (2FA)</Typography>
                  <Typography variant="caption" sx={{ color: 'var(--text-muted)' }}>Proteja sua conta com código via app.</Typography>
                </Box>
                <Switch color="primary" />
              </Box>
            </Box>
          </Paper>
        </Box>

        {/* Notificações e Módulos */}
        <Box sx={{ flex: '1 1 350px', display: 'flex', flexDirection: 'column', gap: 4 }}>
          {/* Permissões / Módulos */}
          <Paper sx={{ 
            p: 4, 
            borderRadius: 4, 
            background: 'var(--bg-card)', 
            border: '1px solid var(--border-color)'
          }}>
            <Typography variant="h6" sx={{ fontWeight: 700, color: 'var(--text-primary)', mb: 1, display: 'flex', alignItems: 'center', gap: 1 }}>
              <CheckCircle size={20} className="text-accent" />
              Seus Módulos Ativos
            </Typography>
            <Typography variant="body2" sx={{ color: 'var(--text-secondary)', mb: 3 }}>
              Funcionalidades que sua conta tem acesso no momento.
            </Typography>

            <Box sx={{ display: 'flex', flexWrap: 'wrap', gap: 1 }}>
              {(user?.modulosAtivos || ['PCP', 'ESTOQUE', 'FINANCEIRO', 'VENDAS']).map((mod, i) => (
                <Chip 
                  key={i} 
                  label={mod} 
                  size="small" 
                  sx={{ 
                    bgcolor: 'rgba(99, 102, 241, 0.1)', 
                    color: 'var(--accent-primary)', 
                    fontWeight: 600, 
                    borderRadius: 1.5 
                  }} 
                />
              ))}
            </Box>
          </Paper>

          {/* Notificações */}
          <Paper sx={{ 
            p: 4, 
            borderRadius: 4, 
            background: 'var(--bg-card)', 
            border: '1px solid var(--border-color)',
            flex: 1
          }}>
            <Typography variant="h6" sx={{ fontWeight: 700, color: 'var(--text-primary)', mb: 1, display: 'flex', alignItems: 'center', gap: 1 }}>
              <Bell size={20} className="text-accent" />
              Notificações
            </Typography>
            <Typography variant="body2" sx={{ color: 'var(--text-secondary)', mb: 3 }}>
              Como você deseja receber alertas do sistema.
            </Typography>

            <Box sx={{ display: 'flex', flexDirection: 'column', gap: 2 }}>
              <FormControlLabel 
                control={<Switch defaultChecked color="primary" />} 
                label={
                  <Box>
                    <Typography variant="body2" sx={{ fontWeight: 600, color: 'var(--text-primary)' }}>Alertas por E-mail</Typography>
                    <Typography variant="caption" sx={{ color: 'var(--text-muted)' }}>Ordens de produção atrasadas, boletos, etc.</Typography>
                  </Box>
                }
                sx={{ ml: 0, justifyContent: 'space-between', width: '100%', m: 0, '& .MuiFormControlLabel-label': { flex: 1 } }}
                labelPlacement="start"
              />
              <Divider sx={{ borderColor: 'var(--border-color)' }} />
              <FormControlLabel 
                control={<Switch defaultChecked color="primary" />} 
                label={
                  <Box>
                    <Typography variant="body2" sx={{ fontWeight: 600, color: 'var(--text-primary)' }}>Notificações no App</Typography>
                    <Typography variant="caption" sx={{ color: 'var(--text-muted)' }}>Avisos visuais enquanto usa o sistema.</Typography>
                  </Box>
                }
                sx={{ ml: 0, justifyContent: 'space-between', width: '100%', m: 0, '& .MuiFormControlLabel-label': { flex: 1 } }}
                labelPlacement="start"
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
