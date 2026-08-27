import React, { createContext, useContext, useState, useEffect } from 'react';
import api from '../api/axios';

interface User {
  nome: string;
  email: string;
  role: string;
  tenantId: string;
  tenantStatus?: string;
  empresas: string[];
  filialPrincipalId?: string;
  permissoes?: string[];
}

interface AuthContextType {
  user: User | null;
  token: string | null;
  login: (token: string, user: User) => void;
  logout: () => void;
  isAuthenticated: boolean;
  impersonatedTenantId: string | null;
  setImpersonatedTenant: (tenantId: string | null) => void;
  hasPermission: (permissionKey?: string) => boolean;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export const AuthProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const [user, setUser] = useState<User | null>(null);
  const [token, setToken] = useState<string | null>(null);
  const [loading, setLoading] = useState(true);

  const [impersonatedTenantId, setImpersonatedTenantId] = useState<string | null>(null);

  useEffect(() => {
    // Carregar token e user do localStorage ao iniciar
    const storedToken = localStorage.getItem('@FashionERP:token');
    const storedUser = localStorage.getItem('@FashionERP:user');
    const storedImpersonated = localStorage.getItem('@FashionERP:impersonatedTenant');

    if (storedToken && storedUser) {
      setToken(storedToken);
      setUser(JSON.parse(storedUser));
    }
    if (storedImpersonated) {
      setImpersonatedTenantId(storedImpersonated);
    }
    setLoading(false);
  }, []);

  const login = (newToken: string, newUser: User) => {
    localStorage.setItem('@FashionERP:token', newToken);
    localStorage.setItem('@FashionERP:user', JSON.stringify(newUser));
    setToken(newToken);
    setUser(newUser);
    setImpersonatedTenant(null); // Reseta a personificação ao logar
  };

  const logout = () => {
    localStorage.removeItem('@FashionERP:token');
    localStorage.removeItem('@FashionERP:user');
    setToken(null);
    setUser(null);
    setImpersonatedTenant(null);
  };

  const setImpersonatedTenant = (tenantId: string | null) => {
    if (tenantId) {
      localStorage.setItem('@FashionERP:impersonatedTenant', tenantId);
    } else {
      localStorage.removeItem('@FashionERP:impersonatedTenant');
    }
    setImpersonatedTenantId(tenantId);
  };

  const hasPermission = (permissionKey?: string): boolean => {
    if (!permissionKey) return true;
    if (user?.role === 'SUPERADMIN' || user?.role === 'ADMIN') return true;
    if (user?.role === 'USER') {
      return user?.permissoes?.includes(permissionKey) ?? false;
    }
    return false;
  };

  if (loading) return null; // Ou um loading spinner global

  return (
    <AuthContext.Provider value={{ user, token, login, logout, isAuthenticated: !!token, impersonatedTenantId, setImpersonatedTenant, hasPermission }}>
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (context === undefined) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
};
