import React, { useState } from 'react';
import { Typography, TextField, FormControl, InputLabel, Select, MenuItem, IconButton, Box } from '@mui/material';
import Grid from '@mui/material/Grid';
import { X } from 'lucide-react';
import SearchIcon from '@mui/icons-material/Search';
import PremiumCard from './PremiumCard';

export type FilterFieldType = 'text' | 'select' | 'date';

export interface FilterOption {
  value: string | number;
  label: string;
}

export interface FilterField {
  name: string;
  label: string;
  type: FilterFieldType;
  options?: FilterOption[];
  size?: number; // Tamanho md (padrão é 2)
}

interface FilterBarProps {
  fields: FilterField[];
  onSearch: (filters: Record<string, any>) => void;
  onClear: () => void;
  initialValues?: Record<string, any>;
  title?: string;
}

const FilterBar: React.FC<FilterBarProps> = ({ 
  fields, 
  onSearch, 
  onClear, 
  initialValues = {}, 
  title = "Filtros de Pesquisa" 
}) => {
  const [values, setValues] = useState<Record<string, any>>(initialValues);

  const handleChange = (name: string, value: any) => {
    setValues(prev => ({ ...prev, [name]: value }));
  };

  const handleSearch = () => {
    onSearch({ ...values });
  };

  const handleKeyDown = (e: React.KeyboardEvent) => {
    if (e.key === 'Enter') {
      e.preventDefault();
      handleSearch();
    }
  };

  const handleClear = () => {
    setValues({});
    onClear();
  };

  return (
    <PremiumCard sx={{ mb: 3 }}>
      <Box sx={{ p: 3 }}>
        <Typography variant="subtitle2" sx={{ mb: 2, color: 'var(--text-secondary)' }}>{title}</Typography>
        <Grid container spacing={2} sx={{ alignItems: 'center' }}>
          {fields.map(field => (
            <Grid size={{ xs: 12, md: field.size || 2 }} key={field.name}>
              {field.type === 'text' && (
                <TextField
                  label={field.label}
                  variant="outlined"
                  fullWidth
                  size="small"
                  value={values[field.name] || ''}
                  onChange={(e) => handleChange(field.name, e.target.value)}
                  onKeyDown={handleKeyDown}
                />
              )}
              {field.type === 'select' && (
                <FormControl fullWidth size="small">
                  <InputLabel>{field.label}</InputLabel>
                  <Select
                    value={values[field.name] || ''}
                    label={field.label}
                    onChange={(e) => handleChange(field.name, e.target.value)}
                  >
                    <MenuItem value=""><em>Todos</em></MenuItem>
                    {field.options?.map(opt => (
                      <MenuItem key={opt.value} value={opt.value}>{opt.label}</MenuItem>
                    ))}
                  </Select>
                </FormControl>
              )}
              {field.type === 'date' && (
                <TextField
                  label={field.label}
                  type="date"
                  fullWidth
                  size="small"
                  slotProps={{ inputLabel: { shrink: true } }}
                  value={values[field.name] || ''}
                  onChange={(e) => handleChange(field.name, e.target.value)}
                  onKeyDown={handleKeyDown}
                />
              )}
            </Grid>
          ))}
          
          <Grid size={{ xs: 12, md: 'auto' }} sx={{ display: 'flex', gap: 1 }}>
            <IconButton onClick={handleSearch} sx={{ bgcolor: 'var(--accent-primary)', color: 'white', '&:hover': { bgcolor: 'var(--accent-secondary)' }, width: 40, height: 40 }}>
              <SearchIcon sx={{ fontSize: 20 }} />
            </IconButton>
            <IconButton onClick={handleClear} sx={{ bgcolor: 'rgba(255,255,255,0.05)', color: 'var(--text-secondary)', '&:hover': { bgcolor: 'rgba(255,255,255,0.1)' }, width: 40, height: 40 }}>
              <X size={20} />
            </IconButton>
          </Grid>
        </Grid>
      </Box>
    </PremiumCard>
  );
};

export default FilterBar;
