package com.erp.inventory.service.impl;

import com.erp.inventory.domain.EstoqueMovimentacao;
import com.erp.inventory.domain.Material;
import com.erp.inventory.domain.TipoMovimentacao;
import com.erp.inventory.repository.EstoqueMovimentacaoRepository;
import com.erp.inventory.repository.MaterialRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EstoqueMovimentacaoServiceImplTest {

    @Mock
    private EstoqueMovimentacaoRepository movimentacaoRepository;

    @Mock
    private MaterialRepository materialRepository;

    @InjectMocks
    private EstoqueMovimentacaoServiceImpl service;

    private Material mockMaterial;

    @BeforeEach
    void setUp() {
        mockMaterial = new Material();
        mockMaterial.setId(UUID.randomUUID());
        mockMaterial.setNome("Tecido");
        mockMaterial.setQuantidadeAtual(new BigDecimal("10.0"));
    }

    @Test
    void shouldRegisterEntradaAndIncreaseQuantity() {
        when(materialRepository.findById(mockMaterial.getId())).thenReturn(Optional.of(mockMaterial));

        service.registrarMovimentacao(mockMaterial.getId(), TipoMovimentacao.ENTRADA, new BigDecimal("5.0"), "Doc123");

        assertEquals(0, new BigDecimal("15.0").compareTo(mockMaterial.getQuantidadeAtual()));
        verify(movimentacaoRepository, times(1)).save(any(EstoqueMovimentacao.class));
        verify(materialRepository, times(1)).save(mockMaterial);
    }

    @Test
    void shouldRegisterSaidaAndDecreaseQuantity() {
        when(materialRepository.findById(mockMaterial.getId())).thenReturn(Optional.of(mockMaterial));

        service.registrarMovimentacao(mockMaterial.getId(), TipoMovimentacao.SAIDA, new BigDecimal("2.5"), "OP-001");

        assertEquals(0, new BigDecimal("7.5").compareTo(mockMaterial.getQuantidadeAtual()));
        verify(movimentacaoRepository, times(1)).save(any(EstoqueMovimentacao.class));
        verify(materialRepository, times(1)).save(mockMaterial);
    }

    @Test
    void shouldThrowExceptionWhenQuantityIsZeroOrNegative() {
        when(materialRepository.findById(mockMaterial.getId())).thenReturn(Optional.of(mockMaterial));

        assertThrows(IllegalArgumentException.class, () ->
                service.registrarMovimentacao(mockMaterial.getId(), TipoMovimentacao.ENTRADA, BigDecimal.ZERO, "Doc")
        );
        
        assertThrows(IllegalArgumentException.class, () ->
                service.registrarMovimentacao(mockMaterial.getId(), TipoMovimentacao.ENTRADA, new BigDecimal("-1"), "Doc")
        );
    }

    @Test
    void shouldThrowExceptionWhenMaterialNotFound() {
        when(materialRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () ->
                service.registrarMovimentacao(UUID.randomUUID(), TipoMovimentacao.ENTRADA, new BigDecimal("5.0"), "Doc")
        );
    }
}
