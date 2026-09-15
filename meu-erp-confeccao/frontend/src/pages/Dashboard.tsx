import React, { useEffect, useState } from 'react';
import { Box, Typography, Grid, Paper, CircularProgress, Chip } from '@mui/material';
import { Package, ClipboardList, CheckCircle, TrendingUp, Activity } from 'lucide-react';
import { AreaChart, Area, PieChart, Pie, Cell, BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer, Legend } from 'recharts';
import api from '../api/axios';
import { useAuth } from '../contexts/AuthContext';

interface DashboardData {
  metrics: {
    totalProdutos: number;
    opsEmAndamento: number;
    opsConcluidas: number;
    valorTotalEstoque: number;
  };
  opStatusDistribution: any[];
  productivityHistory: any[];
  upcomingReceivables: any[];
  opsAtrasadas: any[];
  estoqueCritico: any[];
}

const StatCard = ({ title, value, icon, color, gradient }: any) => (
  <Paper sx={{ 
    p: 3, 
    borderRadius: 4,
    display: 'flex',
    alignItems: 'center',
    gap: 2,
    background: 'var(--bg-card)',
    backdropFilter: 'blur(10px)',
    border: '1px solid rgba(255, 255, 255, 0.1)',
    boxShadow: '0 8px 32px rgba(0,0,0,0.08)',
    transition: 'all 0.3s ease',
    position: 'relative',
    overflow: 'hidden',
    '&:hover': { transform: 'translateY(-4px)', boxShadow: '0 12px 40px rgba(0,0,0,0.12)' },
    '&::before': {
      content: '""',
      position: 'absolute',
      top: 0, left: 0, right: 0, height: '4px',
      background: gradient || color,
      opacity: 0.8
    }
  }}>
    <Box sx={{ 
      p: 2, 
      borderRadius: 3, 
      background: gradient || `${color}15`,
      color: gradient ? 'white' : color,
      display: 'flex',
      boxShadow: gradient ? `0 4px 12px ${color}40` : 'none'
    }}>
      {icon}
    </Box>
    <Box>
      <Typography variant="body2" sx={{ color: 'text.secondary', fontWeight: 700, textTransform: 'uppercase', letterSpacing: 1 }}>
        {title}
      </Typography>
      <Typography variant="h4" sx={{ fontWeight: 800, color: 'text.primary', mt: 0.5, letterSpacing: '-0.5px' }}>
        {value}
      </Typography>
    </Box>
  </Paper>
);

const GlassPanel = ({ children, title, action }: any) => (
  <Paper sx={{ 
    p: 3, 
    borderRadius: 4, 
    background: 'var(--bg-card)', 
    backdropFilter: 'blur(10px)',
    border: '1px solid var(--border-color)',
    boxShadow: '0 8px 32px rgba(0,0,0,0.04)',
    height: '100%',
    display: 'flex',
    flexDirection: 'column'
  }}>
    <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 3 }}>
      <Typography variant="h6" sx={{ fontWeight: 800 }}>{title}</Typography>
      {action && <Box>{action}</Box>}
    </Box>
    <Box sx={{ flex: 1 }}>
      {children}
    </Box>
  </Paper>
);

const CustomTooltip = ({ active, payload, label, prefix = '' }: any) => {
  if (active && payload && payload.length) {
    return (
      <Box sx={{ background: 'var(--bg-paper)', p: 2, borderRadius: 2, border: '1px solid var(--border-color)', boxShadow: '0 4px 20px rgba(0,0,0,0.1)' }}>
        <Typography variant="subtitle2" sx={{ fontWeight: 700, mb: 1 }}>{label}</Typography>
        {payload.map((p: any, i: number) => (
          <Typography key={i} variant="body2" sx={{ color: p.color || p.payload?.color, fontWeight: 600 }}>
            {p.name}: {prefix}{typeof p.value === 'number' && prefix === 'R$ ' ? p.value.toLocaleString('pt-BR', { minimumFractionDigits: 2 }) : p.value}
          </Typography>
        ))}
      </Box>
    );
  }
  return null;
};

