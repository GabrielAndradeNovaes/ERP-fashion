import axios from 'axios';

const api = axios.create({
  baseURL: 'http://localhost:8088/api',
  headers: {
    'Content-Type': 'application/json',
    'X-TenantID': 'tenant_1'
  }
});

// Interceptor para tratamento de erros (opcional, pode expandir depois)
api.interceptors.response.use(
  (response) => response,
  (error) => {
    console.error('API Error:', error.response?.data || error.message);
    return Promise.reject(error);
  }
);

export default api;
