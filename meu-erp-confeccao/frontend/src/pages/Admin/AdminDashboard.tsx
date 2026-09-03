import React, { useEffect, useState } from 'react';
import { Box, Typography, Grid, Paper, CircularProgress } from '@mui/material';
import { Building2, CheckCircle, AlertTriangle, DollarSign, Users } from 'lucide-react';
import api from '../../api/axios';

interface Metrics {
  totalTenants: number;
  activeTenants: number;
  inactiveTenants: number;
  pendingTenants: number;
  estimatedMRR: number;
}

const StatCard = ({ title, value, icon, color, gradient }: any) => (
  <Paper sx={{ 
    p: 3, 
    borderRadius: 4,
    display: 'flex',
    alignItems: 'center',
    gap: 2,
    background: 'var(--bg-card)',
    border: '1px solid var(--border-color)',
    boxShadow: '0 4px 24px rgba(0,0,0,0.05)',
    transition: 'transform 0.2s',
    '&:hover': { transform: 'translateY(-4px)' }
  }}>
    <Box sx={{ 
      p: 2, 
      borderRadius: 3, 
      background: gradient || `rgba(${color}, 0.1)`,
      color: gradient ? 'white' : `rgb(${color})`,
      display: 'flex',
    }}>
      {icon}
    </Box>
    <Box>
      <Typography variant="body2" sx={{ color: 'text.secondary', fontWeight: 600, textTransform: 'uppercase', letterSpacing: 1 }}>
        {title}
      </Typography>
      <Typography variant="h4" sx={{ fontWeight: 800, color: 'text.primary', mt: 0.5 }}>
        {value}
      </Typography>
    </Box>
  </Paper>
);

const AdminDashboard = () => {
  const [metrics, setMetrics] = useState<Metrics | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchMetrics = async () => {
      try {
        const response = await api.get('/admin/dashboard/metrics');
        setMetrics(response.data);
      } catch (error) {
        console.error("Erro ao buscar métricas:", error);
      } finally {
        setLoading(false);
      }
    };
    
    fetchMetrics();
  }, []);

  if (loading) {
    return (
      <Box sx={{ display: 'flex', justifyContent: 'center', p: 8 }}>
        <CircularProgress />
      </Box>
    );
  }

  return (
    <Box sx={{ p: 4, maxWidth: 1400, margin: '0 auto' }}>
      <Box sx={{ mb: 4 }}>
        <Typography variant="h4" sx={{ fontWeight: 800, mb: 1 }}>
          Painel Master
        </Typography>
        <Typography variant="body1" sx={{ color: 'text.secondary' }}>
          Visão global da plataforma Fashion ERP
        </Typography>
      </Box>

      <Grid container spacing={3}>
        <Grid item xs={12} sm={6} md={3}>
          <StatCard 
            title="Total de Tenants" 
            value={metrics?.totalTenants || 0} 
            icon={<Building2 size={28} />} 
            gradient="var(--accent-gradient)"
          />
        </Grid>
        <Grid item xs={12} sm={6} md={3}>
          <StatCard 
            title="Tenants Ativos" 
            value={metrics?.activeTenants || 0} 
            icon={<CheckCircle size={28} />} 
            color="34, 197, 94" // Green
          />
        </Grid>
        <Grid item xs={12} sm={6} md={3}>
          <StatCard 
            title="Tenants Inativos" 
            value={metrics?.inactiveTenants || 0} 
            icon={<AlertTriangle size={28} />} 
            color="239, 68, 68" // Red
          />
        </Grid>
        <Grid item xs={12} sm={6} md={3}>
          <StatCard 
            title="MRR Estimado" 
            value={new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' }).format(metrics?.estimatedMRR || 0)} 
            icon={<DollarSign size={28} />} 
            color="16, 185, 129" // Emerald
          />
        </Grid>
      </Grid>
    </Box>
  );
};

export default AdminDashboard;
