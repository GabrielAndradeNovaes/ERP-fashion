import React, { useEffect, useState } from 'react';
import { Box, Typography, Grid, Paper, CircularProgress, Divider, Chip, Avatar } from '@mui/material';
import { Building2, CheckCircle, AlertTriangle, DollarSign, Activity, Server, Database, Clock, ChevronRight } from 'lucide-react';
import { LineChart, Line, BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer } from 'recharts';
import api from '../../api/axios';

interface DashboardData {
  metrics: {
    totalTenants: number;
    activeTenants: number;
    inactiveTenants: number;
    pendingTenants: number;
    estimatedMRR: number;
  };
  mrrHistory: any[];
  tenantSignups: any[];
  systemHealth: {
    uptime: string;
    cpuUsage: number;
    memoryUsage: number;
    dbLatency: string;
  };
  recentActivities: any[];
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
      background: gradient || `rgb(${color})`,
      opacity: 0.8
    }
  }}>
    <Box sx={{ 
      p: 2, 
      borderRadius: 3, 
      background: gradient || `rgba(${color}, 0.1)`,
      color: gradient ? 'white' : `rgb(${color})`,
      display: 'flex',
      boxShadow: gradient ? '0 4px 12px rgba(99, 102, 241, 0.3)' : 'none'
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
          <Typography key={i} variant="body2" sx={{ color: p.color, fontWeight: 600 }}>
            {p.name}: {prefix}{typeof p.value === 'number' && prefix === 'R$ ' ? p.value.toLocaleString('pt-BR', { minimumFractionDigits: 2 }) : p.value}
          </Typography>
        ))}
      </Box>
    );
  }
  return null;
};

