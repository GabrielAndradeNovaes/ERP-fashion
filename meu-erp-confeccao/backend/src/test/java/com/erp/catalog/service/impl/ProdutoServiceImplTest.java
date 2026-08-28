package com.erp.catalog.service.impl;

import com.erp.catalog.domain.ProdutoBase;
import com.erp.catalog.dto.ProdutoBaseRequest;
import com.erp.catalog.dto.ProdutoBaseResponse;
import com.erp.catalog.repository.ProdutoBaseRepository;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProdutoServiceImplTest {

    @Mock
    private ProdutoBaseRepository produtoBaseRepository;

    @InjectMocks
    private ProdutoServiceImpl produtoService;

    private ProdutoBase produtoBaseMock;
    private ProdutoBaseRequest requestMock;

    @BeforeEach
    void setUp() {
        produtoBaseMock = new ProdutoBase();
        produtoBaseMock.setId(UUID.randomUUID());
        produtoBaseMock.setCodigo("PROD-001");
        produtoBaseMock.setNome("Camiseta Básica");
        produtoBaseMock.setPrecoVenda(new BigDecimal("49.90"));
        produtoBaseMock.setPrecoCusto(new BigDecimal("20.00"));

        requestMock = new ProdutoBaseRequest(
                "PROD-001",
                "Camiseta Básica",
                "Camiseta 100% algodão",
                java.math.BigDecimal.TEN,
                java.math.BigDecimal.ZERO,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null
        );
    }

    @Test
    void shouldCreateProdutoSuccessfully() {
        when(produtoBaseRepository.save(any(ProdutoBase.class))).thenReturn(produtoBaseMock);

        ProdutoBaseResponse response = produtoService.createProduto(requestMock);

        assertNotNull(response);
        assertEquals("PROD-001", response.codigo());
        assertEquals("Camiseta Básica", response.nome());
        verify(produtoBaseRepository, times(1)).save(any(ProdutoBase.class));
    }

    @Test
    void shouldGetAllProdutos() {
        when(produtoBaseRepository.findAll()).thenReturn(List.of(produtoBaseMock));

        List<ProdutoBaseResponse> responses = produtoService.getAllProdutos();

        assertFalse(responses.isEmpty());
        assertEquals(1, responses.size());
        assertEquals("PROD-001", responses.get(0).codigo());
        verify(produtoBaseRepository, times(1)).findAll();
    }

    @Test
    void shouldGetProdutoById() {
        UUID id = produtoBaseMock.getId();
        when(produtoBaseRepository.findById(id)).thenReturn(Optional.of(produtoBaseMock));

        ProdutoBaseResponse response = produtoService.getProduto(id);

        assertNotNull(response);
        assertEquals(id, response.id());
        verify(produtoBaseRepository, times(1)).findById(id);
    }

    @Test
    void shouldThrowExceptionWhenGetProdutoNotFound() {
        UUID id = UUID.randomUUID();
        when(produtoBaseRepository.findById(id)).thenReturn(Optional.empty());

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            produtoService.getProduto(id);
        });

        assertEquals("Produto não encontrado", exception.getMessage());
    }

    @Test
    void shouldDeleteProdutoSuccessfully() {
        UUID id = produtoBaseMock.getId();
        when(produtoBaseRepository.findById(id)).thenReturn(Optional.of(produtoBaseMock));
        doNothing().when(produtoBaseRepository).delete(any(ProdutoBase.class));

        assertDoesNotThrow(() -> produtoService.deleteProduto(id));
        verify(produtoBaseRepository, times(1)).delete(produtoBaseMock);
    }
}
