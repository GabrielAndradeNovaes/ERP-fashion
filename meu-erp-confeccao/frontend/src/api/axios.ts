import axios from 'axios';
import log from 'loglevel';

log.setLevel('info');

// Construct base URL based on the current hostname
const protocol = window.location.protocol;
const hostname = window.location.hostname;
const apiPort = '8088'; // The port where the backend API is running
const baseURL = `${protocol}//${hostname}:${apiPort}/api`;

const api = axios.create({
  baseURL: baseURL,
  headers: {
    'Content-Type': 'application/json'
  }
});

// Interceptor para injetar o Token e o TenantID em todas as requisições
api.interceptors.request.use(
  (config) => {
    log.info(`API Request: ${config.method?.toUpperCase()} ${config.url}`);
    const token = localStorage.getItem('@FashionERP:token');
    const userString = localStorage.getItem('@FashionERP:user');
    
    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`;
    }
    
    if (userString) {
      try {
        const user = JSON.parse(userString);
        const impersonatedTenant = localStorage.getItem('@FashionERP:impersonatedTenant');
        
        if (impersonatedTenant && user.role === 'SUPERADMIN') {
            config.headers['X-TenantID'] = impersonatedTenant;
        } else if (user && user.tenantId) {
            config.headers['X-TenantID'] = user.tenantId;
        } else {
            config.headers['X-TenantID'] = 'tenant_1'; // fallback
        }
      } catch (e) {
        config.headers['X-TenantID'] = 'tenant_1';
      }
    } else {
      config.headers['X-TenantID'] = 'tenant_1';
    }
    
    return config;
  },
  (error) => {
    log.error('API Request Error:', error);
    return Promise.reject(error);
  }
);

// Interceptor para tratamento de erros
api.interceptors.response.use(
  (response) => {
    log.info(`API Response: ${response.config.method?.toUpperCase()} ${response.config.url} - Status ${response.status}`);
    return response;
  },
  (error) => {
    log.error('API Error:', error.response?.data || error.message);
    if (error.response?.status === 401 || error.response?.status === 403) {
      // Deslogar o usuário ou redirecionar se o token expirou
      localStorage.removeItem('@FashionERP:token');
      localStorage.removeItem('@FashionERP:user');
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);

export default api;
