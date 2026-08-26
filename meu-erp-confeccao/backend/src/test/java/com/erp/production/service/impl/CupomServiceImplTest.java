package com.erp.production.service.impl;

import com.erp.production.domain.Cupom;
import com.erp.production.domain.FichaTecnicaOperacao;
import com.erp.production.domain.OrdemProducao;
import com.erp.production.domain.Pacote;
import com.erp.production.dto.CupomResponse;
import com.erp.production.repository.CupomRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CupomServiceImplTest {

    @Mock
    private CupomRepository cupomRepository;

    @InjectMocks
    private CupomServiceImpl service;

    private UUID ordemProducaoId;
    private Cupom mockCupom;

    @BeforeEach
    void setUp() {
        ordemProducaoId = UUID.randomUUID();

        OrdemProducao op = new OrdemProducao();
        op.setNumero("OP-001");

        Pacote pacote = new Pacote();
        pacote.setSequencial(1);
        pacote.setQuantidadePecas(30);
        pacote.setOrdemProducao(op);

        FichaTecnicaOperacao opTec = new FichaTecnicaOperacao();
        opTec.setNome("Corte");

        mockCupom = new Cupom();
        mockCupom.setId(UUID.randomUUID());
        mockCupom.setPacote(pacote);
        mockCupom.setOperacao(opTec);
        mockCupom.setCodigoBarras("1-1-1");
        mockCupom.setTempoTotalCentesimal(new BigDecimal("10.5"));
        mockCupom.setStatus(Cupom.Status.PENDENTE);
    }

    @Test
    void shouldListarPorOrdemProducao() {
        when(cupomRepository.findByOrdemProducaoId(ordemProducaoId)).thenReturn(List.of(mockCupom));

        List<CupomResponse> res = service.listarPorOrdemProducao(ordemProducaoId);

        assertEquals(1, res.size());
        CupomResponse response = res.get(0);
        assertEquals("OP-001", response.ordemProducaoNumero());
        assertEquals(1, response.pacoteSequencial());
        assertEquals("Corte", response.operacaoNome());
        assertEquals("1-1-1", response.codigoBarras());
        assertEquals(new BigDecimal("10.5"), response.tempoTotalCentesimal());
        assertEquals(30, response.quantidadePecas());
        assertEquals(Cupom.Status.PENDENTE, response.status());
    }
}