const Dashboard = () => {
  const { user } = useAuth();
  const [data, setData] = useState<DashboardData | null>(null);
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

  if (loading || !data) {
    return (
      <Box sx={{ display: 'flex', justifyContent: 'center', alignItems: 'center', height: '80vh' }}>
        <CircularProgress size={60} thickness={4} sx={{ color: 'var(--accent-primary)' }} />
      </Box>
    );
  }

  const { metrics, opStatusDistribution, productivityHistory, upcomingReceivables, opsAtrasadas, estoqueCritico } = data;

  const formatCurrency = (val: number) => {
    return new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' }).format(val);
  };

  return (
    <Box sx={{ p: { xs: 2, md: 4 }, maxWidth: 1600, margin: '0 auto' }} className="animate-fade-in">
      <Box sx={{ mb: 4, display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
        <Box>
          <Typography variant="h4" sx={{ fontWeight: 900, mb: 1 }}>
            Dashboard da Confecção
          </Typography>
          <Typography variant="body1" sx={{ color: 'text.secondary', fontWeight: 500 }}>
            Bem-vindo(a) de volta, <strong>{user?.nome}</strong>. Confira o desempenho do chão de fábrica.
          </Typography>
        </Box>
        <Chip 
          icon={<Activity size={16} />} 
          label="STATUS OPERACIONAL" 
          color="success" 
          sx={{ fontWeight: 800, borderRadius: 2, px: 1, backgroundColor: 'rgba(34, 197, 94, 0.1)', color: '#16a34a', border: '1px solid rgba(34,197,94,0.2)' }} 
        />
      </Box>

      {/* KPI Cards */}
      <Grid container spacing={3} sx={{ mb: 4 }}>
        <Grid size={{ xs: 12, sm: 6, md: 3 }}>
          <StatCard 
            title="Total de Produtos" 
            value={metrics?.totalProdutos || 0} 
            icon={<Package size={28} />} 
            color="#3b82f6" 
            gradient="linear-gradient(135deg, #3b82f6 0%, #2563eb 100%)"
          />
        </Grid>
        <Grid size={{ xs: 12, sm: 6, md: 3 }}>
          <StatCard 
            title="OPs em Andamento" 
            value={metrics?.opsEmAndamento || 0} 
            icon={<ClipboardList size={28} />} 
            color="#f59e0b" 
            gradient="linear-gradient(135deg, #f59e0b 0%, #d97706 100%)"
          />
        </Grid>
        <Grid size={{ xs: 12, sm: 6, md: 3 }}>
          <StatCard 
            title="OPs Concluídas" 
            value={metrics?.opsConcluidas || 0} 
            icon={<CheckCircle size={28} />} 
            color="#10b981" 
            gradient="linear-gradient(135deg, #10b981 0%, #059669 100%)"
          />
        </Grid>
        <Grid size={{ xs: 12, sm: 6, md: 3 }}>
          <StatCard 
            title="Estoque (Custo)" 
            value={formatCurrency(metrics?.valorTotalEstoque || 0)} 
            icon={<TrendingUp size={28} />} 
            color="#8b5cf6" 
            gradient="linear-gradient(135deg, #8b5cf6 0%, #7c3aed 100%)"
          />
        </Grid>
      </Grid>

      {/* Charts Section 1 */}
      <Grid container spacing={3} sx={{ mb: 4 }}>
        <Grid size={{ xs: 12, lg: 8 }}>
          <GlassPanel title="Produtividade: Minutos Produzidos (7 Dias)">
            <Box sx={{ height: 350, width: '100%', mt: 2 }}>
              <ResponsiveContainer width="100%" height="100%">
                <AreaChart data={productivityHistory || []} margin={{ top: 10, right: 30, left: 0, bottom: 0 }}>
                  <defs>
                    <linearGradient id="colorMinutos" x1="0" y1="0" x2="0" y2="1">
                      <stop offset="5%" stopColor="#3b82f6" stopOpacity={0.3}/>
                      <stop offset="95%" stopColor="#3b82f6" stopOpacity={0}/>
                    </linearGradient>
                  </defs>
                  <CartesianGrid strokeDasharray="3 3" stroke="var(--border-color)" vertical={false} />
                  <XAxis dataKey="name" stroke="var(--text-secondary)" tick={{ fill: 'var(--text-secondary)' }} axisLine={false} tickLine={false} />
                  <YAxis stroke="var(--text-secondary)" tick={{ fill: 'var(--text-secondary)' }} axisLine={false} tickLine={false} />
                  <Tooltip content={<CustomTooltip />} />
                  <Area type="monotone" name="Minutos (SAM)" dataKey="minutos" stroke="#3b82f6" strokeWidth={4} fillOpacity={1} fill="url(#colorMinutos)" />
                </AreaChart>
              </ResponsiveContainer>
            </Box>
          </GlassPanel>
        </Grid>

        <Grid size={{ xs: 12, lg: 4 }}>
          <GlassPanel title="Status das OPs">
            <Box sx={{ height: 350, width: '100%', mt: 2, display: 'flex', justifyContent: 'center', alignItems: 'center' }}>
              <ResponsiveContainer width="100%" height="100%">
                <PieChart>
                  <Pie
                    data={opStatusDistribution}
                    cx="50%"
                    cy="50%"
                    innerRadius={70}
                    outerRadius={110}
                    paddingAngle={5}
                    dataKey="value"
                    stroke="none"
                  >
                    {opStatusDistribution.map((entry, index) => (
                      <Cell key={`cell-${index}`} fill={entry.color} />
                    ))}
                  </Pie>
                  <Tooltip content={<CustomTooltip />} />
                  <Legend verticalAlign="bottom" height={36} iconType="circle" />
                </PieChart>
              </ResponsiveContainer>
            </Box>
          </GlassPanel>
        </Grid>
      </Grid>

      {/* Charts Section 2 */}
      <Grid container spacing={3}>
        <Grid size={{ xs: 12 }}>
          <GlassPanel title="Projeção Financeira: Contas a Receber (Próximas Semanas)">
            <Box sx={{ height: 350, width: '100%', mt: 2 }}>
              <ResponsiveContainer width="100%" height="100%">
                <BarChart data={upcomingReceivables || []} margin={{ top: 20, right: 30, left: 20, bottom: 5 }}>
                  <CartesianGrid strokeDasharray="3 3" stroke="var(--border-color)" vertical={false} />
                  <XAxis dataKey="name" stroke="var(--text-secondary)" tick={{ fill: 'var(--text-secondary)' }} axisLine={false} tickLine={false} />
                  <YAxis stroke="var(--text-secondary)" tick={{ fill: 'var(--text-secondary)' }} axisLine={false} tickLine={false} tickFormatter={(val) => `R$ ${val/1000}k`} />
                  <Tooltip content={<CustomTooltip prefix="R$ " />} />
                  <Legend iconType="circle" />
                  <Bar name="A Receber (No Prazo)" dataKey="receber" stackId="a" fill="#10b981" radius={[0, 0, 4, 4]} />
                  <Bar name="Inadimplente (Atrasado)" dataKey="inadimplente" stackId="a" fill="#ef4444" radius={[4, 4, 0, 0]} />
                </BarChart>
              </ResponsiveContainer>
            </Box>
          </GlassPanel>
        </Grid>
      </Grid>

      {/* New Section: OPs Atrasadas & Estoque Crítico */}
      <Grid container spacing={3} sx={{ mt: 1 }}>
        <Grid size={{ xs: 12, md: 6 }}>
          <GlassPanel title="OPs em Atraso (Mais Antigas)">
            {opsAtrasadas && opsAtrasadas.length > 0 ? (
              <Box sx={{ display: 'flex', flexDirection: 'column', gap: 2, mt: 2 }}>
                {opsAtrasadas.map((op: any) => (
                  <Box key={op.id} sx={{ p: 2, borderRadius: 2, background: 'rgba(239, 68, 68, 0.05)', border: '1px solid rgba(239, 68, 68, 0.2)', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                    <Box>
                      <Typography variant="subtitle2" sx={{ fontWeight: 700, color: '#ef4444' }}>OP #{op.numero}</Typography>
                      <Typography variant="body2" sx={{ color: 'text.secondary' }}>{op.produto}</Typography>
                    </Box>
                    <Box sx={{ textAlign: 'right' }}>
                      <Typography variant="caption" sx={{ display: 'block', color: 'text.secondary' }}>Criado em</Typography>
                      <Typography variant="body2" sx={{ fontWeight: 600 }}>{new Date(op.criadoEm).toLocaleDateString('pt-BR')}</Typography>
                    </Box>
                  </Box>
                ))}
              </Box>
            ) : (
              <Typography variant="body2" sx={{ color: 'text.secondary', mt: 2, fontStyle: 'italic' }}>Nenhuma OP pendente antiga.</Typography>
            )}
          </GlassPanel>
        </Grid>

        <Grid size={{ xs: 12, md: 6 }}>
          <GlassPanel title="Materiais Acabando (Top 5)">
            {estoqueCritico && estoqueCritico.length > 0 ? (
              <Box sx={{ display: 'flex', flexDirection: 'column', gap: 2, mt: 2 }}>
                {estoqueCritico.map((mat: any) => (
                  <Box key={mat.id} sx={{ p: 2, borderRadius: 2, background: 'rgba(245, 158, 11, 0.05)', border: '1px solid rgba(245, 158, 11, 0.2)', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                    <Box>
                      <Typography variant="subtitle2" sx={{ fontWeight: 700, color: '#d97706' }}>{mat.codigo}</Typography>
                      <Typography variant="body2" sx={{ color: 'text.secondary' }}>{mat.nome}</Typography>
                    </Box>
                    <Box sx={{ textAlign: 'right' }}>
                      <Typography variant="caption" sx={{ display: 'block', color: 'text.secondary' }}>Qtd Atual</Typography>
                      <Typography variant="body2" sx={{ fontWeight: 800, color: '#d97706' }}>{mat.quantidadeAtual} {mat.unidade}</Typography>
                    </Box>
                  </Box>
                ))}
              </Box>
            ) : (
              <Typography variant="body2" sx={{ color: 'text.secondary', mt: 2, fontStyle: 'italic' }}>Estoque saudável.</Typography>
            )}
          </GlassPanel>
        </Grid>
      </Grid>
    </Box>
  );
};

export default Dashboard;
