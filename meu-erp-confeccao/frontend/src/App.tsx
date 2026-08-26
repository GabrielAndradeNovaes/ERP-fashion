import { BrowserRouter, Routes, Route, Link, useLocation, Navigate } from 'react-router-dom';
import { LayoutDashboard, Clock, Scissors, PackageSearch, Package, ClipboardList, LogOut, Sun, Moon, Building2 } from 'lucide-react';
import { Box, Typography } from '@mui/material';
import TabelaTempos from './pages/TabelaTempos';
import Estoque from './pages/Estoque';
import Produtos from './pages/Produtos';
import OrdensProducao from './pages/OrdensProducao';
import Cupons from './pages/PCP/Cupons';
import Bipagem from './pages/PCP/Bipagem';
import Funcionarios from './pages/PCP/Funcionarios';
import Produtividade from './pages/PCP/Produtividade';
import Clientes from './pages/Clientes';
import Fornecedores from './pages/Fornecedores';
import Categorias from './pages/Categorias';
import UnidadesMedida from './pages/UnidadesMedida';
import Login from './pages/Login';
import Dashboard from './pages/Dashboard';
import { Users, Truck, Tags, Ruler, ScanLine, UserCog, BarChart, FileText } from 'lucide-react';
import React from 'react';
import { AuthProvider, useAuth } from './contexts/AuthContext';
import { useThemeContext } from './contexts/ThemeContext';

import PaymentPending from './pages/PaymentPending';
import TenantsList from './pages/Backoffice/TenantsList';

// Rotas Protegidas
const PrivateRoute = ({ children, requireSuperAdmin = false }: { children: React.ReactNode, requireSuperAdmin?: boolean }) => {
  const { isAuthenticated, user } = useAuth();
  
  if (!isAuthenticated) return <Navigate to="/login" />;

  // Se o tenant estiver inadimplente, trava o acesso geral e mostra a tela de pendência
  if (user?.tenantStatus === 'INADIMPLENTE' && !requireSuperAdmin) {
    return <PaymentPending />;
  }

  // Futura validação de rotas admin
  if (requireSuperAdmin && user?.role !== 'SUPERADMIN') {
    return <Navigate to="/" />; // ou uma tela de "Não Autorizado"
  }

  return <>{children}</>;
};

