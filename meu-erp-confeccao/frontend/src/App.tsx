import { BrowserRouter, Routes, Route, Link, useLocation } from 'react-router-dom';
import { LayoutDashboard, Clock, Scissors, PackageSearch, Package } from 'lucide-react';
import TabelaTempos from './pages/TabelaTempos';
import Estoque from './pages/Estoque';
import FichasTecnicas from './pages/FichasTecnicas';
import Produtos from './pages/Produtos';

// Menu Lateral Premium
const Sidebar = () => {
  const location = useLocation();
  
  const navItems = [
    { path: '/', label: 'Dashboard', icon: <LayoutDashboard size={20} /> },
    { path: '/catalog/produtos', label: 'Produtos', icon: <Package size={20} /> },
    { path: '/estoque', label: 'Estoque', icon: <PackageSearch size={20} /> },
    { path: '/producao/fichas', label: 'Fichas Técnicas', icon: <Scissors size={20} /> },
    { path: '/pcp/tempos', label: 'Tabela de Tempos', icon: <Clock size={20} /> },
  ];

  return (
    <div className="sidebar">
      <div style={{ marginBottom: '2rem' }}>
        <h1 className="text-accent" style={{ fontSize: '1.5rem', display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
          <Scissors size={24} color="var(--accent-primary)" />
          Fashion ERP
        </h1>
      </div>
      
      <nav style={{ display: 'flex', flexDirection: 'column', gap: '0.5rem' }}>
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
    </div>
  );
};

const DashboardPlaceholder = () => (
  <div className="animate-fade-in">
    <h2 style={{ marginBottom: '1rem' }}>Bem-vindo ao PCP</h2>
    <p style={{ color: 'var(--text-secondary)' }}>Utilize o menu lateral para gerenciar as Fichas Técnicas e a Tabela de Tempos Centesimais.</p>
  </div>
);

function App() {
  return (
    <BrowserRouter>
      <div className="app-container">
        <Sidebar />
        <main className="main-content">
          <Routes>
            <Route path="/" element={<DashboardPlaceholder />} />
            <Route path="/catalog/produtos" element={<Produtos />} />
            <Route path="/pcp/tempos" element={<TabelaTempos />} />
            <Route path="/estoque" element={<Estoque />} />
            <Route path="/producao/fichas" element={<FichasTecnicas />} />
          </Routes>
        </main>
      </div>
    </BrowserRouter>
  );
}

export default App;
