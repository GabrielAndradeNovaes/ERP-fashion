import React, { useState, useEffect } from 'react';
import { Box, Typography, Button, FormControl, InputLabel, Select, MenuItem, CircularProgress, Card, Divider, Stack } from '@mui/material';
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
  pacoteCodigoBarras: string;
  produtoNome: string;
}

const Cupons = () => {
  const [ordens, setOrdens] = useState<OrdemProducao[]>([]);
  const [selectedOp, setSelectedOp] = useState<string>('');
  const [cupons, setCupons] = useState<Cupom[]>([]);
  const [loading, setLoading] = useState(false);
  const [layout, setLayout] = useState<'A4' | 'THERMAL'>('A4');

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
      
      {/* Injeta CSS dinâmico para o formato da página de impressão baseado no layout selecionado */}
      {layout === 'A4' && (
        <style type="text/css" media="print">
          {`@page { size: A4; margin: 10mm; }`}
        </style>
      )}
      {layout === 'THERMAL' && (
        <style type="text/css" media="print">
          {`@page { size: 100mm 50mm; margin: 0mm; }`}
        </style>
      )}

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
              Imprimir {layout === 'A4' ? 'A4' : 'Térmica'}
            </Button>
        </Box>

        <Card className="premium-card" sx={{ p: 3, mb: 4 }}>
          <Stack direction={{ xs: 'column', md: 'row' }} spacing={3}>
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

          <FormControl fullWidth>
            <InputLabel>Formato de Impressão</InputLabel>
            <Select
              value={layout}
              label="Formato de Impressão"
              onChange={e => setLayout(e.target.value as 'A4' | 'THERMAL')}
            >
              <MenuItem value="A4">Folha A4 (Comum)</MenuItem>
              <MenuItem value="THERMAL">Etiqueta Térmica (10cm x 5cm)</MenuItem>
            </Select>
          </FormControl>
        </Stack>
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
          {layout === 'A4' ? (
            <Box className="print-list" sx={{ display: 'grid', gridTemplateColumns: '1fr', gap: 4 }}>
              {Object.entries(groupedCupons).map(([pacoteSeq, pacoteCupons]) => {
                const totalPacoteTempo = pacoteCupons.reduce((acc, c) => acc + (c.tempoTotalCentesimal || 0), 0);
                const qtdPecas = pacoteCupons[0]?.quantidadePecas || 0;
                const produtoNome = pacoteCupons[0]?.produtoNome || '';
                const pacoteBarcode = pacoteCupons[0]?.pacoteCodigoBarras || '';
                
                return (
                <Card key={pacoteSeq} className="pacote-bloco" sx={{ p: 3, background: 'var(--bg-card)', border: '2px solid var(--border-color)', borderRadius: 'var(--radius-lg)' }}>
                  {/* Cabeçalho do Pacote */}
                  <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', mb: 2 }}>
                    <Box>
                      <Typography variant="h5" sx={{ fontWeight: 900, textTransform: 'uppercase' }}>PACOTE Nº {pacoteSeq}</Typography>
                      <Typography variant="subtitle1" sx={{ color: 'var(--text-secondary)' }}>
                        <strong>OP:</strong> {pacoteCupons[0]?.ordemProducaoNumero} &nbsp;|&nbsp; 
                        <strong>Produto:</strong> {produtoNome}
                      </Typography>
                      <Typography variant="subtitle2" sx={{ color: 'var(--text-secondary)' }}>
                        <strong>Peças:</strong> {qtdPecas} un &nbsp;|&nbsp; 
                        <strong>Tempo Total:</strong> {totalPacoteTempo.toFixed(2)}h
                      </Typography>
                    </Box>
                    {/* Código de barras do pacote (Entrada Estoque) */}
                    <Box sx={{ textAlign: 'center', background: '#fff', p: 1, borderRadius: 1, border: '1px dashed #ccc' }}>
                      <Typography variant="caption" sx={{ fontWeight: 'bold', color: '#000' }}>ENTRADA DE ESTOQUE</Typography>
                      <Barcode 
                        value={pacoteBarcode || '000'} 
                        width={1.5} 
                        height={40} 
                        fontSize={12}
                        margin={0}
                        displayValue={true} 
                        background="transparent"
                      />
                    </Box>
                  </Box>
                  
                  <Divider sx={{ mb: 2 }} />
                  <Typography variant="subtitle2" sx={{ mb: 2, fontWeight: 700, textTransform: 'uppercase' }}>Cupons de Operação (Costureira)</Typography>
                  
                  {/* Grid de Operações (2 colunas) */}
                  <Box sx={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 2 }}>
                    {pacoteCupons.map(cupom => (
                      <Box key={cupom.id} sx={{ display: 'flex', alignItems: 'center', p: 1.5, border: '1px solid var(--border-color)', borderRadius: 1, pageBreakInside: 'avoid', breakInside: 'avoid' }}>
                         <Box sx={{ background: '#fff', padding: '4px', borderRadius: '4px', mr: 2 }}>
                           <Barcode 
                             value={cupom.codigoBarras || '000'} 
                             width={1} 
                             height={25} 
                             fontSize={9}
                             margin={0}
                             displayValue={true} 
                             background="transparent"
                           />
                         </Box>
                         <Box>
                           <Typography variant="body2" sx={{ fontWeight: 700 }}>{cupom.operacaoNome}</Typography>
                           <Typography variant="caption" sx={{ color: 'var(--text-secondary)' }}>Tempo: {cupom.tempoTotalCentesimal}h</Typography>
                         </Box>
                      </Box>
                    ))}
                  </Box>
                </Card>
              )})}
            </Box>
          ) : (
            <Box className="print-list" sx={{ display: 'flex', flexWrap: 'wrap', gap: 2 }}>
              {cupons.map(cupom => (
                <Card 
                  key={cupom.id} 
                  className="thermal-label" 
                  sx={{ 
                    width: '100mm', 
                    height: '50mm', 
                    p: 2, 
                    boxSizing: 'border-box', 
                    border: '1px dashed var(--border-color)', 
                    display: 'flex', 
                    flexDirection: 'column', 
                    justifyContent: 'center', 
                    alignItems: 'center',
                    background: '#fff', /* always white for barcode scanners */
                    color: '#000',
                    margin: 'auto'
                  }}
                >
                   <Typography variant="body2" sx={{ fontWeight: 800, fontSize: '14px', textTransform: 'uppercase' }}>
                     OP: {cupom.ordemProducaoNumero} - Pct: {cupom.pacoteSequencial}
                   </Typography>
                   <Typography variant="body2" sx={{ fontSize: '12px', mb: 1, textAlign: 'center', whiteSpace: 'nowrap', overflow: 'hidden', textOverflow: 'ellipsis', maxWidth: '100%' }}>
                     {cupom.operacaoNome} ({cupom.quantidadePecas} un)
                   </Typography>
                   <Box sx={{ flexGrow: 1, display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
                     <Barcode 
                        value={cupom.codigoBarras || '000'} 
                        width={1.8} 
                        height={50} 
                        fontSize={12}
                        margin={0}
                        displayValue={true} 
                        background="transparent"
                      />
                   </Box>
                </Card>
              ))}
            </Box>
          )}
        </Box>
      )}
    </Box>
  );
};

export default Cupons;
