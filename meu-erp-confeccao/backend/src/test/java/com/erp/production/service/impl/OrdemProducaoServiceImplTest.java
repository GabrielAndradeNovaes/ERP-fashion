package com.erp.production.service.impl;

import com.erp.catalog.domain.ProdutoBase;
import com.erp.catalog.repository.ProdutoBaseRepository;
import com.erp.inventory.domain.Material;
import com.erp.inventory.domain.TipoMovimentacao;
import com.erp.inventory.service.EstoqueMovimentacaoService;
import com.erp.production.domain.FichaTecnica;
import com.erp.production.domain.FichaTecnicaMaterial;
import com.erp.production.domain.OrdemProducao;
import com.erp.production.domain.OrdemProducaoStatus;
import com.erp.production.dto.OrdemProducaoRequest;
import com.erp.production.dto.OrdemProducaoResponse;
import com.erp.production.repository.FichaTecnicaRepository;
import com.erp.production.repository.OrdemProducaoRepository;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrdemProducaoServiceImplTest {

    @Mock
    private OrdemProducaoRepository ordemProducaoRepository;
    @Mock
    private ProdutoBaseRepository produtoBaseRepository;
    @Mock
    private FichaTecnicaRepository fichaTecnicaRepository;
    @Mock
    private EstoqueMovimentacaoService estoqueMovimentacaoService;

    @InjectMocks
    private OrdemProducaoServiceImpl service;

    private OrdemProducao mockOp;
    private ProdutoBase mockProduto;
    private FichaTecnica mockFicha;
    private OrdemProducaoRequest mockRequest;

    @BeforeEach
    void setUp() {
        mockProduto = new ProdutoBase();
        mockProduto.setId(UUID.randomUUID());
        mockProduto.setNome("Camiseta");

        mockFicha = new FichaTecnica();
        mockFicha.setId(UUID.randomUUID());
        mockFicha.setVersao("v1");
        mockFicha.setMateriais(new ArrayList<>());
        
        Material mat = new Material();
        mat.setId(UUID.randomUUID());
        
        FichaTecnicaMaterial ftm = new FichaTecnicaMaterial();
        ftm.setMaterial(mat);
        ftm.setQuantidade(new BigDecimal("1.5"));
        mockFicha.addMaterial(ftm);

        mockOp = new OrdemProducao();
        mockOp.setId(UUID.randomUUID());
        mockOp.setNumero("OP-001");
        mockOp.setProdutoBase(mockProduto);
        mockOp.setFichaTecnica(mockFicha);
        mockOp.setQuantidade(10);
        mockOp.setStatus(OrdemProducaoStatus.CADASTRADA);

        mockRequest = new OrdemProducaoRequest("OP-001", mockProduto.getId(), mockFicha.getId(), 10);
    }

    @Test
    void shouldCreateOrdemProducao() {
        when(ordemProducaoRepository.existsByNumero("OP-001")).thenReturn(false);
        when(produtoBaseRepository.findById(mockProduto.getId())).thenReturn(Optional.of(mockProduto));
        when(fichaTecnicaRepository.findById(mockFicha.getId())).thenReturn(Optional.of(mockFicha));
        when(ordemProducaoRepository.save(any(OrdemProducao.class))).thenReturn(mockOp);

        OrdemProducaoResponse response = service.criarOrdemProducao(mockRequest);

        assertNotNull(response);
        assertEquals("OP-001", response.numero());
        assertEquals(OrdemProducaoStatus.CADASTRADA, response.status());
    }

    @Test
    void shouldThrowExceptionIfOpNumberExists() {
        when(ordemProducaoRepository.existsByNumero("OP-001")).thenReturn(true);

        Exception ex = assertThrows(IllegalArgumentException.class, () -> service.criarOrdemProducao(mockRequest));
        assertTrue(ex.getMessage().contains("OP-001"));
    }

    @Test
    void shouldListAllOps() {
        when(ordemProducaoRepository.findAll()).thenReturn(List.of(mockOp));
        List<OrdemProducaoResponse> list = service.listarTodas();
        assertEquals(1, list.size());
    }

    @Test
    void shouldStartProductionAndDeductMaterials() {
        when(ordemProducaoRepository.findById(mockOp.getId())).thenReturn(Optional.of(mockOp));
        when(ordemProducaoRepository.save(any(OrdemProducao.class))).thenReturn(mockOp);

        // 10 units * 1.5 of material = 15 total needed
        OrdemProducaoResponse response = service.iniciarProducao(mockOp.getId());

        assertEquals(OrdemProducaoStatus.EM_ANDAMENTO, mockOp.getStatus());
        assertNotNull(mockOp.getDataInicio());

        // Verify inventory service was called correctly for the explosion
        verify(estoqueMovimentacaoService, times(1)).registrarMovimentacao(
                any(UUID.class),
                eq(TipoMovimentacao.SAIDA),
                eq(new BigDecimal("15.0")),
                eq("OP: OP-001")
        );
    }
    
    @Test
    void shouldThrowExceptionWhenStartingStartedOp() {
        mockOp.setStatus(OrdemProducaoStatus.EM_ANDAMENTO);
        when(ordemProducaoRepository.findById(mockOp.getId())).thenReturn(Optional.of(mockOp));
        
        assertThrows(IllegalStateException.class, () -> service.iniciarProducao(mockOp.getId()));
    }
}
