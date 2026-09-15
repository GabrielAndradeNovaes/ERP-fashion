package com.erp.core.service;

import com.erp.catalog.repository.ProdutoBaseRepository;
import com.erp.core.dto.DashboardResumoDTO;
import com.erp.inventory.domain.Material;
import com.erp.inventory.repository.MaterialRepository;
import com.erp.production.domain.OrdemProducaoStatus;
import com.erp.production.repository.OrdemProducaoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DashboardServiceTest {

    @Mock
    private ProdutoBaseRepository produtoBaseRepository;
    @Mock
    private OrdemProducaoRepository ordemProducaoRepository;
    @Mock
    private MaterialRepository materialRepository;

    @InjectMocks
    private DashboardService service;

    @Test
    void testGetResumo() {
        when(produtoBaseRepository.count()).thenReturn(10L);
        when(ordemProducaoRepository.countByStatus(OrdemProducaoStatus.EM_ANDAMENTO)).thenReturn(3L);
        when(ordemProducaoRepository.countByStatus(OrdemProducaoStatus.FACCAO)).thenReturn(2L);
        when(ordemProducaoRepository.countByStatus(OrdemProducaoStatus.CONCLUIDA)).thenReturn(5L);

        Material m = new Material();
        m.setQuantidadeAtual(new BigDecimal("10"));
        m.setCustoUnitario(new BigDecimal("2.5"));
        when(materialRepository.findAll()).thenReturn(Collections.singletonList(m));

        java.util.Map<String, Object> result = service.getResumo();
        DashboardResumoDTO metrics = (DashboardResumoDTO) result.get("metrics");

        assertEquals(10L, metrics.getTotalProdutos());
        assertEquals(5L, metrics.getOpsEmAndamento());
        assertEquals(5L, metrics.getOpsConcluidas());
        assertEquals(25.0, metrics.getValorTotalEstoque());
    }
}
