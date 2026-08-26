import axios from 'axios';

const api = axios.create({
  baseURL: 'http://localhost:8088/api',
  headers: {
    'Content-Type': 'application/json'
  }
});

// Interceptor para injetar o Token e o TenantID em todas as requisições
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('@FashionERP:token');
    const userString = localStorage.getItem('@FashionERP:user');
    
    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`;
    }
    
    if (userString) {
      try {
        const user = JSON.parse(userString);
        if (user && user.tenantId) {
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
    return Promise.reject(error);
  }
);

// Interceptor para tratamento de erros
api.interceptors.response.use(
  (response) => response,
  (error) => {
    console.error('API Error:', error.response?.data || error.message);
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