// Menu Lateral Premium
const Sidebar = () => {
  const location = useLocation();
  const { user, logout } = useAuth();
  const { mode, toggleTheme } = useThemeContext();
  
  const navGroups = [
    {
      title: 'Geral',
      items: [
        { path: '/', label: 'Dashboard', icon: <LayoutDashboard size={20} /> },
      ]
    },
    {
      title: 'Produção (PCP)',
      items: [
        { path: '/pcp/ordens', label: 'Ordens de Produção', icon: <ClipboardList size={20} /> },
        { path: '/pcp/cupons', label: 'Cupons / Etiquetas', icon: <FileText size={20} /> },
        { path: '/pcp/bipagem', label: 'Bipagem Rápida', icon: <ScanLine size={20} /> },
        { path: '/pcp/produtividade', label: 'Produtividade', icon: <BarChart size={20} /> },
        { path: '/pcp/funcionarios', label: 'Funcionários (PCP)', icon: <UserCog size={20} /> },
        { path: '/pcp/tempos', label: 'Tabela de Tempos', icon: <Clock size={20} /> },
      ]
    },
    {
      title: 'Inventário e Engenharia',
      items: [
        { path: '/catalog/produtos', label: 'Produtos e Fichas', icon: <Package size={20} /> },
        { path: '/estoque', label: 'Estoque', icon: <PackageSearch size={20} /> },
      ]
    },
    {
      title: 'Cadastros Base',
      items: [
        { path: '/core/clientes', label: 'Clientes', icon: <Users size={20} /> },
        { path: '/core/fornecedores', label: 'Fornecedores', icon: <Truck size={20} /> },
        { path: '/core/categorias', label: 'Categorias', icon: <Tags size={20} /> },
        { path: '/core/unidades-medida', label: 'Unidades de Medida', icon: <Ruler size={20} /> },
      ]
    }
  ];

  return (
    <div className="sidebar premium-card" style={{ 
      display: 'flex', 
      flexDirection: 'column', 
      height: '100vh',
      background: 'var(--bg-card)',
      borderRight: '1px solid var(--border-color)',
      padding: '24px 16px',
      width: 'var(--sidebar-width)',
      boxShadow: '4px 0 24px rgba(0,0,0,0.2)'
    }}>
      <div style={{ marginBottom: '2rem', padding: '0 1rem' }}>
        <h1 className="text-gradient" style={{ fontSize: '1.5rem', display: 'flex', alignItems: 'center', gap: '0.75rem', fontWeight: 800 }}>
          <div style={{ background: 'var(--accent-gradient)', padding: '6px', borderRadius: '8px', display: 'flex' }}>
            <Scissors size={20} color="white" />
          </div>
          Fashion ERP
        </h1>
      </div>
      
      <nav style={{ display: 'flex', flexDirection: 'column', gap: '1rem', flex: 1, overflowY: 'auto', paddingBottom: '1rem' }}>
        
        {user?.role === 'SUPERADMIN' && (
          <Box sx={{ display: 'flex', flexDirection: 'column', gap: '0.25rem' }}>
            <Typography variant="overline" sx={{ px: 2, color: 'var(--text-muted)', fontWeight: 700, letterSpacing: '0.5px' }}>
              Backoffice (Admin)
            </Typography>
            <Link 
              to="/admin/tenants"
              style={{
                display: 'flex', alignItems: 'center', gap: '1rem', padding: '0.75rem 1rem',
                borderRadius: 'var(--radius-md)',
                color: location.pathname.startsWith('/admin') ? 'white' : 'var(--text-secondary)',
                background: location.pathname.startsWith('/admin') ? 'var(--accent-gradient)' : 'transparent',
                fontWeight: location.pathname.startsWith('/admin') ? 600 : 500,
                transition: 'all var(--transition-fast)',
                boxShadow: location.pathname.startsWith('/admin') ? '0 4px 14px 0 rgba(99, 102, 241, 0.39)' : 'none',
                textDecoration: 'none'
              }}
              onMouseEnter={(e) => { if (!location.pathname.startsWith('/admin')) e.currentTarget.style.backgroundColor = 'rgba(128,128,128,0.1)' }}
              onMouseLeave={(e) => { if (!location.pathname.startsWith('/admin')) e.currentTarget.style.backgroundColor = 'transparent' }}
            >
              <Building2 size={20} />
              Gestão de Tenants
            </Link>
          </Box>
        )}

        {navGroups.map((group, index) => (
          <Box key={index} sx={{ display: 'flex', flexDirection: 'column', gap: '0.25rem' }}>
            <Typography variant="overline" sx={{ px: 2, color: 'var(--text-muted)', fontWeight: 700, letterSpacing: '0.5px' }}>
              {group.title}
            </Typography>
            {group.items.map(item => {
              const isActive = location.pathname === item.path;
              return (
                <Link 
                  key={item.path} 
                  to={item.path}
                  style={{
                    display: 'flex',
                    alignItems: 'center',
                    gap: '1rem',
                    padding: '0.75rem 1rem',
                    borderRadius: 'var(--radius-md)',
                    color: isActive ? 'white' : 'var(--text-secondary)',
                    background: isActive ? 'var(--accent-gradient)' : 'transparent',
                    fontWeight: isActive ? 600 : 500,
                    transition: 'all var(--transition-fast)',
                    boxShadow: isActive ? '0 4px 14px 0 rgba(99, 102, 241, 0.39)' : 'none',
                    textDecoration: 'none'
                  }}
                  onMouseEnter={(e) => { if (!isActive) e.currentTarget.style.backgroundColor = 'rgba(128,128,128,0.1)' }}
                  onMouseLeave={(e) => { if (!isActive) e.currentTarget.style.backgroundColor = 'transparent' }}
                >
                  {item.icon}
                  {item.label}
                </Link>
              );
            })}
          </Box>
        ))}
      </nav>

      {/* User Profile & Logout */}
      <Box sx={{ mt: 'auto', pt: 2, borderTop: '1px solid var(--border-color)', display: 'flex', alignItems: 'center', justifyContent: 'space-between', padding: '1rem 0.5rem 0' }}>
        <Box>
          <Typography variant="subtitle2" sx={{ fontWeight: 700, color: 'var(--text-primary)' }}>{user?.nome}</Typography>
          <Typography variant="caption" sx={{ color: 'var(--accent-primary)', fontWeight: 600 }}>{user?.role} - {user?.tenantId}</Typography>
        </Box>
        <Box sx={{ display: 'flex', gap: 1 }}>
          <button 
            onClick={toggleTheme}
            style={{ background: 'rgba(99, 102, 241, 0.1)', border: 'none', color: 'var(--accent-primary)', cursor: 'pointer', padding: '0.5rem', borderRadius: 'var(--radius-sm)', display: 'flex' }}
            title={mode === 'dark' ? "Modo Claro" : "Modo Escuro"}
          >
            {mode === 'dark' ? <Sun size={18} /> : <Moon size={18} />}
          </button>
          <button 
            onClick={logout}
            style={{ background: 'rgba(239, 68, 68, 0.1)', border: 'none', color: 'var(--danger)', cursor: 'pointer', padding: '0.5rem', borderRadius: 'var(--radius-sm)', display: 'flex' }}
            title="Sair"
          >
            <LogOut size={18} />
          </button>
        </Box>
      </Box>
    </div>
  );
};