const AdminDashboard = () => {
  const [data, setData] = useState<DashboardData | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchData = async () => {
      try {
        const response = await api.get('/admin/dashboard/metrics');
        setData(response.data);
      } catch (error) {
        console.error("Erro ao buscar métricas:", error);
      } finally {
        setLoading(false);
      }
    };
    
    fetchData();
  }, []);

  if (loading || !data) {
    return (
      <Box sx={{ display: 'flex', justifyContent: 'center', alignItems: 'center', height: '80vh' }}>
        <CircularProgress size={60} thickness={4} sx={{ color: 'var(--accent-primary)' }} />
      </Box>
    );
  }

  const { metrics, mrrHistory, tenantSignups, systemHealth, recentActivities } = data;

  return (
    <Box sx={{ p: { xs: 2, md: 4 }, maxWidth: 1600, margin: '0 auto' }}>
      <Box sx={{ mb: 4, display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
        <Box>
          <Typography variant="h4" sx={{ fontWeight: 900, mb: 1, background: 'var(--accent-gradient)', WebkitBackgroundClip: 'text', WebkitTextFillColor: 'transparent' }}>
            Control Plane (Master)
          </Typography>
          <Typography variant="body1" sx={{ color: 'text.secondary', fontWeight: 500 }}>
            Visão executiva e telemetria da plataforma Fashion ERP
          </Typography>
        </Box>
        <Chip 
          icon={<Activity size={16} />} 
          label="SISTEMA OPERACIONAL" 
          color="success" 
          sx={{ fontWeight: 800, borderRadius: 2, px: 1, backgroundColor: 'rgba(34, 197, 94, 0.1)', color: '#16a34a', border: '1px solid rgba(34,197,94,0.2)' }} 
        />
      </Box>

      {/* KPI Cards */}
      <Grid container spacing={3} sx={{ mb: 4 }}>
        <Grid item xs={12} sm={6} md={3}>
          <StatCard 
            title="Receita Recorrente (MRR)" 
            value={new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' }).format(metrics.estimatedMRR)} 
            icon={<DollarSign size={28} />} 
            gradient="linear-gradient(135deg, #10b981 0%, #059669 100%)"
          />
        </Grid>
        <Grid item xs={12} sm={6} md={3}>
          <StatCard 
            title="Tenants Ativos" 
            value={metrics.activeTenants} 
            icon={<CheckCircle size={28} />} 
            gradient="linear-gradient(135deg, #3b82f6 0%, #2563eb 100%)"
          />
        </Grid>
        <Grid item xs={12} sm={6} md={3}>
          <StatCard 
            title="Tenants Pendentes" 
            value={metrics.pendingTenants} 
            icon={<AlertTriangle size={28} />} 
            color="245, 158, 11" // Amber
          />
        </Grid>
        <Grid item xs={12} sm={6} md={3}>
          <StatCard 
            title="Total de Tenants" 
            value={metrics.totalTenants} 
            icon={<Building2 size={28} />} 
            color="139, 92, 246" // Violet
          />
        </Grid>
      </Grid>

      {/* Charts Section */}
      <Grid container spacing={3} sx={{ mb: 4 }}>
        <Grid item xs={12} lg={8}>
          <GlassPanel title="Crescimento de Receita (MRR)">
            <Box sx={{ height: 350, width: '100%', mt: 2 }}>
              <ResponsiveContainer width="100%" height="100%">
                <LineChart data={mrrHistory} margin={{ top: 5, right: 30, left: 20, bottom: 5 }}>
                  <CartesianGrid strokeDasharray="3 3" stroke="var(--border-color)" vertical={false} />
                  <XAxis dataKey="name" stroke="var(--text-secondary)" tick={{ fill: 'var(--text-secondary)' }} axisLine={false} tickLine={false} />
                  <YAxis stroke="var(--text-secondary)" tick={{ fill: 'var(--text-secondary)' }} axisLine={false} tickLine={false} tickFormatter={(val) => `R$ ${val/1000}k`} />
                  <Tooltip content={<CustomTooltip prefix="R$ " />} />
                  <Line 
                    type="monotone" 
                    name="MRR Estimado"
                    dataKey="mrr" 
                    stroke="#10b981" 
                    strokeWidth={4} 
                    dot={{ r: 6, fill: '#10b981', strokeWidth: 2, stroke: '#fff' }} 
                    activeDot={{ r: 8, fill: '#10b981', stroke: '#fff', strokeWidth: 2 }}
                  />
                </LineChart>
              </ResponsiveContainer>
            </Box>
          </GlassPanel>
        </Grid>
        <Grid item xs={12} lg={4}>
          <GlassPanel title="Aquisição de Tenants">
            <Box sx={{ height: 350, width: '100%', mt: 2 }}>
              <ResponsiveContainer width="100%" height="100%">
                <BarChart data={tenantSignups} margin={{ top: 5, right: 10, left: -20, bottom: 5 }}>
                  <CartesianGrid strokeDasharray="3 3" stroke="var(--border-color)" vertical={false} />
                  <XAxis dataKey="name" stroke="var(--text-secondary)" tick={{ fill: 'var(--text-secondary)' }} axisLine={false} tickLine={false} />
                  <YAxis stroke="var(--text-secondary)" tick={{ fill: 'var(--text-secondary)' }} axisLine={false} tickLine={false} />
                  <Tooltip content={<CustomTooltip />} />
                  <Bar 
                    name="Novos Tenants"
                    dataKey="novos" 
                    fill="#3b82f6" 
                    radius={[6, 6, 0, 0]} 
                  />
                </BarChart>
              </ResponsiveContainer>
            </Box>
          </GlassPanel>
        </Grid>
      </Grid>

      {/* System Health & Activity Feed */}
      <Grid container spacing={3}>
        <Grid item xs={12} md={4}>
          <GlassPanel title="Saúde do Sistema">
            <Box sx={{ display: 'flex', flexDirection: 'column', gap: 3, mt: 2 }}>
              
              <Box>
                <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 1 }}>
                  <Typography variant="body2" sx={{ color: 'text.secondary', display: 'flex', alignItems: 'center', gap: 1 }}><Clock size={16} /> Uptime</Typography>
                  <Typography variant="body2" sx={{ fontWeight: 800, color: '#10b981' }}>{systemHealth.uptime}</Typography>
                </Box>
                <Box sx={{ w: '100%', height: 6, bgcolor: 'rgba(16,185,129,0.2)', borderRadius: 3, overflow: 'hidden' }}>
                  <Box sx={{ width: '99.98%', height: '100%', bgcolor: '#10b981' }} />
                </Box>
              </Box>

              <Box>
                <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 1 }}>
                  <Typography variant="body2" sx={{ color: 'text.secondary', display: 'flex', alignItems: 'center', gap: 1 }}><Server size={16} /> Uso de CPU</Typography>
                  <Typography variant="body2" sx={{ fontWeight: 800, color: systemHealth.cpuUsage > 80 ? '#ef4444' : '#3b82f6' }}>{systemHealth.cpuUsage}%</Typography>
                </Box>
                <Box sx={{ w: '100%', height: 6, bgcolor: 'rgba(59,130,246,0.2)', borderRadius: 3, overflow: 'hidden' }}>
                  <Box sx={{ width: `${systemHealth.cpuUsage}%`, height: '100%', bgcolor: '#3b82f6' }} />
                </Box>
              </Box>

              <Box>
                <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 1 }}>
                  <Typography variant="body2" sx={{ color: 'text.secondary', display: 'flex', alignItems: 'center', gap: 1 }}><Activity size={16} /> Consumo de Memória</Typography>
                  <Typography variant="body2" sx={{ fontWeight: 800, color: systemHealth.memoryUsage > 85 ? '#ef4444' : '#8b5cf6' }}>{systemHealth.memoryUsage}%</Typography>
                </Box>
                <Box sx={{ w: '100%', height: 6, bgcolor: 'rgba(139,92,246,0.2)', borderRadius: 3, overflow: 'hidden' }}>
                  <Box sx={{ width: `${systemHealth.memoryUsage}%`, height: '100%', bgcolor: '#8b5cf6' }} />
                </Box>
              </Box>

              <Box>
                <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 1 }}>
                  <Typography variant="body2" sx={{ color: 'text.secondary', display: 'flex', alignItems: 'center', gap: 1 }}><Database size={16} /> Latência Banco (Leitura)</Typography>
                  <Typography variant="body2" sx={{ fontWeight: 800 }}>{systemHealth.dbLatency}</Typography>
                </Box>
              </Box>

            </Box>
          </GlassPanel>
        </Grid>
        
        <Grid item xs={12} md={8}>
          <GlassPanel title="Audit Log (Atividades Recentes)">
            <Box sx={{ mt: 2, display: 'flex', flexDirection: 'column', gap: 0 }}>
              {recentActivities.map((act, index) => (
                <Box key={act.id} sx={{ 
                  display: 'flex', 
                  gap: 3, 
                  p: 2, 
                  borderRadius: 2,
                  transition: 'background 0.2s',
                  '&:hover': { background: 'rgba(128,128,128,0.05)' }
                }}>
                  <Box sx={{ 
                    width: 12, 
                    display: 'flex', 
                    flexDirection: 'column', 
                    alignItems: 'center', 
                    position: 'relative' 
                  }}>
                    <Box sx={{ 
                      width: 12, height: 12, borderRadius: '50%', 
                      bgcolor: act.type === 'success' ? '#10b981' : act.type === 'warning' ? '#f59e0b' : '#3b82f6',
                      boxShadow: `0 0 0 4px rgba(${act.type === 'success' ? '16,185,129' : act.type === 'warning' ? '245,158,11' : '59,130,246'}, 0.2)`
                    }} />
                    {index < recentActivities.length - 1 && (
                      <Box sx={{ width: 2, height: '100%', bgcolor: 'var(--border-color)', position: 'absolute', top: 16 }} />
                    )}
                  </Box>
                  <Box sx={{ flex: 1, pb: index < recentActivities.length - 1 ? 2 : 0 }}>
                    <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 0.5 }}>
                      <Typography variant="subtitle2" sx={{ fontWeight: 700 }}>{act.title}</Typography>
                      <Typography variant="caption" sx={{ color: 'text.secondary', fontWeight: 600 }}>{act.time}</Typography>
                    </Box>
                    <Typography variant="body2" sx={{ color: 'text.secondary' }}>{act.description}</Typography>
                  </Box>
                </Box>
              ))}
            </Box>
          </GlassPanel>
        </Grid>
      </Grid>
    </Box>
  );
};

export default AdminDashboard;
