import React, { useEffect, useState } from 'react';
import { FormControl, InputLabel, Select, MenuItem, FormHelperText, CircularProgress } from '@mui/material';
import api from '../api/axios';

interface EmpresaSelectProps {
  value: string;
  onChange: (value: string) => void;
  required?: boolean;
  disabled?: boolean;
}

export const EmpresaSelect: React.FC<EmpresaSelectProps> = ({ value, onChange, required = true, disabled = false }) => {
  const [empresas, setEmpresas] = useState<any[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchEmpresas = async () => {
      try {
        const res = await api.get('/empresas');
        setEmpresas(res.data);
      } catch (err) {
        console.error('Erro ao buscar empresas:', err);
      } finally {
        setLoading(false);
      }
    };
    fetchEmpresas();
  }, []);

  if (loading) {
    return <CircularProgress size={24} />;
  }

  return (
    <FormControl fullWidth required={required} disabled={disabled}>
      <InputLabel>Empresa</InputLabel>
      <Select
        value={value || ''}
        label="Empresa"
        onChange={(e) => onChange(e.target.value)}
      >
        <MenuItem value="">
          <em>Selecione...</em>
        </MenuItem>
        {empresas.map((emp) => (
          <MenuItem key={emp.id} value={emp.id}>
            {emp.nomeFantasia || emp.razaoSocial}
          </MenuItem>
        ))}
      </Select>
      {!value && required && <FormHelperText>Selecione a empresa proprietária do registro</FormHelperText>}
    </FormControl>
  );
};
