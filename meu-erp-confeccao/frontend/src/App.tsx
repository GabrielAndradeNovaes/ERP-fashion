import { BrowserRouter, Routes, Route, Link, useLocation, Navigate } from 'react-router-dom';
import { LayoutDashboard, Clock, Scissors, PackageSearch, Package, ClipboardList, LogOut, Sun, Moon, Building2 } from 'lucide-react';
import { Box, Typography } from '@mui/material';
import Estoque from './pages/Estoque';
import Produtos from './pages/Produtos';
import OrdensProducao from './pages/OrdensProducao';
import Cupons from './pages/PCP/Cupons';
import Bipagem from './pages/PCP/Bipagem';
import Funcionarios from './pages/PCP/Funcionarios';
import Produtividade from './pages/PCP/Produtividade';
import Faccoes from './pages/PCP/Faccoes';
import Clientes from './pages/Clientes';
import Fornecedores from './pages/Fornecedores';
import Categorias from './pages/Categorias';
import CadastrosAuxiliares from './pages/CadastrosAuxiliares';
import Login from './pages/Login';
import Dashboard from './pages/Dashboard';
import { Users, Truck, Tags, Ruler, ScanLine, UserCog, BarChart, FileText, Tag } from 'lucide-react';
import { AuthProvider, useAuth } from './contexts/AuthContext';
import { useThemeContext } from './contexts/ThemeContext';
import Empresas from './pages/Empresas';
import Usuarios from './pages/Usuarios';
import PaymentPending from './pages/PaymentPending';
import TenantsList from './pages/Backoffice/TenantsList';
import AdminDashboard from './pages/Admin/AdminDashboard';
import LandingPage from './pages/LandingPage';

// Rotas Protegidas
const PrivateRoute = ({ children, requireSuperAdmin = false, requiredPermission }: { children: React.ReactNode, requireSuperAdmin?: boolean, requiredPermission?: string }) => {
  const { isAuthenticated, user, hasPermission } = useAuth();
  
  if (!isAuthenticated) return <Navigate to="/login" />;

  // Se o tenant estiver inadimplente, trava o acesso geral e mostra a tela de pendência
  if (user?.tenantStatus === 'INADIMPLENTE' && !requireSuperAdmin) {
    return <PaymentPending />;
  }

  // Futura validação de rotas admin
  if (requireSuperAdmin && user?.role !== 'SUPERADMIN') {
    return <Navigate to="/" />; // ou uma tela de "Não Autorizado"
  }

  // Validação granular
  if (requiredPermission && !hasPermission(requiredPermission)) {
    return <Navigate to="/" />; // ou tela de Não Autorizado
  }

  return <>{children}</>;
};

