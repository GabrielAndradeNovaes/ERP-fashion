import React, { useEffect, useState } from 'react';
import { Box, Typography, Grid, Card, CardContent, CircularProgress } from '@mui/material';
import { Package, ClipboardList, CheckCircle, TrendingUp } from 'lucide-react';
import api from '../api/axios';
import { useAuth } from '../contexts/AuthContext';

interface DashboardResumo {
  totalProdutos: number;
  opsEmAndamento: number;
  opsConcluidas: number;
  valorTotalEstoque: number;
}

const StatCard = ({ title, value, icon, color }: { title: string, value: string | number, icon: React.ReactNode, color: string }) => (
  <Card variant="outlined" sx={{ borderRadius: 2, height: '100%', borderLeft: `4px solid ${color}` }}>
    <CardContent sx={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', p: 3 }}>
      <Box>
        <Typography color="text.secondary" variant="subtitle2" gutterBottom fontWeight="bold" textTransform="uppercase">
          {title}
        </Typography>
        <Typography variant="h4" fontWeight="bold">
          {value}
        </Typography>
      </Box>
      <Box sx={{ 
        bgcolor: `${color}15`, 
        p: 2, 
        borderRadius: '50%',
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'center',
        color: color
      }}>
        {icon}
      </Box>
    </CardContent>
  </Card>
);

const Dashboard = () => {
  const { user } = useAuth();
  const [data, setData] = useState<DashboardResumo | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchDashboard = async () => {
      try {
        const response = await api.get('/dashboard/resumo');
        setData(response.data);
      } catch (err) {
        console.error('Erro ao buscar dashboard:', err);
      } finally {
        setLoading(false);
      }
    };
    fetchDashboard();
  }, []);

  if (loading) {
    return (
      <Box sx={{ display: 'flex', justifyContent: 'center', p: 10 }}>
        <CircularProgress />
      </Box>
    );
  }

  const formatCurrency = (val: number) => {
    return new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' }).format(val);
  };

  return (
    <Box className="animate-fade-in">
      <Box sx={{ mb: 4 }}>
        <Typography variant="h4" component="h1" gutterBottom fontWeight="bold">
          Visão Geral
        </Typography>
        <Typography variant="body1" color="text.secondary">
          Bem-vindo de volta, <strong>{user?.nome}</strong>. Aqui está o resumo da produção.
        </Typography>
      </Box>

      <Grid container spacing={3}>
        <Grid item xs={12} sm={6} md={3}>
          <StatCard 
            title="Total de Produtos" 
            value={data?.totalProdutos || 0} 
            icon={<Package size={28} />} 
            color="#3b82f6" 
          />
        </Grid>
        <Grid item xs={12} sm={6} md={3}>
          <StatCard 
            title="OPs em Andamento" 
            value={data?.opsEmAndamento || 0} 
            icon={<ClipboardList size={28} />} 
            color="#f59e0b" 
          />
        </Grid>
        <Grid item xs={12} sm={6} md={3}>
          <StatCard 
            title="OPs Concluídas" 
            value={data?.opsConcluidas || 0} 
            icon={<CheckCircle size={28} />} 
            color="#10b981" 
          />
        </Grid>
        <Grid item xs={12} sm={6} md={3}>
          <StatCard 
            title="Estoque (Custo)" 
            value={formatCurrency(data?.valorTotalEstoque || 0)} 
            icon={<TrendingUp size={28} />} 
            color="#8b5cf6" 
          />
        </Grid>
      </Grid>
      
      {/* Aqui no futuro entra o Chart Recharts */}
      <Box sx={{ mt: 4 }}>
        <Card variant="outlined" sx={{ borderRadius: 2, p: 4, textAlign: 'center', color: 'text.secondary', bgcolor: 'grey.50' }}>
          <Typography variant="h6" gutterBottom>Módulo Financeiro & Analytics</Typography>
          <Typography variant="body2">Os gráficos de performance e vendas serão implementados na próxima fase (Módulo Comercial).</Typography>
        </Card>
      </Box>
    </Box>
  );
};

export default Dashboard;
