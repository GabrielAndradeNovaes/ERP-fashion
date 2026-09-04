package com.erp.catalog.service.impl;

import com.erp.catalog.domain.ProdutoBase;
import com.erp.catalog.domain.ProdutoSku;
import com.erp.catalog.dto.ProdutoBaseRequest;
import com.erp.catalog.dto.ProdutoBaseResponse;
import com.erp.catalog.dto.ProdutoSkuRequest;
import com.erp.catalog.repository.ProdutoBaseRepository;
import com.erp.catalog.repository.ProdutoSkuRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
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
    @Mock
    private ProdutoSkuRepository produtoSkuRepository;

    @InjectMocks
    private ProdutoServiceImpl produtoService;

    private ProdutoBase mockBase;

    @BeforeEach
    void setUp() {
        mockBase = new ProdutoBase();
        mockBase.setId(UUID.randomUUID());
        mockBase.setNome("Camiseta");
        mockBase.setSkus(new ArrayList<>());
    }

    @Test
    void shouldCreateProdutoComSkus() {
        ProdutoSkuRequest skuReq = new ProdutoSkuRequest(
                "Vermelho", "M", "123456", new BigDecimal("50.00")
        );
        ProdutoBaseRequest req = new ProdutoBaseRequest(
                "COD123", "Camiseta", "Camiseta Algodão", new BigDecimal("20.00"), new BigDecimal("10.00"),
                "Marca", "Categoria", "Colecao", "Genero", "NCM", "CEST", "Origem",
                new BigDecimal("0.5"), new BigDecimal("0.4"), "ATIVO", List.of(skuReq)
        );

        when(produtoBaseRepository.save(any())).thenReturn(mockBase);

        ProdutoBaseResponse response = produtoService.createProduto(req);

        assertNotNull(response);
        assertEquals("Camiseta", response.nome());
    }

    @Test
    void shouldUpdateProdutoESkus() {
        ProdutoSku skuExistente = new ProdutoSku();
        skuExistente.setId(UUID.randomUUID());
        skuExistente.setProdutoBase(mockBase);
        skuExistente.setCor("Azul");
        skuExistente.setTamanho("G");
        mockBase.getSkus().add(skuExistente);

        ProdutoSkuRequest skuReq = new ProdutoSkuRequest(
                "Azul", "G", "654321", new BigDecimal("60.00") // update
        );
        ProdutoBaseRequest req = new ProdutoBaseRequest(
                "COD123", "Camiseta Atualizada", "Desc", new BigDecimal("25.00"), new BigDecimal("10.00"),
                "Marca", "Categoria", "Colecao", "Genero", "NCM", "CEST", "Origem",
                new BigDecimal("0.5"), new BigDecimal("0.4"), "ATIVO", List.of(skuReq)
        );

        when(produtoBaseRepository.findById(mockBase.getId())).thenReturn(Optional.of(mockBase));
        when(produtoBaseRepository.save(any())).thenReturn(mockBase);

        ProdutoBaseResponse response = produtoService.updateProduto(mockBase.getId(), req);

        assertNotNull(response);
        assertEquals("Camiseta Atualizada", response.nome());
    }
}
