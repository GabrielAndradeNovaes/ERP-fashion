import { BrowserRouter, Routes, Route, Link, useLocation, Navigate } from 'react-router-dom';
import { LayoutDashboard, Clock, Scissors, PackageSearch, Package, ClipboardList, LogOut, Sun, Moon } from 'lucide-react';
import { Box, Typography } from '@mui/material';
import TabelaTempos from './pages/TabelaTempos';
import Estoque from './pages/Estoque';
import Produtos from './pages/Produtos';
import OrdensProducao from './pages/OrdensProducao';
import Clientes from './pages/Clientes';
import Fornecedores from './pages/Fornecedores';
import Categorias from './pages/Categorias';
import UnidadesMedida from './pages/UnidadesMedida';
import Login from './pages/Login';
import Dashboard from './pages/Dashboard';
import { Users, Truck, Tags, Ruler } from 'lucide-react';
import { AuthProvider, useAuth } from './contexts/AuthContext';
import { useThemeContext } from './contexts/ThemeContext';
import React from 'react';

// Rotas Protegidas
const PrivateRoute = ({ children }: { children: React.ReactNode }) => {
  const { isAuthenticated } = useAuth();
  return isAuthenticated ? <>{children}</> : <Navigate to="/login" />;
};

// Menu Lateral Premium
const Sidebar = () => {
  const location = useLocation();
  const { user, logout } = useAuth();
  const { mode, toggleTheme } = useThemeContext();
  
  const navItems = [
    { path: '/', label: 'Dashboard', icon: <LayoutDashboard size={20} /> },
    { path: '/core/clientes', label: 'Clientes', icon: <Users size={20} /> },
    { path: '/core/fornecedores', label: 'Fornecedores', icon: <Truck size={20} /> },
    { path: '/core/categorias', label: 'Categorias', icon: <Tags size={20} /> },
    { path: '/core/unidades-medida', label: 'Unidades de Medida', icon: <Ruler size={20} /> },
    { path: '/catalog/produtos', label: 'Produtos e Fichas', icon: <Package size={20} /> },
    { path: '/estoque', label: 'Estoque', icon: <PackageSearch size={20} /> },
    { path: '/pcp/ordens', label: 'Ordens de Produção', icon: <ClipboardList size={20} /> },
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
      
      <nav style={{ display: 'flex', flexDirection: 'column', gap: '0.5rem', flex: 1 }}>
        {navItems.map(item => {
          const isActive = location.pathname === item.path;
          return (
            <Link 
              key={item.path} 
              to={item.path}
              style={{
                display: 'flex',
                alignItems: 'center',
                gap: '1rem',
                padding: '0.85rem 1rem',
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
