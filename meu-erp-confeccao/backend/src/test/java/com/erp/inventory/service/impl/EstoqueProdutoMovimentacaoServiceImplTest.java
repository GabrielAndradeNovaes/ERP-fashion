package com.erp.inventory.service.impl;

import com.erp.catalog.domain.ProdutoSku;
import com.erp.catalog.repository.ProdutoSkuRepository;
import com.erp.inventory.domain.EstoqueProdutoMovimentacao;
import com.erp.inventory.domain.TipoMovimentacao;
import com.erp.inventory.repository.EstoqueProdutoMovimentacaoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EstoqueProdutoMovimentacaoServiceImplTest {

    @Mock
    private EstoqueProdutoMovimentacaoRepository movimentacaoRepository;

    @Mock
    private ProdutoSkuRepository skuRepository;

    @InjectMocks
    private EstoqueProdutoMovimentacaoServiceImpl service;

    private ProdutoSku mockSku;

    @BeforeEach
    void setUp() {
        mockSku = new ProdutoSku();
        mockSku.setId(UUID.randomUUID());
        mockSku.setCodigoBarras("CAM-AZUL-M");
        mockSku.setQuantidadeAtual(10);
    }

    @Test
    void shouldRegisterEntradaAndIncreaseQuantity() {
        when(skuRepository.findById(mockSku.getId())).thenReturn(Optional.of(mockSku));
        when(movimentacaoRepository.save(any(EstoqueProdutoMovimentacao.class))).thenAnswer(i -> i.getArgument(0));

        EstoqueProdutoMovimentacao result = service.registrarMovimentacao(mockSku.getId(), TipoMovimentacao.ENTRADA, 5, "OP-001");

        assertNotNull(result);
        assertEquals(15, mockSku.getQuantidadeAtual());
        assertEquals(TipoMovimentacao.ENTRADA, result.getTipo());
        assertEquals(5, result.getQuantidade());
        
        verify(skuRepository, times(1)).save(mockSku);
        verify(movimentacaoRepository, times(1)).save(any(EstoqueProdutoMovimentacao.class));
    }

    @Test
    void shouldRegisterSaidaAndDecreaseQuantity() {
        when(skuRepository.findById(mockSku.getId())).thenReturn(Optional.of(mockSku));
        when(movimentacaoRepository.save(any(EstoqueProdutoMovimentacao.class))).thenAnswer(i -> i.getArgument(0));

        EstoqueProdutoMovimentacao result = service.registrarMovimentacao(mockSku.getId(), TipoMovimentacao.SAIDA, 3, "Venda-123");

        assertNotNull(result);
        assertEquals(7, mockSku.getQuantidadeAtual()); // 10 - 3
        assertEquals(TipoMovimentacao.SAIDA, result.getTipo());
        
        verify(skuRepository, times(1)).save(mockSku);
        verify(movimentacaoRepository, times(1)).save(any(EstoqueProdutoMovimentacao.class));
    }

    @Test
    void shouldThrowExceptionWhenQuantityIsZeroOrNegative() {
        when(skuRepository.findById(mockSku.getId())).thenReturn(Optional.of(mockSku));

        assertThrows(IllegalArgumentException.class, () ->
                service.registrarMovimentacao(mockSku.getId(), TipoMovimentacao.ENTRADA, 0, "Doc")
        );
        
        assertThrows(IllegalArgumentException.class, () ->
                service.registrarMovimentacao(mockSku.getId(), TipoMovimentacao.ENTRADA, -1, "Doc")
        );
        
        assertThrows(IllegalArgumentException.class, () ->
                service.registrarMovimentacao(mockSku.getId(), TipoMovimentacao.ENTRADA, null, "Doc")
        );
    }

    @Test
    void shouldThrowExceptionWhenSkuNotFound() {
        when(skuRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () ->
                service.registrarMovimentacao(UUID.randomUUID(), TipoMovimentacao.ENTRADA, 5, "Doc")
        );
    }

    @Test
    void shouldListHistoryBySku() {
        when(movimentacaoRepository.findBySkuIdOrderByDataMovimentacaoDesc(mockSku.getId()))
                .thenReturn(List.of(new EstoqueProdutoMovimentacao()));

        List<EstoqueProdutoMovimentacao> list = service.listarHistoricoPorSku(mockSku.getId());

        assertEquals(1, list.size());
        verify(movimentacaoRepository, times(1)).findBySkuIdOrderByDataMovimentacaoDesc(mockSku.getId());
    }
}