// Menu Lateral Premium
const Sidebar = () => {
  const location = useLocation();
  const { user, logout, impersonatedTenantId, setImpersonatedTenant, hasPermission } = useAuth();
  const { mode, toggleTheme } = useThemeContext();
  
  const navGroups = [
    {
      title: 'Geral',
      module: 'CORE',
      items: [
        { path: '/', label: 'Dashboard', icon: <LayoutDashboard size={20} /> },
      ]
    },
    {
      title: 'Produção (PCP)',
      module: 'PCP',
      items: [
        { path: '/pcp/ordens', label: 'Ordens de Produção', icon: <ClipboardList size={20} />, perm: 'PCP_VIEW' },
        { path: '/pcp/faccoes', label: 'Gestão de Facções', icon: <Truck size={20} />, perm: 'PCP_VIEW' },
        { path: '/pcp/cupons', label: 'Cupons / Etiquetas', icon: <FileText size={20} />, perm: 'PCP_VIEW' },
        { path: '/pcp/bipagem', label: 'Bipagem Rápida', icon: <ScanLine size={20} />, perm: 'PCP_VIEW' },
        { path: '/pcp/produtividade', label: 'Produtividade', icon: <BarChart size={20} />, perm: 'PCP_VIEW' },
        { path: '/pcp/funcionarios', label: 'Funcionários (PCP)', icon: <UserCog size={20} />, perm: 'PCP_VIEW' },
      ]
    },
    {
      title: 'Inventário e Engenharia',
      module: 'ESTOQUE',
      items: [
        { path: '/catalog/produtos', label: 'Produtos e Fichas', icon: <Package size={20} />, perm: 'PRODUTOS_VIEW' },
        { path: '/estoque', label: 'Estoque', icon: <PackageSearch size={20} />, perm: 'ESTOQUE_VIEW' },
      ]
    },
    {
      title: 'Cadastros Base',
      module: 'CORE',
      items: [
        { path: '/core/cadastros-auxiliares', label: 'Cores e Tamanhos', icon: <Tag size={20} />, perm: 'PRODUTOS_VIEW' },
        { path: '/core/empresas', label: 'Empresas/Filiais', icon: <Building2 size={20} />, perm: 'USUARIOS_ADMIN' },
        { path: '/core/usuarios', label: 'Usuários', icon: <UserCog size={20} />, perm: 'USUARIOS_ADMIN' },
        { path: '/core/clientes', label: 'Clientes', icon: <Users size={20} />, perm: 'CLIENTES_VIEW' },
        { path: '/core/fornecedores', label: 'Fornecedores', icon: <Truck size={20} />, perm: 'CLIENTES_VIEW' },
        { path: '/core/categorias', label: 'Categorias', icon: <Tags size={20} />, perm: 'PRODUTOS_VIEW' },
      ]
    }
  ];

  const { hasModule } = useAuth();

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
      <div style={{ marginBottom: '1rem', padding: '0 1rem' }}>
        <h1 className="text-gradient" style={{ fontSize: '1.5rem', display: 'flex', alignItems: 'center', gap: '0.75rem', fontWeight: 800 }}>
          <div style={{ background: 'var(--accent-gradient)', padding: '6px', borderRadius: '8px', display: 'flex' }}>
            <Scissors size={20} color="white" />
          </div>
          Fashion ERP
        </h1>
      </div>

      {impersonatedTenantId && user?.role === 'SUPERADMIN' && (
        <Box sx={{ p: 2, mb: 2, bgcolor: 'rgba(239, 68, 68, 0.1)', borderRadius: 2, border: '1px solid var(--danger)' }}>
          <Typography variant="body2" sx={{ color: 'var(--danger)', fontWeight: 'bold', mb: 0.5 }}>
            Modo Suporte
          </Typography>
          <Typography variant="caption" display="block" sx={{ mb: 1, color: 'var(--text-secondary)' }}>
            Acessando: {impersonatedTenantId}
          </Typography>
          <button 
            onClick={() => { setImpersonatedTenant(null); window.location.href = '/admin/tenants'; }}
            style={{ width: '100%', background: 'var(--danger)', color: 'white', border: 'none', padding: '6px', borderRadius: '4px', cursor: 'pointer', fontWeight: 'bold', fontSize: '12px' }}
          >
            Voltar ao Master
          </button>
        </Box>
      )}
      
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

        {navGroups.map((group, index) => {
          if (group.module && !hasModule(group.module)) return null;

          const visibleItems = group.items.filter(item => hasPermission(item.perm));
          if (visibleItems.length === 0) return null;

          return (
            <Box key={index} sx={{ display: 'flex', flexDirection: 'column', gap: '0.25rem' }}>
              <Typography variant="overline" sx={{ px: 2, color: 'var(--text-muted)', fontWeight: 700, letterSpacing: '0.5px' }}>
                {group.title}
              </Typography>
              {visibleItems.map(item => {
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
          <Route path="/core/empresas" element={<PrivateRoute requiredPermission="USUARIOS_ADMIN"><Empresas /></PrivateRoute>} />
          <Route path="/core/usuarios" element={<PrivateRoute requiredPermission="USUARIOS_ADMIN"><Usuarios /></PrivateRoute>} />
          <Route path="/core/cadastros-auxiliares" element={<PrivateRoute requiredPermission="PRODUTOS_VIEW"><CadastrosAuxiliares /></PrivateRoute>} />
          <Route path="/core/clientes" element={<PrivateRoute requiredPermission="CLIENTES_VIEW"><Clientes /></PrivateRoute>} />
          <Route path="/core/fornecedores" element={<PrivateRoute requiredPermission="CLIENTES_VIEW"><Fornecedores /></PrivateRoute>} />
          <Route path="/core/categorias" element={<PrivateRoute requiredPermission="PRODUTOS_VIEW"><Categorias /></PrivateRoute>} />
          <Route path="/catalog/produtos" element={<PrivateRoute requiredPermission="PRODUTOS_VIEW"><Produtos /></PrivateRoute>} />
          <Route path="/estoque" element={<PrivateRoute requiredPermission="ESTOQUE_VIEW"><Estoque /></PrivateRoute>} />
          <Route path="/pcp/ordens" element={<PrivateRoute requiredPermission="PCP_VIEW"><OrdensProducao /></PrivateRoute>} />
          <Route path="/pcp/faccoes" element={<PrivateRoute requiredPermission="PCP_VIEW"><Faccoes /></PrivateRoute>} />
          <Route path="/pcp/cupons" element={<PrivateRoute requiredPermission="PCP_VIEW"><Cupons /></PrivateRoute>} />
          <Route path="/pcp/bipagem" element={<PrivateRoute requiredPermission="PCP_VIEW"><Bipagem /></PrivateRoute>} />
          <Route path="/pcp/funcionarios" element={<PrivateRoute requiredPermission="PCP_VIEW"><Funcionarios /></PrivateRoute>} />
          <Route path="/pcp/produtividade" element={<PrivateRoute requiredPermission="PCP_VIEW"><Produtividade /></PrivateRoute>} />
          
          <Route path="/admin/tenants" element={<PrivateRoute requireSuperAdmin><TenantsList /></PrivateRoute>} />
        </Routes>
      </main>
    </div>
  );
};

import AdminBilling from './pages/Admin/AdminBilling';
import AdminSettings from './pages/Admin/AdminSettings';
import { CreditCard, Settings } from 'lucide-react';

const AdminSidebar = () => {
  const location = useLocation();
  const { user, logout } = useAuth();
  const { mode, toggleTheme } = useThemeContext();
  
  return (
    <div className="sidebar premium-card" style={{ 
      display: 'flex', flexDirection: 'column', height: '100vh',
      background: 'var(--bg-card)', borderRight: '1px solid var(--border-color)',
      padding: '24px 16px', width: 'var(--sidebar-width)', boxShadow: '4px 0 24px rgba(0,0,0,0.2)'
    }}>
      <div style={{ marginBottom: '1rem', padding: '0 1rem' }}>
        <h1 className="text-gradient" style={{ fontSize: '1.5rem', display: 'flex', alignItems: 'center', gap: '0.75rem', fontWeight: 800 }}>
          <Building2 size={20} color="var(--accent-primary)" />
          Fashion ERP <br/><small style={{fontSize: '10px'}}>Master Admin</small>
        </h1>
      </div>
      
      <nav style={{ display: 'flex', flexDirection: 'column', gap: '1rem', flex: 1, overflowY: 'auto', paddingBottom: '1rem', marginTop: '1rem' }}>
        <Link 
          to="/"
          style={{
            display: 'flex', alignItems: 'center', gap: '1rem', padding: '0.75rem 1rem',
            borderRadius: 'var(--radius-md)',
            color: location.pathname === '/' ? 'white' : 'var(--text-secondary)',
            background: location.pathname === '/' ? 'var(--accent-gradient)' : 'transparent',
            fontWeight: location.pathname === '/' ? 600 : 500,
            textDecoration: 'none'
          }}
        >
          <LayoutDashboard size={20} />
          Painel Master
        </Link>
        <Link 
          to="/tenants"
          style={{
            display: 'flex', alignItems: 'center', gap: '1rem', padding: '0.75rem 1rem',
            borderRadius: 'var(--radius-md)',
            color: location.pathname === '/tenants' ? 'white' : 'var(--text-secondary)',
            background: location.pathname === '/tenants' ? 'var(--accent-gradient)' : 'transparent',
            fontWeight: location.pathname === '/tenants' ? 600 : 500,
            textDecoration: 'none'
          }}
        >
          <Building2 size={20} />
          Gestão de Tenants
        </Link>
        <Link 
          to="/billing"
          style={{
            display: 'flex', alignItems: 'center', gap: '1rem', padding: '0.75rem 1rem',
            borderRadius: 'var(--radius-md)',
            color: location.pathname === '/billing' ? 'white' : 'var(--text-secondary)',
            background: location.pathname === '/billing' ? 'var(--accent-gradient)' : 'transparent',
            fontWeight: location.pathname === '/billing' ? 600 : 500,
            textDecoration: 'none'
          }}
        >
          <CreditCard size={20} />
          Faturamento Global
        </Link>
        <Link 
          to="/settings"
          style={{
            display: 'flex', alignItems: 'center', gap: '1rem', padding: '0.75rem 1rem',
            borderRadius: 'var(--radius-md)',
            color: location.pathname === '/settings' ? 'white' : 'var(--text-secondary)',
            background: location.pathname === '/settings' ? 'var(--accent-gradient)' : 'transparent',
            fontWeight: location.pathname === '/settings' ? 600 : 500,
            textDecoration: 'none'
          }}
        >
          <Settings size={20} />
          Configurações
        </Link>
      </nav>

      {/* User Profile & Logout */}
      <Box sx={{ mt: 'auto', pt: 2, borderTop: '1px solid var(--border-color)', display: 'flex', alignItems: 'center', justifyContent: 'space-between', padding: '1rem 0.5rem 0' }}>
        <Box>
          <Typography variant="subtitle2" sx={{ fontWeight: 700, color: 'var(--text-primary)' }}>{user?.nome}</Typography>
          <Typography variant="caption" sx={{ color: 'var(--accent-primary)', fontWeight: 600 }}>SUPERADMIN</Typography>
        </Box>
        <Box sx={{ display: 'flex', gap: 1 }}>
          <button onClick={toggleTheme} style={{ background: 'rgba(99, 102, 241, 0.1)', border: 'none', color: 'var(--accent-primary)', cursor: 'pointer', padding: '0.5rem', borderRadius: 'var(--radius-sm)', display: 'flex' }}><Sun size={18} /></button>
          <button onClick={logout} style={{ background: 'rgba(239, 68, 68, 0.1)', border: 'none', color: 'var(--danger)', cursor: 'pointer', padding: '0.5rem', borderRadius: 'var(--radius-sm)', display: 'flex' }}><LogOut size={18} /></button>
        </Box>
      </Box>
    </div>
  );
};

const AdminApp = () => {
  return (
    <div className="app-container">
      <AdminSidebar />
      <main className="main-content">
        <Routes>
          <Route path="/" element={<PrivateRoute requireSuperAdmin><AdminDashboard /></PrivateRoute>} />
          <Route path="/tenants" element={<PrivateRoute requireSuperAdmin><TenantsList /></PrivateRoute>} />
          <Route path="/billing" element={<PrivateRoute requireSuperAdmin><AdminBilling /></PrivateRoute>} />
          <Route path="/settings" element={<PrivateRoute requireSuperAdmin><AdminSettings /></PrivateRoute>} />
          <Route path="*" element={<Navigate to="/" />} />
        </Routes>
      </main>
    </div>
  );
};

const AppRouter = () => {
  const hostname = window.location.hostname;
  const subdomain = hostname.split('.')[0];

  if (subdomain === 'www') {
    return (
      <Routes>
        <Route path="*" element={<LandingPage />} />
      </Routes>
    );
  }

  if (subdomain === 'admin') {
    return (
      <Routes>
        <Route path="/login" element={<Login />} />
        <Route path="/*" element={<AdminApp />} />
      </Routes>
    );
  }

  return (
    <Routes>
      <Route path="/login" element={<Login />} />
      <Route path="/*" element={<MainApp />} />
    </Routes>
  );
};

function App() {
  return (
    <BrowserRouter>
      <AuthProvider>
        <AppRouter />
      </AuthProvider>
    </BrowserRouter>
  );
}

export default App;
