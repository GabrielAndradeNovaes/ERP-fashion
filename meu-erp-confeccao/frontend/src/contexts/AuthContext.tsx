import React, { createContext, useContext, useState, useEffect } from 'react';
import api from '../api/axios';

interface User {
  nome: string;
  email: string;
  role: string;
  tenantId: string;
  tenantStatus?: string;
}

interface AuthContextType {
  user: User | null;
  token: string | null;
  login: (token: string, user: User) => void;
  logout: () => void;
  isAuthenticated: boolean;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export const AuthProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const [user, setUser] = useState<User | null>(null);
  const [token, setToken] = useState<string | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    // Carregar token e user do localStorage ao iniciar
    const storedToken = localStorage.getItem('@FashionERP:token');
    const storedUser = localStorage.getItem('@FashionERP:user');

    if (storedToken && storedUser) {
      setToken(storedToken);
      setUser(JSON.parse(storedUser));
    }
    setLoading(false);
  }, []);

  const login = (newToken: string, newUser: User) => {
    localStorage.setItem('@FashionERP:token', newToken);
    localStorage.setItem('@FashionERP:user', JSON.stringify(newUser));
    setToken(newToken);
    setUser(newUser);
  };

  const logout = () => {
    localStorage.removeItem('@FashionERP:token');
    localStorage.removeItem('@FashionERP:user');
    setToken(null);
    setUser(null);
  };

  if (loading) return null; // Ou um loading spinner global

  return (
    <AuthContext.Provider value={{ user, token, login, logout, isAuthenticated: !!token }}>
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
