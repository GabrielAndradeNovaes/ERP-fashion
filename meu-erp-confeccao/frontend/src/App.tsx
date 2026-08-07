import { BrowserRouter, Routes, Route, Link, useLocation, Navigate } from 'react-router-dom';
import { LayoutDashboard, Clock, Scissors, PackageSearch, Package, ClipboardList, LogOut } from 'lucide-react';
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

// Rotas Protegidas
const PrivateRoute = ({ children }: { children: JSX.Element }) => {
  const { isAuthenticated } = useAuth();
  return isAuthenticated ? children : <Navigate to="/login" />;
};

// Menu Lateral Premium
const Sidebar = () => {
  const location = useLocation();
  const { user, logout } = useAuth();
  
  const navItems = [
    { path: '/', label: 'Dashboard', icon: <LayoutDashboard size={20} /> },
    { path: '/core/clientes', label: 'Clientes', icon: <Users size={20} /> },
    { path: '/core/fornecedores', label: 'Fornecedores', icon: <Truck size={20} /> },
    { path: '/core/categorias', label: 'Categorias', icon: <Tags size={20} /> },
    { path: '/core/unidades-medida', label: 'Unidades de Medida', icon: <Ruler size={20} /> },
    { path: '/catalog/produtos', label: 'Produtos e Fichas', icon: <Package size={20} /> },
    { path: '/estoque', label: 'Estoque', icon: <PackageSearch size={20} /> },
    { path: '/pcp/ordens', label: 'Ordens de Produção', icon: <ClipboardList size={20} /> },
    { path: '/pcp/tempos', label: 'Tabela de Tempos', icon: <Clock size={20} /> },
  ];

  return (
    <div className="sidebar" style={{ display: 'flex', flexDirection: 'column', height: '100vh' }}>
      <div style={{ marginBottom: '2rem' }}>
        <h1 className="text-accent" style={{ fontSize: '1.5rem', display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
          <Scissors size={24} color="var(--accent-primary)" />
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
                color: isActive ? 'white' : 'var(--text-muted)',
                background: isActive ? 'var(--accent-primary)' : 'transparent',
                fontWeight: isActive ? 600 : 500,
                transition: 'all var(--transition-fast)',
                boxShadow: isActive ? '0 4px 12px rgba(99, 102, 241, 0.3)' : 'none'
              }}
            >
              {item.icon}
              {item.label}
            </Link>
          );
        })}
      </nav>

      {/* User Profile & Logout */}
      <Box sx={{ mt: 'auto', pt: 2, borderTop: '1px solid var(--border-color)', display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
        <Box>
          <Typography variant="subtitle2" fontWeight="bold" sx={{ color: 'var(--text-primary)' }}>{user?.nome}</Typography>
          <Typography variant="caption" sx={{ color: 'var(--text-secondary)' }}>{user?.role} - {user?.tenantId}</Typography>
        </Box>
        <button 
          onClick={logout}
          style={{ background: 'none', border: 'none', color: 'var(--danger)', cursor: 'pointer', padding: '0.5rem' }}
          title="Sair"
        >
          <LogOut size={20} />
        </button>
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
