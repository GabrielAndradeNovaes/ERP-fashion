package com.erp.production.service;

import com.erp.core.domain.Empresa;
import com.erp.core.domain.Funcionario;
import com.erp.core.domain.FuncionarioJornada;
import com.erp.core.repository.FuncionarioRepository;
import com.erp.finance.service.FinanceiroService;
import com.erp.production.domain.Apontamento;
import com.erp.production.domain.ApontamentoManual;
import com.erp.production.dto.ProdutividadeResumo;
import com.erp.production.repository.ApontamentoManualRepository;
import com.erp.production.repository.ApontamentoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProdutividadeServiceTest {

    @Mock
    private ApontamentoRepository apontamentoRepository;

    @Mock
    private ApontamentoManualRepository apontamentoManualRepository;

    @Mock
    private FuncionarioRepository funcionarioRepository;

    @Mock
    private FinanceiroService financeiroService;

    @InjectMocks
    private ProdutividadeService produtividadeService;

    private Funcionario costureira;
    private UUID costureiraId;

    @BeforeEach
    void setUp() {
        costureiraId = UUID.randomUUID();
        costureira = new Funcionario();
        costureira.setId(costureiraId);
        costureira.setNome("Maria");
        costureira.setMetaMinima(BigDecimal.valueOf(100));
        costureira.setPremio100(BigDecimal.valueOf(50));
        
        FuncionarioJornada jornada = new FuncionarioJornada();
        jornada.setDiaSemana(1); // Segunda
        jornada.setEntrada(LocalTime.of(8, 0));
        jornada.setSaida(LocalTime.of(12, 0)); // 4 horas = 240 minutos
        costureira.setJornadas(List.of(jornada));
        
        Empresa empresa = new Empresa();
        empresa.setId(UUID.randomUUID());
        costureira.setEmpresa(empresa);
    }

    @Test
    void getResumo_ReturnsCorrectCalculation() {
        LocalDateTime start = LocalDateTime.of(2023, 10, 2, 0, 0); // Segunda
        LocalDateTime end = LocalDateTime.of(2023, 10, 2, 23, 59);

        when(funcionarioRepository.findByGrupoProducao()).thenReturn(List.of(costureira));
        
        ProdutividadeResumo resumoExistente = new ProdutividadeResumo(
                costureiraId, "Maria", 100L, BigDecimal.valueOf(10.5), BigDecimal.valueOf(100), BigDecimal.valueOf(50), 0);
                
        when(apontamentoRepository.getProdutividadeResumo(start, end)).thenReturn(List.of(resumoExistente));
        
        List<Object[]> manualRowList = java.util.Collections.singletonList(new Object[]{costureiraId, 120L}); // 120 min = 2.0 horas centesimais
        when(apontamentoManualRepository.getSomaMinutosNaoPagos(start, end)).thenReturn(manualRowList);

        List<ProdutividadeResumo> result = produtividadeService.getResumo(start, end);

        assertNotNull(result);
        assertEquals(1, result.size());
        ProdutividadeResumo res = result.get(0);
        assertEquals(costureiraId, res.funcionarioId());
        assertEquals(0, BigDecimal.valueOf(12.5).compareTo(res.tempoPadraoProduzido())); // 10.5 + 2.0
        assertEquals(240, res.tempoTeorico()); // 4 horas da jornada de segunda
    }
    
    @Test
    void getResumo_SemApontamentosMasComManual() {
        LocalDateTime start = LocalDateTime.of(2023, 10, 2, 0, 0); // Segunda
        LocalDateTime end = LocalDateTime.of(2023, 10, 2, 23, 59);

        when(funcionarioRepository.findByGrupoProducao()).thenReturn(List.of(costureira));
        when(apontamentoRepository.getProdutividadeResumo(start, end)).thenReturn(List.of());
        
        List<Object[]> manualRowList = java.util.Collections.singletonList(new Object[]{costureiraId, 60L}); // 60 min = 1.0 hora centesimal
        when(apontamentoManualRepository.getSomaMinutosNaoPagos(start, end)).thenReturn(manualRowList);

        List<ProdutividadeResumo> result = produtividadeService.getResumo(start, end);

        assertNotNull(result);
        assertEquals(1, result.size());
        ProdutividadeResumo res = result.get(0);
        assertEquals(0, BigDecimal.valueOf(1.0).compareTo(res.tempoPadraoProduzido()));
    }

    @Test
    void pagarProdutividade_Success() {
        LocalDateTime start = LocalDateTime.of(2023, 10, 1, 0, 0);
        LocalDateTime end = LocalDateTime.of(2023, 10, 31, 23, 59);
        BigDecimal valorPagar = BigDecimal.valueOf(250.0);

        when(funcionarioRepository.findById(costureiraId)).thenReturn(Optional.of(costureira));
        
        Apontamento apontamento = new Apontamento();
        apontamento.setPago(false);
        when(apontamentoRepository.findNaoPagosByFuncionarioAndPeriod(costureiraId, start, end))
                .thenReturn(List.of(apontamento));
                
        ApontamentoManual apontamentoManual = new ApontamentoManual();
        apontamentoManual.setPago(false);
        when(apontamentoManualRepository.findNaoPagosByFuncionarioAndPeriod(costureiraId, start, end))
                .thenReturn(List.of(apontamentoManual));

        produtividadeService.pagarProdutividade(costureiraId, start, end, valorPagar);

        assertTrue(apontamento.isPago());
        assertNotNull(apontamento.getDataPagamento());
        verify(apontamentoRepository).saveAll(anyList());
        
        assertTrue(apontamentoManual.isPago());
        assertNotNull(apontamentoManual.getDataPagamento());
        verify(apontamentoManualRepository).saveAll(anyList());
        
        verify(financeiroService).criarTituloPagamentoFuncionario(eq(costureira.getEmpresa()), eq(costureira), eq(valorPagar), anyString());
    }

    @Test
    void pagarProdutividade_NaoFazNadaSeVazio() {
        LocalDateTime start = LocalDateTime.of(2023, 10, 1, 0, 0);
        LocalDateTime end = LocalDateTime.of(2023, 10, 31, 23, 59);
        BigDecimal valorPagar = BigDecimal.valueOf(250.0);

        when(funcionarioRepository.findById(costureiraId)).thenReturn(Optional.of(costureira));
        when(apontamentoRepository.findNaoPagosByFuncionarioAndPeriod(costureiraId, start, end)).thenReturn(List.of());
        when(apontamentoManualRepository.findNaoPagosByFuncionarioAndPeriod(costureiraId, start, end)).thenReturn(List.of());

        produtividadeService.pagarProdutividade(costureiraId, start, end, valorPagar);

        verify(apontamentoRepository, never()).saveAll(anyList());
        verify(financeiroService, never()).criarTituloPagamentoFuncionario(any(), any(), any(), any());
    }
}
