import React from 'react';
import { Chip, CircularProgress, ChipProps } from '@mui/material';
import { CheckCircle2, AlertCircle, XCircle } from 'lucide-react';

interface StatusChipProps extends Omit<ChipProps, 'color' | 'icon' | 'label'> {
  status: string;
}

const StatusChip: React.FC<StatusChipProps> = ({ status, sx, ...props }) => {
  const getStatusColor = (s: string): ChipProps['color'] => {
    switch (s?.toUpperCase()) {
      case 'ATIVO': 
      case 'CONCLUIDO':
      case 'PAGO':
        return 'success';
      case 'INADIMPLENTE': 
      case 'PENDENTE':
      case 'ATRASADO':
      case 'PAUSADO':
        return 'warning';
      case 'CANCELADO': 
      case 'FALHA':
      case 'ERRO':
        return 'error';
      case 'CRIANDO_INFRA':
      case 'EM_ANDAMENTO':
        return 'info';
      default: 
        return 'default';
    }
  };

  const getStatusIcon = (s: string) => {
    switch (s?.toUpperCase()) {
      case 'ATIVO': 
      case 'CONCLUIDO':
      case 'PAGO':
        return <CheckCircle2 size={16} />;
      case 'INADIMPLENTE': 
      case 'ATRASADO':
      case 'PAUSADO':
        return <AlertCircle size={16} />;
      case 'CANCELADO': 
      case 'FALHA':
      case 'ERRO':
        return <XCircle size={16} />;
      case 'PENDENTE':
      case 'CRIANDO_INFRA': 
      case 'EM_ANDAMENTO':
        return <CircularProgress size={16} color="inherit" />;
      default: 
        return undefined;
    }
  };

  return (
    <Chip 
      icon={getStatusIcon(status)} 
      label={status || 'DESCONHECIDO'} 
      color={getStatusColor(status)} 
      size="small" 
      variant="outlined" 
      sx={{ fontWeight: 'bold', ...sx }}
      {...props}
    />
  );
};

export default StatusChip;
