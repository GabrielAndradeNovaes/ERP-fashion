import React, { useState, useEffect } from 'react';
import { Box, Typography, Button, FormControl, InputLabel, Select, MenuItem, CircularProgress, Card, CardContent, Divider } from '@mui/material';
import { Printer, Package } from 'lucide-react';
import Barcode from 'react-barcode';
import api from '../../api/axios';
import './PrintLayout.css';

interface OrdemProducao {
  id: string;
  numero: string;
  produtoBaseNome: string;
}

interface Cupom {
  id: string;
  ordemProducaoNumero: string;
  pacoteSequencial: number;
  operacaoNome: string;
  codigoBarras: string;
  tempoTotalCentesimal: number;
  quantidadePecas: number;
  status: string;
}

const Cupons = () => {
  const [ordens, setOrdens] = useState<OrdemProducao[]>([]);
  const [selectedOp, setSelectedOp] = useState<string>('');
  const [cupons, setCupons] = useState<Cupom[]>([]);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    // Carregar ordens de produção disponíveis
    api.get('/production/ordens')
      .then(res => setOrdens(res.data))
      .catch(console.error);
  }, []);

  useEffect(() => {
    if (!selectedOp) {
      setCupons([]);
      return;
    }
    setLoading(true);
    api.get(`/production/cupons/ordem/${selectedOp}`)
      .then(res => setCupons(res.data))
      .catch(console.error)
      .finally(() => setLoading(false));
  }, [selectedOp]);

  const handlePrint = () => {
    window.print();
  };

  const groupedCupons = cupons.reduce((acc, cupom) => {
    if (!acc[cupom.pacoteSequencial]) {
      acc[cupom.pacoteSequencial] = [];
    }
    acc[cupom.pacoteSequencial].push(cupom);
    return acc;
  }, {} as Record<number, Cupom[]>);

  return (
    <Box className="animate-fade-in-up" sx={{ height: '100%', display: 'flex', flexDirection: 'column' }}>
      {/* Header and Controls - Hidden on Print */}
      <Box className="no-print" sx={{ mb: 4 }}>
        <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 4 }}>
          <Box>
            <Typography variant="h4" component="h1" gutterBottom sx={{ fontWeight: 800, color: 'var(--text-primary)', letterSpacing: '-0.5px' }}>
              Folhas de <span className="text-gradient">Pacotes</span>
            </Typography>
            <Typography variant="body1" sx={{ color: 'var(--text-secondary)' }}>
              Selecione uma Ordem de Produção para visualizar e imprimir as folhas (Formato A4).
            </Typography>
          </Box>
          <Button 
            variant="contained" 
            startIcon={<Printer size={20} />}
            onClick={handlePrint}
            disabled={cupons.length === 0}
            size="large"
            sx={{
              background: 'var(--accent-gradient)',
              borderRadius: 'var(--radius-md)',
              textTransform: 'none',
              fontWeight: 600,
              boxShadow: '0 4px 14px 0 rgba(99, 102, 241, 0.39)',
            }}
          >
            Imprimir A4
          </Button>
        </Box>

        <Card className="premium-card" sx={{ p: 3, mb: 4 }}>
          <FormControl fullWidth>
            <InputLabel>Ordem de Produção</InputLabel>
            <Select
              value={selectedOp}
              label="Ordem de Produção"
              onChange={e => setSelectedOp(e.target.value as string)}
            >
              <MenuItem value=""><em>Selecione...</em></MenuItem>
              {ordens.map(op => (
                <MenuItem key={op.id} value={op.id}>
                  {op.numero} - {op.produtoBaseNome}
                </MenuItem>
              ))}
            </Select>
          </FormControl>
        </Card>
      </Box>

      {/* Loading State - Hidden on Print */}
      {loading && (
        <Box className="no-print" sx={{ display: 'flex', justifyContent: 'center', mt: 4 }}>
          <CircularProgress sx={{ color: 'var(--accent-primary)' }} />
        </Box>
      )}

      {/* Empty State - Hidden on Print */}
      {!loading && cupons.length === 0 && selectedOp && (
        <Box className="no-print" sx={{ display: 'flex', flexDirection: 'column', alignItems: 'center', justifyContent: 'center', p: 8, color: 'var(--text-muted)' }}>
          <Package size={48} style={{ opacity: 0.5, marginBottom: 16 }} />
          <Typography variant="h6">Nenhum cupom gerado para esta OP.</Typography>
          <Typography variant="body2">Gere os pacotes físicos na tela de Ordens de Produção primeiro.</Typography>
        </Box>
      )}

      {/* Print Area - Visible on Print & Screen */}
      {cupons.length > 0 && (
        <Box id="print-area">
          <Box className="print-list" sx={{ display: 'flex', flexDirection: 'column', gap: 4 }}>
            {Object.entries(groupedCupons).map(([pacoteSeq, pacoteCupons]) => (
              <Card key={pacoteSeq} className="pacote-bloco" sx={{ p: 3, background: 'var(--bg-card)', border: '2px solid var(--border-color)', borderRadius: 'var(--radius-lg)' }}>
                {/* Cabeçalho do Pacote */}
                <Box sx={{ textAlign: 'center', mb: 3 }}>
                  <Typography variant="h5" sx={{ fontWeight: 900, textTransform: 'uppercase' }}>PACOTE Nº {pacoteSeq}</Typography>
                  <Typography variant="subtitle1" sx={{ color: 'var(--text-secondary)' }}>OP: {pacoteCupons[0]?.ordemProducaoNumero}</Typography>
                </Box>
                
                <Divider sx={{ mb: 3 }} />
                
                {/* Tabela de Operações */}
                <table className="print-table" style={{ width: '100%', borderCollapse: 'collapse' }}>
                  <thead>
                    <tr>
                      <th style={{ textAlign: 'left', padding: '12px 8px', borderBottom: '2px solid var(--border-color)' }}>Código / Operação</th>
                      <th style={{ textAlign: 'center', padding: '12px 8px', borderBottom: '2px solid var(--border-color)' }}>Qtd. Peças</th>
                      <th style={{ textAlign: 'center', padding: '12px 8px', borderBottom: '2px solid var(--border-color)' }}>Tempo Padrão</th>
                    </tr>
                  </thead>
                  <tbody>
                    {pacoteCupons.map(cupom => (
                      <tr key={cupom.id} style={{ borderBottom: '1px solid var(--border-color)' }}>
                        <td style={{ padding: '16px 8px' }}>
                          <Box sx={{ display: 'flex', alignItems: 'center', gap: 2 }}>
                            <Box sx={{ background: '#fff', padding: '4px', borderRadius: '4px' }}>
                              <Barcode 
                                value={cupom.codigoBarras} 
                                width={1.2} 
                                height={30} 
                                fontSize={10}
                                margin={0}
                                displayValue={true} 
                                background="transparent"
                              />
                            </Box>
                            <Typography variant="body1" sx={{ fontWeight: 600 }}>{cupom.operacaoNome}</Typography>
                          </Box>
                        </td>
                        <td style={{ textAlign: 'center', padding: '16px 8px' }}>
                          <Typography variant="body1" sx={{ fontWeight: 700 }}>{cupom.quantidadePecas}</Typography>
                        </td>
                        <td style={{ textAlign: 'center', padding: '16px 8px' }}>
                          <Typography variant="body1">{cupom.tempoTotalCentesimal}h</Typography>
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </Card>
            ))}
          </Box>
        </Box>
      )}
    </Box>
  );
};

export default Cupons;
