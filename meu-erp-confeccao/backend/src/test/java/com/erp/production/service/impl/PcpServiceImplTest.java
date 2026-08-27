package com.erp.production.service.impl;

import com.erp.core.domain.Funcionario;
import com.erp.core.repository.FuncionarioRepository;
import com.erp.production.domain.Apontamento;
import com.erp.production.domain.Cupom;
import com.erp.production.domain.Ocorrencia;
import com.erp.production.dto.ApontamentoRequest;
import com.erp.production.dto.OcorrenciaRequest;
import com.erp.production.dto.ProdutividadeResponse;
import com.erp.production.repository.ApontamentoRepository;
import com.erp.production.repository.CupomRepository;
import com.erp.production.repository.OcorrenciaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PcpServiceImplTest {

    @Mock
    private CupomRepository cupomRepository;
    @Mock
    private ApontamentoRepository apontamentoRepository;
    @Mock
    private OcorrenciaRepository ocorrenciaRepository;
    @Mock
    private FuncionarioRepository funcionarioRepository;

    @InjectMocks
    private PcpServiceImpl service;

    private Funcionario mockFuncionario;
    private Cupom mockCupom;

    @BeforeEach
    void setUp() {
        mockFuncionario = new Funcionario();
        mockFuncionario.setId(UUID.randomUUID());
        mockFuncionario.setNome("João");
        mockFuncionario.setCargaHorariaMensalPadrao(new BigDecimal("220.0"));

        mockCupom = new Cupom();
        mockCupom.setId(UUID.randomUUID());
        mockCupom.setCodigoBarras("1-1-1");
        mockCupom.setStatus(Cupom.Status.PENDENTE);
        mockCupom.setTempoTotalCentesimal(new BigDecimal("10.5"));
    }

    @Test
    void shouldBiparCupom() {
        ApontamentoRequest req = new ApontamentoRequest("1-1-1", mockFuncionario.getId());

        when(cupomRepository.findByCodigoBarras("1-1-1")).thenReturn(Optional.of(mockCupom));
        when(funcionarioRepository.findById(mockFuncionario.getId())).thenReturn(Optional.of(mockFuncionario));

        service.biparCupom(req);

        assertEquals(Cupom.Status.PROCESSADO, mockCupom.getStatus());
        verify(apontamentoRepository, times(1)).save(any(Apontamento.class));
        verify(cupomRepository, times(1)).save(mockCupom);
    }

    @Test
    void shouldThrowExceptionWhenBiparAlreadyProcessedCupom() {
        mockCupom.setStatus(Cupom.Status.PROCESSADO);
        ApontamentoRequest req = new ApontamentoRequest("1-1-1", mockFuncionario.getId());

        when(cupomRepository.findByCodigoBarras("1-1-1")).thenReturn(Optional.of(mockCupom));

        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> service.biparCupom(req));
        assertTrue(ex.getMessage().contains("ALERTA DE DUPLICIDADE"));
    }

    @Test
    void shouldRegistrarOcorrencia() {
        OcorrenciaRequest req = new OcorrenciaRequest(mockFuncionario.getId(), "Falta", new BigDecimal("8.0"));

        when(funcionarioRepository.findById(mockFuncionario.getId())).thenReturn(Optional.of(mockFuncionario));

        service.registrarOcorrencia(req);

        verify(ocorrenciaRepository, times(1)).save(any(Ocorrencia.class));
    }

    @Test
    void shouldCalcularProdutividade() {
        int ano = 2026;
        int mes = 8;
        
        Apontamento a1 = new Apontamento();
        a1.setCupom(mockCupom);
        
        Ocorrencia o1 = new Ocorrencia();
        o1.setTempoDescontadoCentesimal(new BigDecimal("20.0"));

        when(funcionarioRepository.findById(mockFuncionario.getId())).thenReturn(Optional.of(mockFuncionario));
        when(apontamentoRepository.findByFuncionarioAndPeriod(eq(mockFuncionario.getId()), any(), any()))
                .thenReturn(List.of(a1));
        when(ocorrenciaRepository.findByFuncionarioAndPeriod(eq(mockFuncionario.getId()), any(), any()))
                .thenReturn(List.of(o1));

        ProdutividadeResponse response = service.calcularProdutividade(mockFuncionario.getId(), ano, mes);

        assertNotNull(response);
        assertEquals(new BigDecimal("10.5"), response.tempoProduzidoCentesimal());
        assertEquals(new BigDecimal("220.0"), response.cargaHorariaMensal());
        assertEquals(new BigDecimal("20.0"), response.tempoOcorrenciasCentesimal());
        
        // base = 220 - 20 = 200
        // eficiencia = 10.5 / 200 * 100 = 5.25
        assertEquals(new BigDecimal("5.25"), response.eficienciaPercentual());
    }
}