const MainApp = () => {
  return (
    <div className="app-container">
      <Sidebar />
      <main className="main-content">
        <Routes>
          <Route path="/" element={<PrivateRoute><Dashboard /></PrivateRoute>} />
          <Route path="/core/clientes" element={<PrivateRoute><Clientes /></PrivateRoute>} />
          <Route path="/core/fornecedores" element={<PrivateRoute><Fornecedores /></PrivateRoute>} />
          <Route path="/core/categorias" element={<PrivateRoute><Categorias /></PrivateRoute>} />
          <Route path="/core/unidades-medida" element={<PrivateRoute><UnidadesMedida /></PrivateRoute>} />
          <Route path="/catalog/produtos" element={<PrivateRoute><Produtos /></PrivateRoute>} />
          <Route path="/pcp/tempos" element={<PrivateRoute><TabelaTempos /></PrivateRoute>} />
          <Route path="/estoque" element={<PrivateRoute><Estoque /></PrivateRoute>} />
          <Route path="/pcp/ordens" element={<PrivateRoute><OrdensProducao /></PrivateRoute>} />
          <Route path="/pcp/cupons" element={<PrivateRoute><Cupons /></PrivateRoute>} />
          <Route path="/pcp/bipagem" element={<PrivateRoute><Bipagem /></PrivateRoute>} />
          <Route path="/pcp/funcionarios" element={<PrivateRoute><Funcionarios /></PrivateRoute>} />
          <Route path="/pcp/produtividade" element={<PrivateRoute><Produtividade /></PrivateRoute>} />
          
          <Route path="/admin/tenants" element={<PrivateRoute requireSuperAdmin><TenantsList /></PrivateRoute>} />
        </Routes>
      </main>
    </div>
  );
};

function App() {
  return (
    <BrowserRouter>
      <AuthProvider>
        <Routes>
          <Route path="/login" element={<Login />} />
          <Route path="/*" element={<MainApp />} />
        </Routes>
      </AuthProvider>
    </BrowserRouter>
  );
}

export default App;
